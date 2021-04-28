CREATE TABLE user_address (
      user_id             varchar(255) PRIMARY KEY NOT NULL,
      city	              varchar(255) NOT NULL,
      country             varchar(255) NOT NULL,
      street              varchar(255) NOT NULL,
      cap                 varchar(255) NOT NULL,
      version             integer NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_date  bigint NOT NULL
);
