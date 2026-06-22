package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Shift_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Team_ID", nullable = true)
    private Team team;

    @Column(name = "Start_datum", nullable = false)
    private LocalDate startDatum;

    @Column(name = "Eind_datum", nullable = false)
    private LocalDate eindDatum;

    @Column(name = "Start_tijd")
    private LocalTime startTijd;

    @Column(name = "Eind_tijd")
    private LocalTime eindTijd;

    @Column(name = "Pauze_start")
    private LocalTime pauzeStart;

    @Column(name = "Pauze_eind")
    private LocalTime pauzeEind;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;
}
