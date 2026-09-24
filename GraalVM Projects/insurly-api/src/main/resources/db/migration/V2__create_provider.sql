-- Table for the admin/providers feature (com.wiltech.insurly.admin.providers.Provider).
create table provider (
    id            uuid         primary key default gen_random_uuid(),
    name          varchar(255) not null,
    email         varchar(255) not null,
    phone_number  varchar(50),
    website       varchar(255),
    provider_type varchar(40)  not null
);
