-- 1. Create 100 Users
INSERT INTO users (username, email, password)
SELECT
    'user_' || x,
    'user_' || x || '@example.com',
    'password' || x
FROM SYSTEM_RANGE(1, 100);

-- 2. Create Roles
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_GUEST');

-- 3. Assign random roles (Every user gets ROLE_USER, even ones get ROLE_ADMIN)
INSERT INTO user_roles (user_id, role_id)
SELECT id, 1 FROM users;

INSERT INTO user_roles (user_id, role_id)
SELECT id, 2 FROM users WHERE MOD(id, 2) = 0;

-- 4. Create 500 Orders with random amounts and statuses
INSERT INTO orders (user_id, amount, status)
SELECT
    FLOOR(RAND() * 100) + 1, -- Random user_id between 1 and 100
    CAST(RAND() * 1000 AS DECIMAL(19,2)),
    CASE WHEN RAND() > 0.5 THEN 'COMPLETED' ELSE 'PENDING' END
FROM SYSTEM_RANGE(1, 500);