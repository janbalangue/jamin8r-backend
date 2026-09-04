CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_app_user_username UNIQUE (username)
);

CREATE TABLE profile (
    user_id UUID PRIMARY KEY,
    display_name VARCHAR(80) NOT NULL,
    bio VARCHAR(500),
    location VARCHAR(120),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE TABLE follow_edge (
    id UUID PRIMARY KEY,
    follower_id UUID NOT NULL,
    followed_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_follow_not_self CHECK (follower_id <> followed_id),
    CONSTRAINT uk_follow_pair UNIQUE (follower_id, followed_id),
    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_followed FOREIGN KEY (followed_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE INDEX idx_follow_follower ON follow_edge(follower_id, created_at);
CREATE INDEX idx_follow_followed ON follow_edge(followed_id, created_at);

CREATE TABLE outbox_event (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(120) NOT NULL,
    event_version INTEGER NOT NULL,
    aggregate_id UUID NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    correlation_id UUID NOT NULL,
    causation_id UUID,
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    attempts INTEGER NOT NULL DEFAULT 0,
    last_error VARCHAR(1000)
);

CREATE INDEX idx_outbox_pending ON outbox_event(published_at, created_at);
