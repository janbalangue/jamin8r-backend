package com.jamin8r;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(
    statements = {
        "DELETE FROM outbox_event",
        "DELETE FROM follow_edge",
        "DELETE FROM profile",
        "DELETE FROM app_user"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class Jamin8rApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired JsonMapper json;
    @Autowired JdbcTemplate jdbc;

    @Test
    void registrationProfileAndFollowCreateDurableOutboxEvents() throws Exception {
        UUID jay = createUser("Jay");
        UUID alice = createUser("alice");

        mvc.perform(put("/v1/users/{id}/profile", jay)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"displayName":"Jay","bio":"music + systems","location":"California"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Jay"));

        mvc.perform(post("/v1/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Correlation-Id", UUID.randomUUID().toString())
                        .content("""
                                {"followerId":"%s","followedId":"%s"}
                                """.formatted(jay, alice)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.created").value(true));

        mvc.perform(post("/v1/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"followerId":"%s","followedId":"%s"}
                                """.formatted(jay, alice)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.created").value(false));

        mvc.perform(get("/v1/users/{id}/following", jay))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userIds[0]").value(alice.toString()));

        Integer outboxCount = jdbc.queryForObject("select count(*) from outbox_event", Integer.class);
        Integer followCount = jdbc.queryForObject("select count(*) from follow_edge", Integer.class);
        assertThat(outboxCount).isEqualTo(4); // 2 registrations + profile update + first follow
        assertThat(followCount).isEqualTo(1);
    }

    @Test
    void rejectsDuplicateUsernameAndSelfFollow() throws Exception {
        UUID jay = createUser("jay");

        mvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"JAY\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("username_taken"));

        mvc.perform(post("/v1/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"followerId":"%s","followedId":"%s"}
                                """.formatted(jay, jay)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("self_follow"));
    }

    @Test
    void validatesUsernameAndCorrelationId() throws Exception {
        mvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation_failed"));

        mvc.perform(post("/v1/users")
                        .header("X-Correlation-Id", "not-a-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"valid_name\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("invalid_request"));
    }

    private UUID createUser(String username) throws Exception {
        String body = mvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\"}".formatted(username)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(json.readTree(body).get("id").asString());
    }
}
