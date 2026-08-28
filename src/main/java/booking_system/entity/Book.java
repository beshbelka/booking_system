package booking_system.entity;

import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import booking_system.enums.BOOK_STATUS;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    private BOOK_STATUS status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany
    @JoinColumn(name = "book_id")
    private List<Seat> seats = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    @CreationTimestamp
    private LocalTime createdAt;

    public Book (BOOK_STATUS status,
                 User user,
                 List<Seat> seats,
                 Seance seance) {
        this.status = status;
        this.user = user;
        this.seats = seats;
        this.seance = seance;
    }

    public String getSeatsFormatted() {
        return seats.stream()
                .map(seat -> seat.getRow() + "-" + seat.getNumber())
                .collect(Collectors.joining(", "));
    }

    public String getStatusFormatted() {
        switch (this.status) {
            case PAID -> {
                return "Оплачено";
            }
            case NOT_PAID -> {
                return "Не оплачено";
            }
            case CANCELLED -> {
                return "Отменено";
            }
            case FILM_SCREENING -> {
                return "В процессе";
            }
            default -> {
                return null;
            }
        }
    }

    @Transient
    public float getTotalPrice() {
        float price = 0;
        float seancePrice = seance.getPrice();
        for (Seat seat : this.seats) {
            price += (seancePrice * seat.getCoefficient());
        }
        return price;
    }
}
