CREATE TABLE purchase_order (
      id_order             BIGSERIAL PRIMARY KEY NOT NULL,
      status	        varchar(255) NOT NULL,
      status_description  varchar(255) NOT NULL,
      delivery_address    varchar(255) NULL,
      payment_id           bigint NULL,
      delivery_id          bigint NULL,
      order_price         float8 NULL,
      order_cost          float8 NULL,
      total_weight        integer NULL,
      version             integer NOT NULL,
      created_by VARCHAR(200) NOT NULL,
      created_date        bigint NOT NULL,
      last_modified_by    VARCHAR(200) NOT NULL,
      last_modified_date  bigint NOT NULL
);


CREATE TABLE purchase_item (
      item_id             bigint PRIMARY KEY NOT NULL,
      purchase_order      bigint references purchase_order (id_order),
      quantity	        integer NOT NULL,
      in_stock            boolean NOT NULL
)



