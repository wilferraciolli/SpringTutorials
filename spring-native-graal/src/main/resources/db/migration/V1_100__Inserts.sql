insert into users(id, username, password, active)
values (1000, 'WilFerraciolli@wiltech.com', '{bcrypt}$2a$10$2WacIN6u7bxhQOkx9gxPAOaTZjab0GCzoCSdJF7HU5ajf5CC4hgga', 1);

insert into person(id, user_Id, first_name, last_name, email, gender, phone_Number, date_of_birth, marital_status, number_of_dependants)
values (2000, 1000, 'Wiliam', 'Ferraciolli', 'WilFerraciolli@wiltech.com', 'MALE', '+44 7540595289', '1985-02-16', 'MARRIED', 1);
