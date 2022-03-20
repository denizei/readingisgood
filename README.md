# Reading is Good API

Simple bookstore app that can perform the operations books and orders.

## Assumptions and Workflow

There are 2 types of users in this project
* Standard Users with USER type who can query books, place an order and watch them
* Admin Users with ADMIN type who can perform anything that standard user can; Also they can provide book inventory management and see all of the information and monthly statistics about the orders.

ADMIN users can enter data of book details, price and stock count. They can update price and stock count in case of need.

After that any user with USER role can place an order using these details and tracks the status of orders.

ADMINs can update the status of the orders which can be tracked by USERs.

Finally, ADMINs have the authority to generate statistics report to see the status of monthly book sales.



## Technologies
Project is created with:
* Java 17
* Spring Boot 2.6.4
* Maven
* H2 DB 2.1.210
* Open API 3 for [Swagger Documentation]

[Swagger Documentation]: http://localhost:8082/swagger-ui/index.html
## Setup
All the commands below are needed to run on project path.
* Firstly run `.\mvnw clean install` to create project jar file.
* Then run `docker compose up -d` and the project starts from address `http://localhost:8082/`
* To stop use command `docker compose down`

If you have problem about creating jar file, you can use pre-installed jar file for your container with help of following steps.
* Run `docker compose -f ./docker-compose-cached.yml up -d` and the project starts from address `http://localhost:8082/`
* To stop use command `docker compose -f ./docker-compose-cached.yml  down`

## Database
For testing purposes, database is made reachable on purpose and can be reached
by using the link below
[DB] (http://localhost:8082/db/)
* username:un
* password: (no password needed)

Some sample data are already inserted in database.
`bookstoredb.mv.db` is the file which includes the database.

#### Tables 
* `CUSTOMER` => id, email, name, surname, registration_date, password, status, role, address
* `BOOK` => id, name, author, price, publication_year, isbn, stock_count
* `ORDER` => id, customer_id, order_date, status
* `ORDER_BOOK` => id, order_id, quantity,price
* `CHANGE_LOG` => id, customer_id, entity_id, action_date, status, old_value, new_value

## Usage
Postman collection can be found in this directory:
[POSTMAN]

[POSTMAN]: https://github.com/denizei/readingisgood/blob/main/readingisgood_postman_collection.json

These operations can be performed without any authorization
* Create a customer
* Authenticate
* View details of all the books in the database
* Search for books with keyword
* View book details by book id

This system uses JWT based authentication.

To perform further operations please authenticate using;
* Current admin credentials:
  * email: deniz@example.com
  * password: asdf1234
* Current user credentials:
  * email: ashketchumexample@example.com
  * password: abcd1234
* Creating a new customer and using its email and password info.
* If customer role is set to **1** then the customer becomes an admin.

Since a token is created, it is available for **3 hours**. 

After that please use the generated Bearer token with the request below in Postman.
`https://localhost:8082/authenticate` (Authenticate as User/ Authenticate as Admin)

After being authorized as admin one can perform these operations
* Create a book  (POST `https://localhost:8082/api/book`)
* Place an order of books (POST `https://localhost:8082/api/order`)
* View an order (GET `https://localhost:8082/api/order/{id}`)
* View orders by date interval (GET `https://localhost:8082/api/order?startDate={yyyy-MM-dd}&endDate={yyyy-MM-dd}`)
* Update a book's price or stock count (PUT `https://localhost:8082/api/book/{id}`)
* Update an order's status (PUT `https://localhost:8082/api/order/changestatus/{id}`)
* Display monthly statistics (GET `https://localhost:8082/api/stats/monthly?startDate={yyyy-MM}&endDate={yyyy-MM}`)
* View their orders  (POST `https://localhost:8082/api/orders`)


After authorized as user one can perform these operations
* Place an order of books (POST `https://localhost:8082/api/order`)
* View their orders  (POST `https://localhost:8082/api/orders`)
* View an order of their own (GET `https://localhost:8082/api/order/{id}`)

## Logging

* All  the operations are logged on table `CHANGE_LOG`.
* entity_id corresponds to the id of the table which the log is related.
  * The entity id with the value -1 represents calling the `/api/stats/monthlyrequest`.

* Log status codes can be found below
  * ORDER_INSERT : 0
  * BOOK_INSERT : 1
  * ORDER_UPDATE : 2
  * BOOK_UPDATE_BY_USER : 3
  * BOOK_UPDATE_BY_ORDER : 4
  * CUSTOMER_CREATED : 5
  * MONTHLY_REPORT_FETCHED : 6

## Tests
JUnit tests are implemented. Zipped coverage report file can be obtainable from [HERE].

[HERE]: https://github.com/denizei/readingisgood/tree/main/src/test/docs/coveragereport.zip
* Class coverage: 100%
* Method coverage: 74%
* Line coverage: 79%
