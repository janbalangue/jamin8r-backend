package com.jamin8r.eventing;

public interface OutboxWriter {
    void append(IntegrationEvent event);
}
