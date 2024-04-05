DROP TABLE IF EXISTS wallets;

CREATE TABLE IF NOT EXISTS wallets
(
    id            UUID DEFAULT gen_random_uuid(),
    amount        NUMERIC NOT NULL,
    CONSTRAINT    pk_wallets PRIMARY KEY (id)
);