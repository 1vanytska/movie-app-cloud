# Movies Management System

This web application allows users to manage a collection of movies. The application provides functionality to view, create, edit, delete, and filter movies interacting with a Java Spring Boot backend.

## Features

* **Movie List:** Displays a paginated list of movies with essential details.
* **Filtering:** Server-side filtering by Year, Genre, and Director.
* **CRUD Operations:**
    * Create new movies with form validation.
    * Edit existing movie details.
    * Delete movies with a confirmation dialog.
* **User Interface:**
    * Responsive design using Material UI.
    * Flash messages (notifications) for successful actions.
    * Navigation logic that preserves filter and pagination states when returning from the details page.

## Technology Stack

* **Frontend:** React.js, React Router v6, Material UI, Axios.
* **Testing:** Jest, React Testing Library.
* **Backend:** Java Spring Boot (REST API).

## Setup and Execution

### 1. Prerequisites

* Node.js and npm installed.
* The Backend API server must be running (default expected port is 8082).

### 2. Environment Configuration

Create a file named .env in the root directory of the project (next to package.json) and add the following configuration:

```
PORT=3050
REACT_APP_API_URL=http://localhost:8080/api
```

* PORT: The port where the React application will run.
* REACT_APP_API_URL: The base URL of your Spring Boot backend.

### 3. Installation

Open a terminal in the project folder and install the dependencies:

```
npm install
```

### 4. Running the Application

To start the application in development mode:

```
npm start
```

The application will be available at http://localhost:3050.

## Testing

The project includes unit tests covering component rendering, business logic, and user interactions.

To run the tests:

```
npm test
```

### Test Coverage
* **MoviesList:** Verifies rendering, API calls for loading data, filtering logic, and delete operations.
* **MovieEdit:** Verifies form rendering, validation logic, mode switching (create/edit), and data submission.

## Project Structure

Key directories and files:

* src/axios/movieApi.js - Axios instance configuration and API endpoint definitions.
* src/containers/Movies/MoviesList.jsx - Component for displaying the list of movies with filters.
* src/containers/Movies/MovieEdit.jsx - Component for creating and editing movies.
* src/containers/Movies/*.test.jsx - Corresponding unit tests for the components.