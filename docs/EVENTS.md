# Integration events

## Contract

Every durable integration event carries:

- `eventId`: globally unique delivery identity.
- `eventType`: stable dot-separated contract name.
- `eventVersion`: positive integer schema version.
- `aggregateId`: primary domain object involved.
- `occurredAt`: UTC instant at which the business fact occurred.
- `correlationId`: request/workflow correlation identifier.
- `causationId`: identifier of the event/command that caused this event when known; nullable at the edge.
- `payload`: JSON payload owned by the publishing module.

The outbox row also carries publication attempts, last error, creation time, and publication time.

## 0.1.0 event contracts

### `user.registered` / version 1

```json
{
  "userId": "uuid",
  "username": "jay"
}
```

### `profile.updated` / version 1

```json
{
  "userId": "uuid",
  "displayName": "Jay",
  "bio": "...",
  "location": "..."
}
```

### `social.user-followed` / version 1

```json
{
  "followerId": "uuid",
  "followedId": "uuid"
}
```

## Compatibility rule

Never silently mutate an existing event contract in an incompatible way. Add optional fields compatibly when safe; otherwise publish a new event version.
