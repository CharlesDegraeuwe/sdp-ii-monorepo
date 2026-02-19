package hogent.sdp2.sdpii.domein;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "werknemerstatistieken")
public class Werknemerstatistieken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Statistiek_ID", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Totaal_uren_gewerkt", nullable = false)
    private Integer totaalUrenGewerkt;

    @Column(name = "Taken_toegewezen", nullable = false)
    private Integer takenToegewezen;

    @Column(name = "Taken_afgerond", nullable = false)
    private Integer takenAfgerond;

    @Column(name = "Afwezigheidsdagen", nullable = false)
    private Integer afwezigheidsdagen;

    @Column(name = "Verlofdagen_opgenomen", nullable = false)
    private Integer verlofdagenOpgenomen;

    @Column(name = "Verlofdagen_resterend", nullable = false)
    private Integer verlofdagenResterend;

}