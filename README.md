# Backend-Test

This is a Java application with RESTful API implemented for money transfers between accounts.

## Run The Project and Check All the APIs.
Clone the project via Git.
```bash
git clone git@github.com:guangyang1206/Backend-Test.git
```
Run the project using command below
```bash
mvn exec:java
```

Run the project test
```bash
mvn clean test
```
The project server is running on localhost:8080 with H2 in-memory database initialized with sample data.

* Get Requests
  * Get all the customers http://localhost:8080/customer/all
  * Get customer by name http://localhost:8080/customer/name/Bob
  * Get customer by ID http://localhost:8080/customer/id/1
  * Get all the accounts http://localhost:8080/account/all
  * Get account by ID http://localhost:8080/account/1
  * Get account balance by ID http://localhost:8080/account/1/balance
* Post Requests
  * Create new customer /customer/create
  * Create new account /account/create
  * Transfer money between 2 accounts /transaction
* PUT Requests
   * Update existing customer /customer/{customerId}
   * Deposit money to customer account /account/{accountId}/deposit/{amount}
   * Withdraw money from customer account /account/{accountId}/withdraw/{amount}
* Delete Requests
   * Delete existing customer /customer/{customerId}
   * Delete existing account /account/{accountId}

## Sample JSON Data
##### Customer :
```bash
{
  "customerName":"Allen",
  "emailAddress":"allen@revolut.com",
  "phoneNumber":"11122223333"
}
```
##### Customer Account: :

```sh
{
  "customerName":"Allen",
  "balance":1000.0000,,
  "currencyCode":"CNY"
}
```

#### Customer Transaction:
```sh
{
  "currencyCode":"EUR",
  "amount":100000.0000,
  "fromAccountId":1,
  "toAccountId":2
}
```
