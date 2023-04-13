insert into users(id, role_type, first_name, last_name, username, password, active)
values (1000, 'USER', 'Wiliam', 'Ferraciolli', 'WilFerraciolli@wiltech.com', '$2a$10$z2fahEz7LDQLxbxt154b0.upIfQJvL/iwI8pUjNWWL6BDtqTeIhda', 1);

insert into token(id, user_id, token, token_type, revoked, expired)
values (2000, 1000, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJXaWxGZXJyYWNpb2xsaUB3aWx0ZWNoLmNvbSIsImlhdCI6MTY4MTQxNjQxMywiZXhwIjoxNjgxNDE3ODUzfQ.jDfrp_Ku21escdldckTRoW4Ib57lpKkGXcXm5ECvRuI', 'BEARER', 0, 0);
