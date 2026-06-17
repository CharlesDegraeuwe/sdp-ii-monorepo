package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "teamwerknemer")
public class Teamwerknemer {
    @EmbeddedId private TeamwerknemerId id;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Team_ID", nullable = false)
    private Team team;

    @MapsId("werknemerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Is_Supervisor", nullable = false)
    private Boolean isSupervisor = false;
}
