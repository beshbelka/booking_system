package booking_system.service;

import booking_system.entity.Movie;
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
        Random random = new Random();
        return allMovies.get(random.nextInt(allMovies.size()));
    }

    public long getMovieCount() {
        return movieRepository.count();
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie findById(Long id) {
        return movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
    }
}
