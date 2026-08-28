package booking_system.controller;

import booking_system.DTO.AddMovieRequest;
import booking_system.DTO.ApiResponse;
import booking_system.DTO.AdminDeleteRequest;
import booking_system.DTO.MovieUpdateRequest;
import booking_system.entity.Movie;
import booking_system.service.DeleteService;
import booking_system.service.MovieService;
import booking_system.service.UserService;
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
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DeleteService deleteService;
    private final MovieService movieService;

    private static final String POSTER_DIR = "src/main/resources/static/images/posters/";
    private static final String BACKDROP_DIR = "src/main/resources/static/images/backdrops/";

    @DeleteMapping("/control-films-delete")
    @Transactional
    public ResponseEntity<ApiResponse> deleteFilm(@RequestBody AdminDeleteRequest request) {
        try {
            Long movieId = request.id();
            if (movieId == 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, "ID не может равняться нулю"));
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
            if (userId == null) return ResponseEntity.status(400).body(ApiResponse.error(400, "ID не может равняться нулю"));
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
            if (movieId == 0) return ResponseEntity.status(400).body(ApiResponse.error(400, "ID не может равняться нулю"));
            Movie movie = movieService.findById(movieId);
            if (request.title() != null && !request.title().isEmpty()) movie.setTitle(request.title());
            if (request.description() != null && !request.description().isEmpty()) movie.setDescription(request.description());
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
}
