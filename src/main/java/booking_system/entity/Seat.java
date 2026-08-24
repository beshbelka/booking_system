package booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;

@Entity
@Table(name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hall_id", "row", "number", "seance_id"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private short row;
    private short number;

    @Enumerated(EnumType.STRING)
    private SEAT_STATUS status;

    private float price;

    @Enumerated(EnumType.STRING)
    private SEAT_TYPE type;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    public Seat (short row,
                 short number,
                 SEAT_STATUS status,
                 float price,
                 SEAT_TYPE type,
                 Hall hall,
                 Seance seance) {
        this.row = row;
        this.number = number;
        this.status = status;
        this.price = price;
        this.type = type;
        this.hall = hall;
        this.seance = seance;
    }
}
