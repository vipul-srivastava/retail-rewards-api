INSERT INTO transactions (customer_id, amount, transaction_date)
VALUES (101, 120.50, CURRENT_DATE);
INSERT INTO transactions (customer_id, amount, transaction_date)
VALUES (101, 80.00, CURRENT_DATE);
INSERT INTO transactions (customer_id, amount, transaction_date)
VALUES (101, 150.00, DATEADD('MONTH', -1, CURRENT_DATE));
INSERT INTO transactions (customer_id, amount, transaction_date)
VALUES (102, 40.00, CURRENT_DATE);
INSERT INTO transactions (customer_id, amount, transaction_date)
VALUES (103, 100.00, CURRENT_DATE);