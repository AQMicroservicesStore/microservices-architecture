alter table user_address add column  full_name varchar(255) CHECK (full_name <> '');
