# HTTP API — 0.1.0

The HTTP surface is deliberately small. Authentication is not present in 0.1.0; see `SECURITY.md` before exposing this service publicly.

## Correlation

Write endpoints accept an optional `X-Correlation-Id` UUID. If omitted, the backend creates one for the durable integration event.

## Users

### `POST /v1/users`

```json
{
  "username": "jay"
}
```

Returns `201 Created` with the canonical lower-case username and generated UUID. Usernames are 3–32 characters and may contain lowercase letters, numbers, and underscore. Input is normalized to lowercase before uniqueness enforcement.

### `GET /v1/users/{userId}`

Returns the user identity record or `404 user_not_found`.

## Profiles

### `PUT /v1/users/{userId}/profile`

Creates or replaces the current profile fields:

```json
{
  "displayName": "Jay",
  "bio": "music + systems",
  "location": "California"
}
```

`displayName` is required (max 80); `bio` max 500; `location` max 120.

### `GET /v1/users/{userId}/profile`

Returns the profile or `404 profile_not_found`.

## Social follows

### `POST /v1/follows`

```json
{
  "followerId": "uuid",
  "followedId": "uuid"
}
```

Returns `202 Accepted`:

```json
{
  "created": true
}
```

A duplicate request is idempotent and returns `created: false`. Self-follow returns `400 self_follow`. Both users must exist.

### `GET /v1/users/{userId}/following`

Returns:

```json
{
  "userIds": ["uuid"]
}
```

### `GET /v1/users/{userId}/followers`

Same response shape, containing follower IDs.

## Error shape

```json
{
  "timestamp": "2026-09-04T18:00:00Z",
  "status": 400,
  "code": "invalid_request",
  "message": "...",
  "fields": {}
}
```

Validation failures use `validation_failed` and populate `fields`.
