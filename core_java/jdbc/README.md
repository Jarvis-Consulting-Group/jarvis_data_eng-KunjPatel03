# Introduction
The JDBC application is a robust system for performing essential CRUD operations (Create, Read, Update, Delete) on a database, utilizing the powerful JDBC API. It integrates a PostgreSQL database within a Docker container environment, with Maven as the package management and dependency resolution tool, and Git as the chosen version control system.

## Implementation

The implementation of this project followed a well-structured and formal approach within a Linux environment running CentOS 7. The primary objective was to develop a robust JDBC application that allows users to perform CRUD operations on the database, leveraging the powerful JDBC API. Throughout the implementation process, Maven was utilized as the package management and dependency resolution tool.

To facilitate a quick and smooth setup, follow the steps below:

### Prerequisites
1. Docker: Ensure that Docker is installed on your system.
2. psql client: Install the psql client to interact with the PostgreSQL database.

### Quick Start

1. Clone the project repository:

```shell script
git clone https://github.com/Jarvis-Consulting-Group/jarvis_data_eng-KunjPatel03.git
cd jarvis_data_eng_Kunj/core_java/jdbc
```

2. Pull the PostgreSQL Docker image:

```shell script
docker pull postgres:9.6-alpine
```

3. Create a volume for persistent database data:

```shell script
docker volume create pgdata
```

4. Run the Docker image and create the container:

```shell script
docker run --name jrvs-psql -e POSTGRES_PASSWORD=password -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:9.6-alpine
```

5. Verify that Docker is running properly:

```shell script
sudo systemctl status docker
```

6. Ensure that the Docker container is up and running:

```shell script
docker ps
```

7. If necessary, start the Docker container:

```shell script
docker container start jrvs-psql
```

8. Set up the 'hplussport' database and load data into its tables. Execute the following SQL files sequentially:

```shell script
psql -h localhost -U postgres -p 5432 -f ../assets/database.sql
psql -h localhost -U postgres -d hplussport -p 5432 -f ../assets/customer.sql
psql -h localhost -U postgres -d hplussport -p 5432 -f ../assets/product.sql
psql -h localhost -U postgres -d hplussport -p 5432 -f ../assets/salesperson.sql
psql -h localhost -U postgres -d hplussport -p 5432 -f ../assets/orders.sql
```

These SQL files will create the necessary tables and populate them with relevant data in the 'hplussport' database.

By following these comprehensive implementation steps, you will successfully set up the project environment and ensure the proper functioning of the JDBC application.

Feel free to reach out if you have any further questions or need additional assistance!
## ER Diagram
![core_java_ER.png](../assets/ERD.jpeg)

The ER Diagram illustrates the database structure for the project, consisting of several interconnected tables:

1. **Customer**: Represents information about customers, including their unique identifier, first name, last name, email, phone number, address, city, state, and zipcode.

2. **Product**: Contains details of products available, such as the product ID, code, name, size, variety, price, and status.

3. **Salesperson**: Stores information about salespersons, including their unique identifier, first name, last name, email, phone number, address, city, state, and zipcode.

4. **Orders**: Tracks order-related data, including the order ID, creation date, total due, status, customer ID, and salesperson ID. It establishes a relationship with the Customer table through the customer ID and with the Salesperson table through the salesperson ID.

5. **Order_Item**: Represents individual items within an order, denoting the order item ID, order ID, product ID, and quantity. It establishes relationships with the Orders and Product tables through the order ID and product ID, respectively.

The relationships between the tables are defined as follows:

- Each order in the Orders table is associated with a specific customer through the customer ID reference.
- Each order in the Orders table is assigned to a salesperson via the salesperson ID reference.
- Each order item in the Order_Item table is linked to a specific order through the order ID reference.
- Each order item in the Order_Item table is associated with a specific product via the product ID reference.

The ER Diagram visually represents the structure and relationships between these tables, providing an overview of the database schema for the project.

## Design Patterns

The project incorporates the implementation of two essential design patterns: DAO (Data Access Object) and Repository.

The DAO pattern serves as an abstract method of design that is responsible for creating the necessary classes and interfaces to facilitate CRUD operations in a database. In the project, the DAO pattern is applied to create a class that models the 'Customers' table. Each instance of this class represents a row within the table, with class attributes corresponding to the fields in the database table. The methods within the class are responsible for performing CRUD operations such as creating new entries, updating existing rows, reading data, and deleting rows.

On the other hand, the Repository design pattern shares similarities with DAO but focuses strictly on a single table per class. In our project, the DAO implementation for the 'Customers' table also adheres to the Repository design pattern. While both DAO and Repository patterns interact with the database and abstract the access to it, DAOs are generally designed to be more flexible, working with various object types. In contrast, Repositories serve as a specific type of object search and function more like a collection, supporting CRUD operations specifically for the 'Customers' table.

The implementation of these design patterns ensures structured and efficient interaction with the database, providing a clear separation of concerns and promoting code reusability and maintainability.

## Test

The application primarily underwent database testing to ensure the proper functioning of CRUD operations. The following tests were performed to validate the application's functionality:

1. **Docker Verification**: The first step was to ensure that Docker was running correctly by executing the following command:
    ```shell script
    docker ps -f name=jrvs-psql
    ```
    This command checked if the Docker container for the PostgreSQL database was up and running.

2. **Database Verification**: Next, the creation of the database was verified by connecting to it using the following command:
    ```shell script
    psql -h localhost -U postgres -d hplussport
    ```
    This command validated that the 'hplussport' database was successfully created.

3. **Data Population Verification**: Queries were executed to verify if the data had been properly populated in the database tables. For example, the following query was used to check the number of rows in the 'customer' table:
    ```sql
    SELECT COUNT(*) FROM customer;
    ```
    Similar queries were executed for other tables to ensure the correct population

 of data.

4. **JDBC Connection Verification**: The Java file was run to verify if the JDBC connection to the database was established successfully. This step ensured that the application could interact with the database using JDBC.

5. **Manual Application Testing**: The application was manually tested by querying the PostgreSQL database within a terminal to confirm if data created, updated, or deleted by the application reflected accurately in the database. The PostgreSQL database was set up and deployed within a Docker container using the `docker run` command with the Postgres base image from Docker Hub.

These database-centric tests were conducted to validate the application's functionality and ensure the correctness of CRUD operations performed by the JDBC application on the PostgreSQL database.