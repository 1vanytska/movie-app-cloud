import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import {
  Container, Grid, Card, CardContent, Typography, IconButton,
  Button, TextField, Dialog, DialogTitle, DialogActions,
  Box, Pagination, Snackbar, Alert, MenuItem
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import { searchMovies, deleteMovie, getAllDirectors } from "../../../axios/movieApi";

const MoviesList = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const initialPage = Number(searchParams.get("page")) || 1;
  const initialYear = searchParams.get("year") || "";
  const initialGenre = searchParams.get("genre") || "";
  const initialDirectorId = searchParams.get("directorId") || "";

  const [movies, setMovies] = useState([]);
  const [directorsList, setDirectorsList] = useState([]);
  const [totalPages, setTotalPages] = useState(0);

  const [filterYear, setFilterYear] = useState(initialYear);
  const [filterGenre, setFilterGenre] = useState(initialGenre);
  const [filterDirectorId, setFilterDirectorId] = useState(initialDirectorId);

  const [query, setQuery] = useState({ 
      page: initialPage, 
      year: initialYear, 
      genre: initialGenre,
      directorId: initialDirectorId 
  });

  const [deleteId, setDeleteId] = useState(null);
  const [notification, setNotification] = useState({ open: false, msg: "", severity: "success" });

  useEffect(() => {
    const flashMessage = sessionStorage.getItem("flashMessage");
    if (flashMessage) {
        setNotification({ open: true, msg: flashMessage, severity: "success" });
        sessionStorage.removeItem("flashMessage");
        const currentQuery = { ...query };
        setQuery(currentQuery);
    }
  }, []);

  useEffect(() => {
      getAllDirectors()
        .then(res => setDirectorsList(res.data))
        .catch(err => console.error("Failed to load directors", err));
  }, []);

  useEffect(() => {
    const params = {};
    if (query.page > 1) params.page = query.page;
    if (query.year) params.year = query.year;
    if (query.genre) params.genre = query.genre;
    if (query.directorId) params.directorId = query.directorId;
    
    setSearchParams(params);

    searchMovies(query.page - 1, 10, query.year, query.genre, query.directorId)
      .then((res) => {
        setMovies(res.data.content || res.data.list || []);
        setTotalPages(res.data.totalPages || 1);
      })
      .catch((err) => console.error("Error loading movies:", err));
  }, [query]);

  const capitalizeFirstLetter = (string) => {
      if (!string) return "";
      return string.charAt(0).toUpperCase() + string.slice(1);
  };

  const handleFilter = () => {
      const formattedGenre = capitalizeFirstLetter(filterGenre);
      setQuery({
          page: 1,
          year: filterYear,
          genre: formattedGenre,
          directorId: filterDirectorId
      });
  };

  const handlePageChange = (_, newPage) => {
      setQuery({ ...query, page: newPage });
  };

  const handleDelete = () => {
    if (!deleteId) return;
    deleteMovie(deleteId)
      .then(() => {
        setNotification({ open: true, msg: "Movie deleted!", severity: "success" });
        setDeleteId(null);
        const currentQuery = { ...query };
        setQuery(currentQuery); 
      })
      .catch(() => setNotification({ open: true, msg: "Delete error", severity: "error" }));
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" mb={3}>
        <Typography variant="h4">Movies</Typography>
        <Button 
            variant="contained" 
            startIcon={<AddIcon />}
            onClick={() => navigate("/movies/new")}
        >
          Add
        </Button>
      </Box>

      <Box display="flex" gap={2} mb={3} alignItems="center" flexWrap="wrap">
        <TextField 
          label="Year" 
          size="small"
          type="number"
          value={filterYear} 
          onChange={(e) => setFilterYear(e.target.value)} 
          sx={{ width: 100 }}
        />
        
        <TextField 
          label="Genre" 
          size="small"
          value={filterGenre} 
          onChange={(e) => setFilterGenre(e.target.value)}
        />

        <TextField
          select
          label="Director"
          size="small"
          value={filterDirectorId}
          onChange={(e) => setFilterDirectorId(e.target.value)}
          sx={{ minWidth: 150 }}
        >
          <MenuItem value="">
            <em>All</em>
          </MenuItem>
          {directorsList.map((director) => (
            <MenuItem key={director.id} value={director.id}>
              {director.name}
            </MenuItem>
          ))}
        </TextField>

        <Button variant="outlined" onClick={handleFilter}>
            Filter
        </Button>
      </Box>

      <Grid container spacing={2}>
        {movies.map((movie) => (
          <Grid item xs={12} sm={6} md={4} key={movie.id}>
            <Card 
              sx={{ 
                cursor: "pointer", position: "relative",
                "&:hover .delete-btn": { opacity: 1 },
                "&:hover": { boxShadow: 6 }
              }}
              onClick={() => navigate(`/movies/${movie.id}`)}
            >
              <CardContent>
                <Typography 
                    variant="h6" 
                    title={movie.title}
                    sx={{ 
                        fontWeight: 'bold',
                        height: "2.4em",           
                        overflow: "hidden",        
                        textOverflow: "ellipsis",  
                        display: "-webkit-box",
                        WebkitLineClamp: "2",      
                        WebkitBoxOrient: "vertical",
                        lineHeight: "1.2em",       
                        marginBottom: 1            
                    }}
                >
                    {movie.title}
                </Typography>

                <Typography variant="body2" color="textSecondary">
                    Year: {movie.year}
                </Typography>
                
                <IconButton 
                  className="delete-btn" 
                  color="error"
                  sx={{ position: "absolute", top: 5, right: 5, opacity: 0, transition: "0.2s" }}
                  onClick={(e) => { e.stopPropagation(); setDeleteId(movie.id); }}
                >
                  <DeleteIcon />
                </IconButton>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      
      <Box mt={3} display="flex" justifyContent="center">
        <Pagination 
            count={totalPages} 
            page={query.page} 
            onChange={handlePageChange} 
        />
      </Box>

      <Dialog open={!!deleteId} onClose={() => setDeleteId(null)}>
        <DialogTitle>Delete movie?</DialogTitle>
        <DialogActions>
          <Button onClick={() => setDeleteId(null)}>No</Button>
          <Button onClick={handleDelete} color="error">Yes, delete</Button>
        </DialogActions>
      </Dialog>

      <Snackbar open={notification.open} autoHideDuration={3000} onClose={() => setNotification({...notification, open: false})}>
        <Alert severity={notification.severity}>{notification.msg}</Alert>
      </Snackbar>
    </Container>
  );
};

export default MoviesList;