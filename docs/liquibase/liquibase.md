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
