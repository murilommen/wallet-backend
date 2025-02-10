--liquibase formatted sql

--changeset murilommen:create-transactions-table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    transaction_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    description VARCHAR(255),
    previous_balance DECIMAL(19, 2) NOT NULL,
    current_balance DECIMAL(19, 2) NOT NULL,
    related_transaction_id UUID,
    CONSTRAINT fk_transactions_wallets
        FOREIGN KEY (wallet_id)
        REFERENCES wallets(id)
);