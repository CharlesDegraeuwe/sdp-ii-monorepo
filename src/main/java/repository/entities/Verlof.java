package repository.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "verlof")
public class Verlof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Verlof_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Goedkeurder_ID", nullable = false)
    private Integer goedkeurderId;

    @Column(name = "Start_datum", nullable = false)
    private LocalDate startDatum;

    @Column(name = "Eind_datum", nullable = false)
    private LocalDate eindDatum;

    @Column(name = "Status", nullable = false, length = 45)
    private String status;

    @Column(name = "Type", nullable = false, length = 45)
    private String type;

}