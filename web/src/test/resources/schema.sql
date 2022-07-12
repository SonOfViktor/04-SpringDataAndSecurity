CREATE SCHEMA module_4;
-- -----------------------------------------------------
-- Table `module_4`.`gift_certificates`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `module_4`.`gift_certificates`
(
    `id`               INT           NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(45)   NOT NULL,
    `description`      VARCHAR(500)  NULL,
    `price`            DECIMAL(6, 2) NOT NULL DEFAULT 0,
    `duration`         INT           NOT NULL,
    `create_date`      TIMESTAMP(3)  NOT NULL,
    `last_update_date` TIMESTAMP(3)  NOT NULL,
    PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `module_4`.`tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `module_4`.`tags`
(
    `id`   INT         NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    UNIQUE (`name`),
    PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `module_4`.`gift_certificate_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `module_4`.`gift_certificate_tag`
(
    `gift_certificate_id` INT NOT NULL,
    `tag_id`              INT NOT NULL,
    PRIMARY KEY (`gift_certificate_id`, `tag_id`),
    FOREIGN KEY (`gift_certificate_id`)
        REFERENCES `gift_certificates` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (`tag_id`)
        REFERENCES `tags` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- -----------------------------------------------------
-- Table `module_4`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `module_4`.`users`
(
    `id`         INT         NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name`  VARCHAR(50) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `module_4`.`payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `module_4`.`payments`
(
    `id`           INT          NOT NULL AUTO_INCREMENT,
    `user_id`      INT          NOT NULL REFERENCES `module_4`.`users` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    `created_date` TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `module_4`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `module_4`.`orders`
(
    `id`                  INT           NOT NULL AUTO_INCREMENT,
    `cost`                NUMERIC(5, 2) NOT NULL CHECK (`cost` > 0),
    `gift_certificate_id` INT           REFERENCES `module_4`.`gift_certificates` (`id`)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    `payment_id`          INT           NOT NULL REFERENCES `module_4`.`payments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    PRIMARY KEY (`id`)
);