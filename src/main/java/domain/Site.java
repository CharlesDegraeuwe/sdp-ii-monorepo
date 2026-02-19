package hogent.sdp2.sdpii.domein;

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

    @Column(name = "Stad", nullable = false, length = 45)
    private String stad;

    @Column(name = "Land", nullable = false, length = 45)
    private String land;

    @Column(name = "Latitude", nullable = false, precision = 8, scale = 6)
    private BigDecimal latitude;

    @Column(name = "Longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

}