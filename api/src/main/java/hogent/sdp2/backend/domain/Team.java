package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Team_ID", nullable = false)
    private Integer id;

    @Column(name = "Naam", nullable = false, length = 45)
    private String naam;

    @Column(name = "Beschrijving", length = 255)
    private String beschrijving;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Manager_ID")
    private Werknemer manager;
}
