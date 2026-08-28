package booking_system.controller;

import booking_system.DTO.*;
import booking_system.entity.Hall;
import booking_system.entity.Movie;
import booking_system.entity.Seance;
import booking_system.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DeleteService deleteService;
    private final MovieService movieService;

    private static final String POSTER_DIR = "src/main/resources/static/images/posters/";
    private static final String BACKDROP_DIR = "src/main/resources/static/images/backdrops/";
    private final HallService hallService;
    private final SeanceService seanceService;
    private final SeatService seatService;

    @DeleteMapping("/control-films-delete")
    @Transactional
    public ResponseEntity<ApiResponse> deleteFilm(@RequestBody AdminDeleteRequest request) {
        try {
            Long movieId = request.id();
            if (movieId == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "ID не может равняться нулю"));
            ApiResponse apiResponse = deleteService.deleteMovie(movieId);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error());
        }
    }

    @DeleteMapping("/control-users-delete")
    @Transactional
    public ResponseEntity<ApiResponse> deleteUser(@RequestBody AdminDeleteRequest request) {
        try {
            Long userId = request.id();
            if (userId == null)
                return ResponseEntity.status(400).body(ApiResponse.error(400, "ID не может равняться нулю"));
            ApiResponse apiResponse = deleteService.deleteAccount(userId);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }

    @PostMapping(path = "/control-films-add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> addMovie(@ModelAttribute AddMovieRequest request) {
        try {
            Movie movie = new Movie();
            movie.setTitle(request.title());
            movie.setDescription(request.description());
            movie.setDuration(request.duration());
            movie.setGenre(request.genre());
            movie.setAgeRating(request.ageRating());
            if (request.posterFile() != null && !request.posterFile().isEmpty()) {
                String posterPath = saveFile(request.posterFile(), POSTER_DIR, "posters");
                movie.setPosterUrl(posterPath);
            }
            if (request.backdropFile() != null && !request.backdropFile().isEmpty()) {
                String backdropPath = saveFile(request.backdropFile(), BACKDROP_DIR, "backdrops");
                movie.setBackdropUrl(backdropPath);
            }
            movie.setActive(request.active());
            movieService.save(movie);
            return ResponseEntity.ok().body(ApiResponse.success("add movie ok"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error());
        }
    }

    private String saveFile(MultipartFile file, String directory, String subdirectory) throws IOException {
        Path dirPath = Paths.get(directory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        Path filePath = Paths.get(directory + filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + subdirectory + "/" + filename;
    }

    @PostMapping(path = "/control-films-edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> editMovie(@ModelAttribute MovieUpdateRequest request) {
        try {
            Long movieId = request.movieId();
            if (movieId == 0)
                return ResponseEntity.status(400).body(ApiResponse.error(400, "ID не может равняться нулю"));
            Movie movie = movieService.findById(movieId);
            if (request.title() != null && !request.title().isEmpty()) movie.setTitle(request.title());
            if (request.description() != null && !request.description().isEmpty())
                movie.setDescription(request.description());
            if (request.duration() != null && request.duration() != 0) movie.setDuration(request.duration());
            if (request.genre() != null && !request.genre().isEmpty()) movie.setGenre(request.genre());
            if (request.ageRating() != null && !request.ageRating().isEmpty()) movie.setAgeRating(request.ageRating());
            if (request.active() != null) movie.setActive(request.active());
            if (request.posterFile() != null && !request.posterFile().isEmpty()) {
                String posterPath = saveFile(request.posterFile(), POSTER_DIR, "posters");
                movie.setPosterUrl(posterPath);
            }
            if (request.backdropFile() != null && !request.backdropFile().isEmpty()) {
                String backdropPath = saveFile(request.backdropFile(), BACKDROP_DIR, "backdrops");
                movie.setBackdropUrl(backdropPath);
            }
            movieService.save(movie);
            return ResponseEntity.ok().body(ApiResponse.success("edit movie ok"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error());
        }
    }

    @GetMapping("/control-seances-get-data")
    public ResponseEntity<ApiResponse> getMoviesAndCountHalls() {
        try {
            HashMap<String, String> data = new HashMap<>();
            List<Movie> movies = movieService.getAll();
            for (Movie movie : movies) {
                data.put(String.valueOf(movie.getId()), movie.getTitle());
            }
            long count = hallService.getHallCount();
            data.put("countHalls", String.valueOf(count));
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }

    @GetMapping("/control-seances-add-data")
    public ResponseEntity<ApiResponse> getMoviesWithDurationAndCountHalls() {
        try {
            HashMap<String, String> data = new HashMap<>();
            List<Movie> movies = movieService.getAll();
            for (Movie movie : movies) {
                data.put(movie.getTitle(), String.valueOf(movie.getFormattedDuration()));
            }
            long count = hallService.getHallCount();
            data.put("countHalls", String.valueOf(count));
            System.out.println(data);
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }

    @GetMapping("/control-seances-find")
    public ResponseEntity<List<SeanceResponse>> findSeances(@RequestParam Long movieId, @RequestParam Long hallId) {
        try {
            if (movieId == null || hallId == null || movieId < 0 || hallId < 0) return ResponseEntity.status(400).build();
            List<SeanceResponse> seanceResponses = new ArrayList<>();
            List<Seance> seances;
            if (movieId != 0) {
                if (hallId != 0) {
                    seances = seanceService.findByMovieIdAndHallId(movieId, hallId);
                } else {
                    seances = seanceService.findByMovieId(movieId);
                }
            } else {
                if (hallId != 0) {
                    seances = seanceService.findByHallId(hallId);
                } else {
                    seances = seanceService.findAll();
                }
            }
            for (Seance seance : seances) {
                SeanceResponse seanceResponse = new SeanceResponse(
                        seance.getId(),
                        seance.getStart_time(),
                        seance.getEnd_time(),
                        seance.isAvailable(),
                        seance.getHall().getTotalSeats(),
                        seatService.getBookedSeatsCount(seance),
                        seance.isCancelled());
                if (seance.isAvailable()) System.out.println("AVAILABLE SEANCE: " + seance.getId());
                seanceResponses.add(seanceResponse);
            }
            return ResponseEntity.ok().body(seanceResponses);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/control-seances-delete")
    public ResponseEntity<ApiResponse> cancelSeance(@RequestParam Long seanceId) {
        try {
            if (seanceId <= 0) return ResponseEntity.status(400).body(ApiResponse.error(400, "Некорректный ID"));
            ApiResponse apiResponse = deleteService.cancelSeance(seanceId);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok().body(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }

    @PostMapping("/control-seances-add")
    public ResponseEntity<ApiResponse> addSeance(@RequestBody AddSeanceRequest request) {
        try {
            Movie movie = movieService.findByTitle(request.movieTitle());
            if (movie == null) return ResponseEntity.status(404).body(ApiResponse.error(404, "Фильм не найден"));
            Hall hall = hallService.findById(request.hallId());
            if (hall == null) return ResponseEntity.status(404).body(ApiResponse.error(404, "Зал не найден"));
            if (!hallService.isHallAvailable(0L, request.hallId(), request.start_time(), request.start_time().plusMinutes(movie.getDuration()))) {
                return ResponseEntity.status(409).body(ApiResponse.error(409, "Зал занят в это время"));
            }
            Seance seance = new Seance();
            seance.setStart_time(request.start_time());
            seance.setEnd_time(request.start_time().plusMinutes(movie.getDuration()));
            seance.setMovie(movie);
            seance.setHall(hall);
            seance.setPrice(request.price());
            seance.setCancelled(false);
            seanceService.save(seance);
            movie.getSeances().add(seance);
            movieService.save(movie);
            hall.getSeances().add(seance);
            hallService.save(hall);
            return ResponseEntity.ok().body(ApiResponse.success("add seance ok"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }

    @PostMapping("/control-seances-edit")
    public ResponseEntity<ApiResponse> editSeance(@RequestBody EditSeanceRequest request) {
        try {
            if (request.seanceId() <= 0) return ResponseEntity.status(400).body(ApiResponse.error(400, "Некорректный ID"));
            Seance seance = seanceService.findById(request.seanceId());
            Hall hall = seance.getHall();
            Movie movie = seance.getMovie();
            LocalTime endTime = request.start_time().plusMinutes(movie.getDuration());
            if (!hallService.isHallAvailable(request.seanceId(), hall.id, request.start_time(), endTime)) return ResponseEntity.status(409).body(ApiResponse.error(409, "Зал занят в это время"));
            seance.setStart_time(request.start_time());
            seance.setEnd_time(endTime);
            seanceService.save(seance);
            return ResponseEntity.ok().body(ApiResponse.success("edit seance ok"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }
}
