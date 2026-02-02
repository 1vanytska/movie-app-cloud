import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import MovieEdit from './MovieEdit';
import * as movieApi from '../../../axios/movieApi';

jest.mock('../../../axios/movieApi');

const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate,
}));

const mockDirectors = {
  data: [
    { id: 1, name: 'Christopher Nolan', country: 'USA' },
    { id: 2, name: 'Quentin Tarantino', country: 'USA' }
  ]
};

const mockMovie = {
  data: {
    id: 10,
    title: 'Dunkirk',
    year: 2017,
    genre: 'History',
    director: { id: 1, name: 'Christopher Nolan' }
  }
};

describe('MovieEdit Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    movieApi.getAllDirectors.mockResolvedValue(mockDirectors);
    movieApi.getMovieById.mockResolvedValue(mockMovie);
    movieApi.createMovie.mockResolvedValue({});
    movieApi.updateMovie.mockResolvedValue({});
  });

  test('renders in create mode', async () => {
    render(
      <MemoryRouter initialEntries={['/movies/new']}>
        <Routes>
          <Route path="/movies/:id" element={<MovieEdit />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(movieApi.getAllDirectors).toHaveBeenCalled());
    expect(screen.getByText('Create Movie')).toBeInTheDocument();
  });

  test('creates movie successfully', async () => {
    render(
      <MemoryRouter initialEntries={['/movies/new']}>
        <Routes>
          <Route path="/movies/:id" element={<MovieEdit />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(movieApi.getAllDirectors).toHaveBeenCalled());

    fireEvent.change(screen.getByLabelText(/Title/i), { target: { value: 'New Movie' } });
    fireEvent.change(screen.getByLabelText(/Year/i), { target: { value: '2023' } });
    fireEvent.change(screen.getByLabelText(/Genre/i), { target: { value: 'Comedy' } });

    const directorSelect = screen.getByLabelText(/Director/i);
    fireEvent.mouseDown(directorSelect);
    const option = await screen.findByText(/Christopher Nolan/i);
    fireEvent.click(option);

    fireEvent.click(screen.getByText('Save'));

    await waitFor(() => {
      expect(movieApi.createMovie).toHaveBeenCalledWith({
        title: 'New Movie',
        year: 2023,
        genre: 'Comedy',
        directorId: 1
      });
    });

    expect(mockedNavigate).toHaveBeenCalledWith(-1);
  });

  test('edits existing movie successfully', async () => {
    render(
      <MemoryRouter initialEntries={['/movies/10']}>
        <Routes>
          <Route path="/movies/:id" element={<MovieEdit />} />
        </Routes>
      </MemoryRouter>
    );

    expect(await screen.findByDisplayValue('Dunkirk')).toBeInTheDocument();
    
    fireEvent.click(screen.getByTestId('EditIcon'));

    fireEvent.change(screen.getByLabelText(/Title/i), { target: { value: 'Dunkirk Updated' } });

    fireEvent.click(screen.getByText('Save'));

    await waitFor(() => {
      expect(movieApi.updateMovie).toHaveBeenCalled();
    });
    
    expect(await screen.findByText('Saved successfully!')).toBeInTheDocument();
  });

  test('validation: does not save if fields are empty', async () => {
    render(
      <MemoryRouter initialEntries={['/movies/new']}>
        <Routes>
          <Route path="/movies/:id" element={<MovieEdit />} />
        </Routes>
      </MemoryRouter>
    );

    fireEvent.click(screen.getByText('Save'));

    expect(await screen.findByText('Enter title')).toBeInTheDocument();
    expect(screen.getByText('Invalid year')).toBeInTheDocument();
    
    expect(movieApi.createMovie).not.toHaveBeenCalled();
  });

  test('server error: displays error message', async () => {
    movieApi.createMovie.mockRejectedValue(new Error('Server Error'));

    render(
      <MemoryRouter initialEntries={['/movies/new']}>
        <Routes>
          <Route path="/movies/:id" element={<MovieEdit />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(movieApi.getAllDirectors).toHaveBeenCalled());

    fireEvent.change(screen.getByLabelText(/Title/i), { target: { value: 'Bad Movie' } });
    fireEvent.change(screen.getByLabelText(/Year/i), { target: { value: '2022' } });
    fireEvent.change(screen.getByLabelText(/Genre/i), { target: { value: 'Horror' } });
    
    const directorSelect = screen.getByLabelText(/Director/i);
    fireEvent.mouseDown(directorSelect);
    const option = await screen.findByText(/Christopher Nolan/i);
    fireEvent.click(option);

    fireEvent.click(screen.getByText('Save'));

    expect(await screen.findByText('Save error')).toBeInTheDocument();
  });

  test('Cancel button resets changes', async () => {
    render(
      <MemoryRouter initialEntries={['/movies/10']}>
        <Routes>
          <Route path="/movies/:id" element={<MovieEdit />} />
        </Routes>
      </MemoryRouter>
    );

    expect(await screen.findByDisplayValue('Dunkirk')).toBeInTheDocument();
    
    fireEvent.click(screen.getByTestId('EditIcon'));

    const titleInput = screen.getByLabelText(/Title/i);
    fireEvent.change(titleInput, { target: { value: 'Wrong Title' } });
    expect(titleInput.value).toBe('Wrong Title');

    fireEvent.click(screen.getByText('Cancel'));

    expect(screen.getByLabelText(/Title/i).value).toBe('Dunkirk');
  });
});