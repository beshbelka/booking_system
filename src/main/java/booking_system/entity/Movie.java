package booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String description;

    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER)
    private List<Seance> seances;

    private Integer duration;
    private String genre;
    private String ageRating;

    private String posterUrl;
    private String backdropUrl;

    @Column(nullable = false)
    private boolean active = true;

    public String getFormattedDuration() {
        if (duration == null) return "—";
        int hours = duration / 60;
        int mins = duration % 60;

        if (hours == 0) {
            return mins + " мин";
        } else if (mins == 0) {
            return hours + " ч";
        } else {
            return hours + " ч " + mins + " мин";
        }
    }

}
