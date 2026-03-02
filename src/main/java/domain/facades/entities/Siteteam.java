package domain.facades.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "siteteam")
public class Siteteam {
    @EmbeddedId
    private SiteteamId id;

    @MapsId("siteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Site_ID", nullable = false)
    private Site site;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Team_ID", nullable = false)
    private Team team;

}