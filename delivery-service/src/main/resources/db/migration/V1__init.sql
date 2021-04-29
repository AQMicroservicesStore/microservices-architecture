CREATE TABLE orderdelivery (
      delivery_id         BIGSERIAL PRIMARY KEY NOT NULL,
      id_order            bigint NOT NULL,
      user_id             VARCHAR(200) NOT NULL,      
      delivery_address    VARCHAR(255) NOT NULL,
      shipping_date      bigint NULL,
      delivery_date       bigint NULL,
      status              VARCHAR(200) NOT NULL,      
      version             integer NOT NULL,
      created_by          VARCHAR(200) NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_by    VARCHAR(200) NOT NULL
);


