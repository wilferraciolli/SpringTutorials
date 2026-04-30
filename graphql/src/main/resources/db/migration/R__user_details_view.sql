-- This view aggregates users and their roles into a single searchable entity
CREATE
OR REPLACE VIEW user_details_v AS
SELECT u.id,
       u.username,
       u.email,
       GROUP_CONCAT(r.name SEPARATOR ', ') AS role_names
FROM users u
         LEFT JOIN user_roles ur ON u.id = ur.user_id
         LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.email;
