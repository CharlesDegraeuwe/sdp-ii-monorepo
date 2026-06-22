package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "taken")
public class Taken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Taak_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Titel", nullable = false, length = 45)
    private String titel;

    @Column(name = "Beschrijving", nullable = false, length = 90)
    private String beschrijving;

    @Column(name = "Afgewerkt", nullable = false, length = 45)
    private String afgewerkt;

    @Column(name = "Deadline", nullable = false)
    private LocalDate deadline;

    // Deze mogen nu leeg (null) zijn voor je oude taken
    @Column(name = "startuur")
    private LocalTime startuur;

    @Column(name = "einduur")
    private LocalTime einduur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Site_ID")
    private Site site;
}
