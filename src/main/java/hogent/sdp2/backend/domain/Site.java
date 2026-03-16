package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "site")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Site_ID", nullable = false)
    private Integer id;

    @Column(name = "Naam", nullable = false, length = 45)
    private String naam;

    @Column(name = "Locatie", nullable = false, length = 120)
    private String locatie;

    @Column(name = "Capaciteit", nullable = false)
    private Integer capaciteit;

    @Column(name = "Status", nullable = false, length = 45)
    private String status;
}