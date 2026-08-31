package booking_system.service;

import booking_system.DTO.MovieFinanceRequest;
import booking_system.entity.*;
import booking_system.exception.MovieNotFoundException;
import booking_system.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final SeatService seatService;
    private final BookService bookService;

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

    public List<Movie> findAll() {
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

    public List<MovieFinanceRequest> finance() {
        try {
            List<Movie> movies = movieRepository.findAll();
            List<MovieFinanceRequest> movieFinanceRequests = new ArrayList<>();
            for (Movie movie : movies) {
                Long ticketCount = seatService.countByMovie(movie);
                float total = bookService.total(movie);
                float averagePrice = bookService.averagePrice(total, movie);
                MovieFinanceRequest request = new MovieFinanceRequest(
                        movie.id,
                        movie.getTitle(),
                        ticketCount,
                        total,
                        averagePrice
                );
                movieFinanceRequests.add(request);
            }
            return movieFinanceRequests;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
