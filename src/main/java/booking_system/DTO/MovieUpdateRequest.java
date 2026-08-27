package booking_system.DTO;

import org.springframework.web.multipart.MultipartFile;

public record MovieUpdateRequest(
        Long movieId,
        String title,
        String description,
        String genre,
        Integer duration,
        String ageRating,
        Boolean active,
        MultipartFile posterFile,
        MultipartFile backdropFile
) {
}
