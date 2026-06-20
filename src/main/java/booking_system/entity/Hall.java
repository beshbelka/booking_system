package booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hall")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToMany(mappedBy = "hall")
    private List<Seance> seances = new ArrayList<>();

    @OneToMany(mappedBy = "hall")
    private List<Seat> seats = new ArrayList<>();

}
