package booking_system.service;

import booking_system.entity.Movie;
import booking_system.repository.MovieRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMoviesWithSeances() {
        return movieRepository.findAll();
    }

    // сеанс недели
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
        return movieRepository.findById(id).orElseThrow(() -> new RuntimeException("movie not found"));
    }
}
