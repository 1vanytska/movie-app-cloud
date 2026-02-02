# Movie API
## Overview
This is a Spring Boot REST API for managing movies and directors.
It supports full CRUD operations, pagination, CSV report generation, and bulk import from JSON.

## Domain Model
### Movie (Entity 1)
Represents a film record in the system. Each movie is linked to one director.

| Field         | Type     | Description                                 |
|---------------|----------|---------------------------------------------|
| `id`          | Long     | Unique identifier                           |
| `title`       | String   | Movie title (required, max 255 characters)  |
| `year`        | Integer  | Release year (required)                     |
| `genre`       | String   | Genre of the movie (required)               |
| `director_id` | Long     | Foreign key referencing a director (required)|

---

### Director (Entity 2)
Represents a film director. Each director can be linked to multiple movies.

| Field     | Type     | Description                                 |
|-----------|----------|---------------------------------------------|
| `id`      | Long     | Unique identifier                           |
| `name`    | String   | Director's name (required, must be unique)  |
| `country` | String   | Country of origin (optional)                |

This structure reflects a many-to-one relationship:
- Each Movie is associated with one Director.
- Each Director can be associated with many Movies.

## API Endpoints
### Movies (/api/movies)
- POST / – Create a movie
- GET /{id} – Get movie by ID (includes director info)
- PUT /{id} – Update movie
- DELETE /{id} – Delete movie
- GET / – Get all movies
- POST /_list – Paginated list with optional filters
- POST /_report – Generate CSV report
- POST /upload – Bulk import movies from JSON
### Directors (/api/directors)
- GET / – Get all directors
- POST / – Create a director (name must be unique)
- PUT /{id} – Update director
- DELETE /{id} – Delete director

## Sample Data
A sample JSON file for movie import is located at:

`src/main/resources/data/movies.json`

## Testing
All endpoints are covered by integration tests using JUnit and MockMvc.
To run tests:

`mvn test`

## Database Setup
Liquibase migration scripts automatically create the database schema and insert initial director data.
