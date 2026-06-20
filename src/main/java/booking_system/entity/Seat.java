package booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;

@Entity
@Table (name = "seat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}
