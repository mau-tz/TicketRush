CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE events (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    venue       VARCHAR(255)   NOT NULL,
    starts_at   TIMESTAMPTZ    NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE TABLE seats (
    id             UUID PRIMARY KEY,
    event_id       UUID        NOT NULL REFERENCES events (id),
    label          VARCHAR(20) NOT NULL,
    status         VARCHAR(20) NOT NULL,
    reservation_id UUID,
    version        BIGINT,
    UNIQUE (event_id, label)
);
CREATE INDEX idx_seats_event_status ON seats (event_id, status);
CREATE INDEX idx_seats_reservation ON seats (reservation_id);

CREATE TABLE reservations (
    id         UUID PRIMARY KEY,
    user_id    UUID           NOT NULL REFERENCES users (id),
    event_id   UUID           NOT NULL REFERENCES events (id),
    status     VARCHAR(20)    NOT NULL,
    total      NUMERIC(10, 2) NOT NULL,
    expires_at TIMESTAMPTZ    NOT NULL,
    created_at TIMESTAMPTZ    NOT NULL
);
CREATE INDEX idx_reservations_pending_expiry ON reservations (status, expires_at);

CREATE TABLE reservation_seats (
    reservation_id UUID NOT NULL REFERENCES reservations (id),
    seat_id        UUID NOT NULL REFERENCES seats (id),
    PRIMARY KEY (reservation_id, seat_id)
);

CREATE TABLE tickets (
    id             UUID PRIMARY KEY,
    reservation_id UUID        NOT NULL REFERENCES reservations (id),
    seat_id        UUID        NOT NULL REFERENCES seats (id),
    seat_label     VARCHAR(20) NOT NULL,
    code           VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE payments (
    id              UUID PRIMARY KEY,
    reservation_id  UUID           NOT NULL REFERENCES reservations (id),
    user_id         UUID           NOT NULL REFERENCES users (id),
    amount          NUMERIC(10, 2) NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    idempotency_key VARCHAR(100)   NOT NULL UNIQUE,
    created_at      TIMESTAMPTZ    NOT NULL
);
