CREATE TABLE IF NOT EXISTS wallet_account (
  user_id UUID PRIMARY KEY,
  balance NUMERIC(19,2) NOT NULL
);

INSERT INTO wallet_account(user_id, balance)
VALUES ('11111111-1111-1111-1111-111111111111', 100000.00)
ON CONFLICT (user_id) DO NOTHING;

CREATE TABLE IF NOT EXISTS outbox_event (
  id UUID PRIMARY KEY,
  aggregate_type VARCHAR(64) NOT NULL,
  aggregate_id VARCHAR(64) NOT NULL,
  type VARCHAR(64) NOT NULL,
  payload JSONB NOT NULL,
  headers JSONB,
  status VARCHAR(16) NOT NULL DEFAULT 'NEW',
  attempts INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_error TEXT
);

CREATE TABLE IF NOT EXISTS processed_messages (
  message_id VARCHAR(100) PRIMARY KEY,
  processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
