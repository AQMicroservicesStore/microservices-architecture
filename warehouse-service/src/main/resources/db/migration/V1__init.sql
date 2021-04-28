CREATE TABLE product (
      id                  BIGSERIAL PRIMARY KEY NOT NULL,
      name	              varchar(255) UNIQUE NOT NULL,
      type                varchar(255) NOT NULL,
      brand               varchar(255) NOT NULL,
      description         varchar(255) NULL,
      weight		  integer NOT NULL,
      version             integer NOT NULL,
      created_by VARCHAR(200) NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_by    VARCHAR(200) NOT NULL,
      last_modified_date  bigint NOT NULL
);



CREATE TABLE stock (
      product             bigint PRIMARY KEY references product(id),
      quantity	        integer NOT NULL,
      price_to_sell       float8 NOT NULL,
      purchase_cost       float8 NOT NULL,
      version             integer NOT NULL,
      created_by VARCHAR(200) NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_by    VARCHAR(200) NOT NULL,
      last_modified_date  bigint NOT NULL     
);




