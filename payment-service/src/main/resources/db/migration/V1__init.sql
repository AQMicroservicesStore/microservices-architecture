CREATE TABLE payment (
      payment_id          BIGSERIAL PRIMARY KEY NOT NULL,
      id_order            bigint NOT NULL,
      order_price         float4 UNIQUE NULL,
      accepted            boolean,
      refunded            boolean,
      user_id             VARCHAR(200) NOT NULL,      
      transaction_code    varchar(255) UNIQUE NULL,
      refunded_date       bigint NOT NULL,
      confirmed_date      bigint NOT NULL,
      version             integer NOT NULL,
      created_by          VARCHAR(200) NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_by    VARCHAR(200) NOT NULL
);


