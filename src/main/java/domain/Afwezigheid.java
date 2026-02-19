package hogent.sdp2.sdpii.domein;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "afwezigheid")
public class Afwezigheid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Afwezigheid_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Start_datum", nullable = false)
    private LocalDate startDatum;

    @Column(name = "Eind_datum", nullable = false)
    private LocalDate eindDatum;

    @Column(name = "Reden", nullable = false, length = 90)
    private String reden;

}