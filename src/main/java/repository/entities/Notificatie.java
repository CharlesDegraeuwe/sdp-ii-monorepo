package repository.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "notificaties")
public class Notificatie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Notificatie_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Titel", nullable = false, length = 45)
    private String titel;

    @Column(name = "Bericht", nullable = false, length = 135)
    private String bericht;

    @Column(name = "Gelezen", nullable = false, length = 10)
    private String gelezen;

    @Column(name = "Datum", nullable = false)
    private LocalDate datum;

}