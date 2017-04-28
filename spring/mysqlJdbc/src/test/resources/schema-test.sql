DROP TABLE IF EXISTS `Person`;
CREATE TABLE `Person` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `documentType` VARCHAR(10) NOT NULL,
  `documentNumber` VARCHAR(15) NOT NULL,
  `firstName` VARCHAR(30) NOT NULL,
  `lastName` VARCHAR(45) NOT NULL,
  `email` VARCHAR(85) NOT NULL,
  `birthDate` DATE NULL,
  `created` TIMESTAMP NOT NULL DEFAULT now(),
  `lastUpdate` TIMESTAMP NOT NULL DEFAULT now(),
  `deleted` BIT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `documentNumber_UNIQUE` (`documentNumber` ASC));