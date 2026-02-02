import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
    Container, TextField, Button, Box, Typography, Paper,
    IconButton, Snackbar, Alert, MenuItem, FormControl, InputLabel, Select, FormHelperText
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { createMovie, getMovieById, updateMovie, getAllDirectors } from "../../../axios/movieApi";

const MovieEdit = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isNew = id === "new";

    const [mode, setMode] = useState(isNew ? "edit" : "view");
    const [formData, setFormData] = useState({
        title: "", year: "", genre: "", directorId: ""
    });
    const [directorsList, setDirectorsList] = useState([]);
    const [errors, setErrors] = useState({});
    const [notification, setNotification] = useState({ open: false, msg: "", severity: "success" });
    const [originalData, setOriginalData] = useState(null);

    useEffect(() => {
        getAllDirectors().then(res => setDirectorsList(res.data)).catch(console.error);

        if (!isNew) {
            getMovieById(id).then((res) => {
                const movie = res.data;
                const data = {
                    title: movie.title,
                    year: movie.year,
                    genre: movie.genre,
                    directorId: movie.director ? movie.director.id : ""
                };
                setFormData(data);
                setOriginalData(data);
            });
        }
    }, [id, isNew]);

    const validate = () => {
        const newErrors = {};
        if (!formData.title) newErrors.title = "Enter title";
        if (!formData.year || formData.year < 1888) newErrors.year = "Invalid year";
        if (!formData.genre) newErrors.genre = "Enter genre";
        if (!formData.directorId) newErrors.directorId = "Select director";
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = () => {
        if (!validate()) return;

        const payload = {
            title: formData.title,
            year: Number(formData.year),
            genre: formData.genre,
            directorId: formData.directorId
        };

        const request = isNew ? createMovie(payload) : updateMovie(id, payload);

        request.then(() => {
            sessionStorage.setItem("flashMessage", isNew ? "Movie created successfully!" : "Movie updated successfully!");

            if (isNew) {
                navigate(-1);
            } else {
                setNotification({ open: true, msg: "Saved successfully!", severity: "success" });
                setOriginalData(formData);
                setMode("view");
            }
        }).catch(() => setNotification({ open: true, msg: "Save error", severity: "error" }));
    };

    const handleCancel = () => {
        if (isNew) navigate(-1);
        else {
            setFormData(originalData);
            setErrors({});
            setMode("view");
        }
    };

    return (
        <Container maxWidth="sm" sx={{ mt: 4 }}>
            <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mb: 2 }}>
                Back
            </Button>

            <Paper sx={{ p: 4 }}>
                <Box display="flex" justifyContent="space-between" mb={3}>
                    <Typography variant="h5">{isNew ? "Create Movie" : formData.title}</Typography>
                    {!isNew && mode === "view" && (
                        <IconButton onClick={() => setMode("edit")}> <EditIcon /> </IconButton>
                    )}
                </Box>

                <Box display="flex" flexDirection="column" gap={2}>
                    <TextField
                        label="Title" value={formData.title}
                        onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                        disabled={mode === "view"}
                        error={!!errors.title} helperText={errors.title}
                    />
                    <TextField
                        label="Year" type="number" value={formData.year}
                        onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                        disabled={mode === "view"}
                        error={!!errors.year} helperText={errors.year}
                    />
                    <TextField
                        label="Genre" value={formData.genre}
                        onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
                        disabled={mode === "view"}
                        error={!!errors.genre} helperText={errors.genre}
                    />

                    <FormControl fullWidth error={!!errors.directorId} disabled={mode === "view"}>
                        <InputLabel id="director-select-label">Director</InputLabel>
                        <Select
                            labelId="director-select-label"
                            id="director-select"

                            value={formData.directorId || ""}
                            label="Director"
                            onChange={(e) => setFormData({ ...formData, directorId: e.target.value })}
                        >
                            {directorsList.map((dir) => (
                                <MenuItem key={dir.id} value={dir.id}>
                                    {dir.name} ({dir.country || "World"})
                                </MenuItem>
                            ))}
                        </Select>
                        {errors.directorId && <FormHelperText>{errors.directorId}</FormHelperText>}
                    </FormControl>

                    {mode === "edit" && (
                        <Box display="flex" gap={2} justifyContent="flex-end" mt={2}>
                            <Button onClick={handleCancel}>Cancel</Button>
                            <Button variant="contained" onClick={handleSave}>Save</Button>
                        </Box>
                    )}
                </Box>
            </Paper>

            <Snackbar open={notification.open} autoHideDuration={3000} onClose={() => setNotification({ ...notification, open: false })}>
                <Alert severity={notification.severity}>{notification.msg}</Alert>
            </Snackbar>
        </Container>
    );
};

export default MovieEdit;