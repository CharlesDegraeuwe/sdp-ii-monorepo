package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "teamkpi")
public class Teamkpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KPI_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Team_ID", nullable = false)
    private Team team;

    @Column(name = "Taken_afgerond_maand", nullable = false)
    private Integer takenAfgerondMaand;

    @Column(name = "Gemiddeld_taak_doorlooptijd", nullable = false)
    private Integer gemiddeldTaakDoorlooptijd;

}