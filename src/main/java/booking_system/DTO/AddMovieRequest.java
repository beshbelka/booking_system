package booking_system.DTO;

import org.springframework.web.multipart.MultipartFile;

public record AddMovieRequest(
        String title,
        String description,
        Integer duration,
        String genre,
        String ageRating,
        MultipartFile posterFile,
        MultipartFile backdropFile,
        boolean active
) {
}
