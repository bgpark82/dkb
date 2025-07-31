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
