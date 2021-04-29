CREATE TABLE payment (
      payment_id          BIGSERIAL PRIMARY KEY NOT NULL,
      id_order            bigint NOT NULL,
      order_price         float4 NOT NULL,
      accepted            boolean,
      refunded            boolean,
      refunded_description   varchar(255) NULL,
      user_id             VARCHAR(200) NOT NULL,      
      transaction_code    varchar(255) UNIQUE NULL,
      transaction_date    bigint NULL,
      refunded_date       bigint NULL,
      version             integer NOT NULL,
      created_by          VARCHAR(200) NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_by    VARCHAR(200) NOT NULL
);


