package booking_system.service;

import booking_system.entity.*;
import booking_system.exception.MovieNotFoundException;
import booking_system.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie getRandomMovie() {
        List<Movie> allMovies = movieRepository.findAll();
        if (allMovies.isEmpty()) {
            return null;
        }
        Movie movie;
        Random random = new Random();
        movie = allMovies.get(random.nextInt(allMovies.size()));
        while (!movie.isActive()) {
            random = new Random();
            movie = allMovies.get(random.nextInt(allMovies.size()));
        }
        return movie;
    }

    public long getMovieCount() {
        return movieRepository.count();
    }

    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    public Movie findById(Long id) {
        return movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
    }

    public void save(Movie movie) {
        movieRepository.save(movie);
    }

    public Movie findByTitle(String movieTitle) {
        try {
            return movieRepository.findByTitle(movieTitle);
        } catch (Exception e) {
            return null;
        }
    }
}
