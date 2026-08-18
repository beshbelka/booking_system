package booking_system.entity;

import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import booking_system.enums.BOOK_STATUS;

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
    @JoinColumn(name = "book_id", nullable = false)
    private List<Seat> seats = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

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

    public float getTotalPrice() {
        return (float) seats.stream()
                .mapToDouble(Seat::getPrice)
                .sum();
    }
}
