# Running the database with Docker

This project's MySQL database can be started with Docker Compose instead of installing MySQL manually.

## Start the database

```
docker-compose up -d
```

This starts a MySQL 8.0 container with:
- database: `pet_adoption_main_db`
- port: `3306` (matches `application.properties`)
- root password: `12345` by default (override with the `MYSQL_ROOT_PASSWORD` environment variable)
- data persisted in a named Docker volume (`mysql_data`)

Once the container is running, start the Spring Boot app normally — it will connect to `localhost:3306` automatically.

## Stop the database

```
docker-compose down
```

Add `-v` to also delete the stored data:

```
docker-compose down -v
```
