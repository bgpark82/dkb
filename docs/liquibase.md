# Liquibase Setup

This document describes the process of setting up Liquibase in this project and how to use it for managing database schema changes.

The setup process involved the following steps:
1.  Adding the `liquibase-core` dependency to `build.gradle.kts`.
2.  Creating the initial master changelog (`db.changelog-master.yaml`) and the first SQL changeset (`001-initial-schema.sql`).
3.  Enabling Liquibase in the Spring Boot application via `application.yml`.

The following sections provide the specific details for each configuration file.

## 1. Add Liquibase Dependency

Add the following to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("org.liquibase:liquibase-core")
}
```

## 2. Create Changelog Files

Create a master changelog file at `src/main/resources/db/changelog/db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-initial-schema.sql
```

Create an initial schema file at `src/main/resources/db/changelog/changes/001-initial-schema.sql`:

```sql
-- liquibase formatted sql

-- changeset author:bgpark dbms:h2
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

## 3. Spring Boot Integration

To ensure that Liquibase runs on application startup, add the following to your `application.yml`:

```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

With this configuration, Spring Boot will automatically run Liquibase migrations upon application startup.

### How Automatic Migration Works

Spring Boot's auto-configuration is the key to this seamless integration. Here's a brief overview of the process:

1.  **Dependency Detection**: When you include `liquibase-core` in your `build.gradle.kts`, Spring Boot detects it on the classpath.
2.  **Auto-configuration Activation**: The presence of the Liquibase library triggers `LiquibaseAutoConfiguration`.
3.  **Bean Creation**: This auto-configuration class automatically creates and configures a `SpringLiquibase` bean.
4.  **Datasource Injection**: The `SpringLiquibase` bean uses the application's primary `DataSource` to connect to the database.
5.  **Migration Execution**: During the application startup sequence, the `SpringLiquibase` bean is invoked. It checks the database's changelog history against the changelog files specified in `spring.liquibase.change-log` and applies any pending changesets before the application is fully started.

This process ensures that the database schema is always up-to-date with the application's requirements without any manual intervention.

### How Liquibase Reads Database Information

Liquibase itself doesn't directly read your `application.yml` file. Instead, it leverages the `DataSource` bean that Spring Boot creates and manages:

1.  **DataSource Configuration**: Spring Boot reads the `spring.datasource.*` properties (`url`, `username`, `password`, etc.) from your `application.yml`.
2.  **DataSource Bean Creation**: It uses these properties to configure and create a primary `DataSource` bean in the application context. This bean represents the connection pool to your database.
3.  **Injection into Liquibase**: The `LiquibaseAutoConfiguration` (mentioned previously) obtains this primary `DataSource` bean from the Spring context.
4.  **Connection Establishment**: The `SpringLiquibase` bean is then configured to use this `DataSource`. When Liquibase runs, it asks the `DataSource` for a database connection, which is already configured with all the necessary credentials and connection details.

In short, Liquibase integrates into the application's existing database connection, ensuring that it operates on the same database that the application uses.


## How to Connect to the Database with Liquibase

This section provides instructions on how to connect to the PostgreSQL database using Liquibase.

### 1. Start the PostgreSQL Database

Ensure that the PostgreSQL database is running. Create a `docker-compose.yml` file with the following content:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    container_name: my_postgres
    restart: always
    environment:
      POSTGRES_USER: myuser
      POSTGRES_PASSWORD: mypassword
      POSTGRES_DB: mydatabase
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

You can start it using Docker Compose with the following command:

```bash
docker-compose up -d
```

### 2. Configure the Datasource in Spring Boot

Update your `application.yml` to connect to the PostgreSQL database. Modify the `spring.datasource` section as follows:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydatabase
    username: myuser
    password: mypassword
    driver-class-name: org.postgresql.Driver
```

### 3. Run Liquibase Migrations

Liquibase migrations are automatically applied when the Spring Boot application starts. If you need to apply them manually, you can use the Liquibase CLI.

## Database Rollbacks with Liquibase

Liquibase supports the ability to roll back changes that have been deployed to your database. This is a critical feature for reverting a deployment that has caused issues.

### 1. Defining Rollback Logic

For Liquibase to be able to perform a rollback, you must define the rollback steps within your changesets. If no rollback logic is provided, Liquibase may not be able to automatically roll back the change.

#### XML Format

In XML changesets, you can add a `<rollback>` tag to specify the rollback operations. For changes like `createTable`, Liquibase can often auto-generate the rollback, but it's best practice to be explicit.

```xml
<changeSet id="002-create-payment" author="bgpark">
    <createTable tableName="payment">
        <column name="id" type="BIGINT" autoIncrement="true">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="amount" type="DECIMAL(19, 2)">
            <constraints nullable="false"/>
        </column>
    </createTable>
    <rollback>
        <dropTable tableName="payment"/>
    </rollback>
</changeSet>
```

#### SQL Format

In SQL-formatted changesets, you can specify rollback logic using a comment with the format `-- rollback <your-rollback-sql>`. You can have multiple lines for the rollback script.

```sql
-- liquibase formatted sql

-- changeset author:bgpark dbms:h2 id:001-initial-schema
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- rollback DROP TABLE customer;
```

### 2. Setting Up and Configuring the Liquibase CLI

Database rollbacks are typically performed manually using the Liquibase Command-Line Interface (CLI). Before you can execute rollbacks, you need to install and configure the CLI to connect to your database.

#### a. Install the Liquibase CLI

First, install the Liquibase CLI on your system. You can find detailed instructions for your operating system on the [official Liquibase documentation](https://docs.liquibase.com/start/install/home.html).

For macOS users, a common way to install it is via Homebrew:
```bash
brew install liquibase
```

#### b. Download the PostgreSQL JDBC Driver

The Liquibase CLI needs the appropriate JDBC driver to communicate with your PostgreSQL database.

1.  Download the PostgreSQL JDBC driver JAR file from the [official PostgreSQL website](https://jdbc.postgresql.org/download/).
2.  Create a directory named `lib` in the root of your project.
3.  Place the downloaded JAR file (e.g., `postgresql-42.7.3.jar`) inside the `lib` directory.

#### c. Create the Liquibase Properties File

To avoid passing database credentials as command-line arguments every time, create a `liquibase.properties` file in the root of your project. This file will hold the configuration for your database connection.

Create the file `liquibase.properties` in the project root with the following content:

```properties
# Liquibase Properties
# Path to your master changelog file
changeLogFile: src/main/resources/db/changelog/db.changelog-master.yaml

# Database Connection Details
url: jdbc:postgresql://localhost:5432/mydatabase
username: myuser
password: mypassword

# Path to the JDBC driver JAR
classpath: lib/postgresql-42.7.3.jar
```
**Note:** Make sure the filename in the `classpath` property matches the version of the JDBC driver you downloaded.

#### d. Verify the Configuration

Once everything is set up, you can verify the connection by running the `status` command from your project root. This command shows you which changesets have been deployed.

```bash
liquibase status
```

If the configuration is correct, you should see a summary of your changesets.

#### e. How the CLI Uses `liquibase.properties`

When you run any `liquibase` command from your project's root directory, the CLI automatically detects and reads the `liquibase.properties` file. The properties defined in this file serve as default values for the command-line arguments.

This means you don't have to repeatedly type connection details and other parameters. For example, running:

```bash
liquibase status
```

Is equivalent to running the much longer command:

```bash
liquibase --changeLogFile=src/main/resources/db/changelog/db.changelog-master.yaml --url=jdbc:postgresql://localhost:5432/mydatabase --username=myuser --password=mypassword --classpath=lib/postgresql-42.7.3.jar status
```

This makes executing commands much simpler and less error-prone. You can always override a property for a single command by providing it as a command-line argument, which will take precedence over the value in the properties file.

#### f. Troubleshooting: 'missing required argument' Error

A common error when using the CLI is `Error parsing command line: Invalid argument '--changelog-file': missing required argument`.

This error almost always means that the Liquibase CLI cannot find your `liquibase.properties` file. To fix this, ensure you are running the `liquibase` command from the **root directory of your project** (the same directory that contains the `lib` folder and `liquibase.properties` file).

The CLI only looks for the `liquibase.properties` file in the directory from which it is executed. If you run the command from any other directory, it will not find your configuration and will expect all parameters to be passed as command-line arguments.

### 3. Executing Rollbacks with the CLI

Here are the most common rollback commands:

*   **Roll back the last changeset applied:**

    ```bash
    liquibase rollback-count 1
    ```

*   **Roll back to a specific date and time:**

    ```bash
    liquibase rollback-to-date "2024-07-29 10:30:00"
    ```

*   **Create a tag to rollback to later:**

    First, tag the current database state:
    ```bash
    liquibase tag my_release_tag_v1.2
    ```

    Later, you can roll back all changes applied after that tag:
    ```bash
    liquibase rollback my_release_tag_v1.2
    ```

*   **Generate the rollback SQL without executing it:**

    This is useful for reviewing the rollback script before applying it.

    ```bash
    liquibase rollback-sql my_release_tag_v1.2
    ```

