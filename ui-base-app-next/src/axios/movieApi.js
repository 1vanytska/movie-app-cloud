import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_URL || "http://34.79.215.93.nip.io/api";

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

export const searchMovies = (page, size, year, genre, directorId) => {
  return api.post("/movies/_list", {
    page: page,
    size: size,
    year: year || null,
    genre: genre || null,
    directorId: directorId || null
  });
};

export const getMovieById = (id) => api.get(`/movies/${id}`);
export const createMovie = (data) => api.post("/movies", data);
export const updateMovie = (id, data) => api.put(`/movies/${id}`, data);
export const deleteMovie = (id) => api.delete(`/movies/${id}`);

export const getAllDirectors = () => api.get("/directors");