
CREATE USER aqstore_order WITH PASSWORD 'aqstore_order' CREATEDB;
CREATE USER aqstore_account WITH PASSWORD 'aqstore_account' CREATEDB;
CREATE USER aqstore_warehouse WITH PASSWORD 'aqstore_warehouse' CREATEDB;
CREATE USER aqstore_payment WITH PASSWORD 'aqstore_payment' CREATEDB;
CREATE USER aqstore_delivery WITH PASSWORD 'aqstore_delivery' CREATEDB;
CREATE USER aqstore_keycloak WITH PASSWORD 'aqstore_keycloak' CREATEDB;

CREATE DATABASE aqstore_account
    WITH 
    OWNER = aqstore_account
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE aqstore_order
    WITH 
    OWNER = aqstore_order
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;


CREATE DATABASE aqstore_warehouse
    WITH 
    OWNER = aqstore_warehouse
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;


CREATE DATABASE aqstore_payment
    WITH 
    OWNER = aqstore_payment
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;


CREATE DATABASE aqstore_delivery
    WITH 
    OWNER = aqstore_delivery
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE aqstore_keycloak
    WITH 
    OWNER = aqstore_keycloak
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

GRANT ALL PRIVILEGES ON DATABASE "aqstore_order" to admin;
GRANT ALL PRIVILEGES ON DATABASE "aqstore_account" to admin;
GRANT ALL PRIVILEGES ON DATABASE "aqstore_warehouse" to admin;
GRANT ALL PRIVILEGES ON DATABASE "aqstore_payment" to admin;
GRANT ALL PRIVILEGES ON DATABASE "aqstore_delivery" to admin;
GRANT ALL PRIVILEGES ON DATABASE "aqstore_keycloak" to admin;
