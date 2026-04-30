CREATE
OR REPLACE VIEW order_summary_view AS
SELECT u.id,
       u.username,
       SUM(o.amount) AS total_amount,
       o.status
FROM users u
         JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.username, o.status;