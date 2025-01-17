DROP TABLE SHOES_STOCK IF EXISTS;
CREATE TABLE SHOES_STOCK (
                             stock_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                             model_id INTEGER,
                             inputDate DATE DEFAULT CURRENT_DATE,
                             outputDate DATE DEFAULT NULL
);

DROP TABLE SHOES_MODEL IF EXISTS;
CREATE TABLE SHOES_MODEL (
                             model_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                             name  varchar(25) not null,
                             color varchar(12) not null,
                             size integer not null
);

ALTER TABLE SHOES_STOCK ADD CONSTRAINT FK_MODEL_STOCK FOREIGN KEY (MODEL_ID) REFERENCES SHOES_MODEL(MODEL_ID);
ALTER TABLE SHOES_MODEL ADD CONSTRAINT UNIC_MODEL UNIQUE ( name, color, size );
