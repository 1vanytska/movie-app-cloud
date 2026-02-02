import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import MoviesList from './MoviesList';
import * as movieApi from '../../../axios/movieApi';

jest.mock('../../../axios/movieApi');

const mockMovies = {
  data: {
    content: [
      { id: 1, title: 'Inception', year: 2010, genre: 'Sci-Fi' },
      { id: 2, title: 'The Godfather', year: 1972, genre: 'Crime' }
    ],
    totalPages: 1
  }
};

const mockDirectors = {
  data: [
    { id: 100, name: 'Christopher Nolan' },
    { id: 101, name: 'Francis Ford Coppola' }
  ]
};

describe('MoviesList Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    movieApi.searchMovies.mockResolvedValue(mockMovies);
    movieApi.getAllDirectors.mockResolvedValue(mockDirectors);
    movieApi.deleteMovie.mockResolvedValue({});
    window.sessionStorage.clear();
  });

  test('renders movies and loads directors', async () => {
    render(
      <MemoryRouter>
        <MoviesList />
      </MemoryRouter>
    );

    await waitFor(() => expect(movieApi.getAllDirectors).toHaveBeenCalled());

    expect(await screen.findByText('Inception')).toBeInTheDocument();
    expect(screen.getByText('Year: 2010')).toBeInTheDocument();
  });

  test('filtering calls API with correct parameters', async () => {
    render(
      <MemoryRouter>
        <MoviesList />
      </MemoryRouter>
    );

    await screen.findByText('Inception');

    const yearInput = screen.getByLabelText(/Year/i);
    fireEvent.change(yearInput, { target: { value: '2023' } });

    const filterBtn = screen.getByRole('button', { name: /Filter/i });
    fireEvent.click(filterBtn);

    await waitFor(() => {
      expect(movieApi.searchMovies).toHaveBeenCalledWith(0, 10, '2023', '', '');
    });
  });

  test('shows Flash Message from sessionStorage', async () => {
    window.sessionStorage.setItem('flashMessage', 'Test Success Message');

    render(
      <MemoryRouter>
        <MoviesList />
      </MemoryRouter>
    );

    expect(await screen.findByText('Test Success Message')).toBeInTheDocument();
    expect(window.sessionStorage.getItem('flashMessage')).toBeNull();
  });

  test('deletes movie after confirmation', async () => {
    render(
      <MemoryRouter>
        <MoviesList />
      </MemoryRouter>
    );

    await screen.findByText('Inception');

    const deleteIcons = screen.getAllByTestId('DeleteIcon');
    fireEvent.click(deleteIcons[0]);

    expect(screen.getByText('Delete movie?')).toBeInTheDocument();

    const confirmBtn = screen.getByText('Yes, delete');
    fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(movieApi.deleteMovie).toHaveBeenCalledWith(1);
    });
  });
});