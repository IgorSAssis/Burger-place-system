INSERT INTO orders (status, opened_at, closed_at, payment_form, customer_id, active)
VALUES
    ('EM_ANDAMENTO', '2023-01-01 12:00:00', NULL, 'CARTAO_CREDITO', 1, true),
    ('CONCLUIDO', '2023-01-02 15:30:00', '2023-01-02 16:15:00', 'DINHEIRO', 3, true),
    ('EM_ANDAMENTO', '2023-01-03 10:45:00', NULL, 'CARTAO_DEBITO', 5, true),
    ('CONCLUIDO', '2023-01-04 18:20:00', '2023-01-04 19:05:00', 'PIX', 7, true),
    ('EM_ANDAMENTO', '2023-01-05 14:00:00', NULL, 'DINHEIRO', 9, true),
    ('CONCLUIDO', '2023-01-06 20:10:00', '2023-01-06 20:45:00', 'CARTAO_CREDITO', 2, true),
    ('EM_ANDAMENTO', '2023-01-07 17:30:00', NULL, 'CARTAO_DEBITO', 4, true),
    ('CONCLUIDO', '2023-01-08 12:40:00', '2023-01-08 13:25:00', 'PIX', 6, true),
    ('EM_ANDAMENTO', '2023-01-09 19:15:00', NULL, 'DINHEIRO', 8, true),
    ('CONCLUIDO', '2023-01-10 16:55:00', '2023-01-10 17:30:00', 'CARTAO_CREDITO', 10, true);