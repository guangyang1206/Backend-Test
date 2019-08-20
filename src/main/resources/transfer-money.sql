
DROP TABLE IF EXISTS Customer;

CREATE TABLE Customer (CustomerId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 CustomerName VARCHAR(30) NOT NULL,
 EmailAddress VARCHAR(30) NOT NULL,
 PhoneNumber VARCHAR(30) NOT NULL);

CREATE UNIQUE INDEX idx_ue on Customer(CustomerName, PhoneNumber);

INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES ('Allen', 'allen@revolut.com', '11122223333');
INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES ('Bob','bob@revolut.com', '22211113333');
INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES ('Carl','carl@revolut.com', '33344445555');
INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES ('David','david@revolut.com', '44455556666');
INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES ('Ellen','ellen@revolut.com', '55566667777');
INSERT INTO Customer (CustomerName, EmailAddress, PhoneNumber) VALUES ('Frank','frank@revolut.com', '66677778888');

DROP TABLE IF EXISTS CustomerAccount;

CREATE TABLE CustomerAccount (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
CustomerName VARCHAR(30),
Balance DECIMAL(19,4),
CurrencyCode VARCHAR(30)
);

CREATE UNIQUE INDEX idx_acc on CustomerAccount(CustomerName, CurrencyCode);

INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Allen',1000.0000,'CNY');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Bob',2000.0000,'CNY');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Carl',3000.0000,'EUR');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('David',4000.0000,'EUR');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Ellen',5000.0000,'GBP');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Frank',5000.0000,'GBP');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Gary',2000.0000,'GBP');
INSERT INTO CustomerAccount (CustomerName, Balance, CurrencyCode) VALUES ('Howard',3000.0000,'USD');
