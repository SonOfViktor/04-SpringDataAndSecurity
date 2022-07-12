-- -----------------------------------------------------
-- Table `module_4`.`gift_certificates`
-- -----------------------------------------------------
INSERT INTO module_4.gift_certificates (name, description, price, duration, create_date, last_update_date)
VALUES ('Oz.by', 'Books, games, stationery', 20, 40, '2022-04-11 13:48:14.000', '2022-04-11 13:48:14.000'),
       ('Belvest', 'Change two shoes', 50, 30, '2022-04-12 13:48:14.000', '2022-04-12 13:48:14.000'),
       ('Evroopt', 'We have a lot of sugar!', 40, 10, '2022-04-13 13:48:14.000', '2022-04-13 13:48:14.000'),
       ('Evroopt', 'Buy two bananas', 20, 10, '2022-04-10 13:48:14.000', '2022-04-10 13:48:14.000');

-- -----------------------------------------------------
-- Table `module_4`.`tags`
-- -----------------------------------------------------
INSERT INTO module_4.tags (name)
VALUES ('food'),
       ('stationery'),
       ('shoe'),
       ('virtual'),
       ('paper'),
       ('by');

-- -----------------------------------------------------
-- Table `module_4`.`gift_certificate_tag`
-- -----------------------------------------------------
INSERT INTO module_4.gift_certificate_tag (gift_certificate_id, tag_id)
VALUES (1, 2),
       (1, 4),
       (2, 3),
       (2, 5),
       (2, 6),
       (3, 1),
       (3, 2),
       (3, 5),
       (3, 6),
       (4, 1),
       (4, 2),
       (4, 5),
       (4, 6);

-- -----------------------------------------------------
-- Table `module_4`.`users`
-- -----------------------------------------------------
INSERT INTO module_4.users (email, password, first_name, last_name, role)
VALUES ('Ivan_Pupkin@gmail.com',
        '$2a$12$8jtm/F2lKTrxJKR1N4z1m.RTFmtAn7VIQ2IHLjtk/wTxd.2Vlmkb6', 'Ivan', 'Pupkin', 'ADMIN'),
       ('Sanek_Lupkin.gmail.com',
        '$2a$12$8jtm/F2lKTrxJKR1N4z1m.RTFmtAn7VIQ2IHLjtk/wTxd.2Vlmkb6','Sanek', 'Lupkin', 'USER');

-- -----------------------------------------------------
-- Table `module_4`.`payments`
-- -----------------------------------------------------
INSERT INTO module_4.payments (user_id, created_date)
VALUES (1, '2022-05-26 22:25:17.000'),
       (1, '2022-05-27 22:25:17.000'),
       (1, '2022-05-28 22:25:17.000'),
       (2, '2022-05-26 22:25:17.000'),
       (2, '2022-05-28 22:25:17.000');

-- -----------------------------------------------------
-- Table `module_4`.`orders`
-- -----------------------------------------------------
INSERT INTO module_4.orders (cost, payment_id, gift_certificate_id)
VALUES (20, 1, 1),
       (10, 1, 2),
       (5, 2, 2),
       (20, 2, 3),
       (20, 3, 4),
       (20, 4, 4),
       (20, 4, 4),
       (20, 5, 2),
       (20, 5, 3);