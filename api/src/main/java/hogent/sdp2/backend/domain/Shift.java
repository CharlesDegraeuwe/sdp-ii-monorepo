package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Shift_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Team_ID", nullable = false)
    private Team team;

    @Column(name = "Start_datum", nullable = false)
    private LocalDate startDatum;

    @Column(name = "Eind_datum", nullable = false)
    private LocalDate eindDatum;

}