CREATE TABLE category
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE product
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255),
    unit        VARCHAR(255),
    description VARCHAR(255),
    price       DOUBLE PRECISION                        NOT NULL,
    image_path  VARCHAR(255),
    active      BOOLEAN                                 NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE product_category
(
    category_id BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    CONSTRAINT pk_product_category PRIMARY KEY (category_id, product_id)
);

ALTER TABLE product
    ADD CONSTRAINT uc_product_name UNIQUE (name);

ALTER TABLE product_category
    ADD CONSTRAINT fk_procat_on_category FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE product_category
    ADD CONSTRAINT fk_procat_on_product FOREIGN KEY (product_id) REFERENCES product (id);

INSERT INTO category (id, name) VALUES (1, 'Fruits');
INSERT INTO category (id, name) VALUES (2, 'Organic');
INSERT INTO category (id, name) VALUES (3, 'Juices');