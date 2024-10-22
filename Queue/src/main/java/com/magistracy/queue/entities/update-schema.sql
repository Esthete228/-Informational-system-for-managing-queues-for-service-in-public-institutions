CREATE TABLE appointment
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    service_entity_id BIGINT                NOT NULL,
    client_id         BIGINT                NOT NULL,
    appointment_time  datetime              NULL,
    CONSTRAINT pk_appointment PRIMARY KEY (id)
);

CREATE TABLE client
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    username     VARCHAR(255)          NULL,
    phone_number VARCHAR(255)          NULL,
    `role`       VARCHAR(255)          NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

CREATE TABLE employee
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    username     VARCHAR(255)          NULL,
    password     VARCHAR(255)          NULL,
    `role`       VARCHAR(255)          NULL,
    workplace_id BIGINT                NULL,
    CONSTRAINT pk_employee PRIMARY KEY (id)
);

CREATE TABLE otp_code_entity
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    phone_number    VARCHAR(255)          NULL,
    otp_code        VARCHAR(255)          NULL,
    expiration_time datetime              NULL,
    CONSTRAINT pk_otpcodeentity PRIMARY KEY (id)
);

CREATE TABLE queue
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    client_id        BIGINT                NULL,
    service_id       BIGINT                NULL,
    workplace_id     BIGINT                NULL,
    appointment_time datetime              NULL,
    status           VARCHAR(255)          NULL,
    ticket_number    INT                   NOT NULL,
    CONSTRAINT pk_queue PRIMARY KEY (id)
);

CREATE TABLE service_entity
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    service_name        VARCHAR(255)          NULL,
    service_description VARCHAR(255)          NULL,
    CONSTRAINT pk_serviceentity PRIMARY KEY (id)
);

CREATE TABLE workplace
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    workplace_name VARCHAR(255)          NULL,
    CONSTRAINT pk_workplace PRIMARY KEY (id)
);

ALTER TABLE appointment
    ADD CONSTRAINT FK_APPOINTMENT_ON_CLIENT FOREIGN KEY (client_id) REFERENCES client (id);

ALTER TABLE appointment
    ADD CONSTRAINT FK_APPOINTMENT_ON_SERVICE_ENTITY FOREIGN KEY (service_entity_id) REFERENCES service_entity (id);

ALTER TABLE employee
    ADD CONSTRAINT FK_EMPLOYEE_ON_WORKPLACE FOREIGN KEY (workplace_id) REFERENCES workplace (id);

ALTER TABLE queue
    ADD CONSTRAINT FK_QUEUE_ON_CLIENT FOREIGN KEY (client_id) REFERENCES client (id);

ALTER TABLE queue
    ADD CONSTRAINT FK_QUEUE_ON_SERVICE FOREIGN KEY (service_id) REFERENCES service_entity (id);

ALTER TABLE queue
    ADD CONSTRAINT FK_QUEUE_ON_WORKPLACE FOREIGN KEY (workplace_id) REFERENCES workplace (id);