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