CREATE TABLE IF NOT EXISTS transactions
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    customer_id
    BIGINT
    NOT
    NULL,
    amount
    DECIMAL
(
    10,
    2
) NOT NULL,
    transaction_date DATE NOT NULL
    );