package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "taakwerknemer")
public class TaakWerknemer {
    @EmbeddedId private TaakWerknemerId id = new TaakWerknemerId();

    @MapsId("taakId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Taak_ID", nullable = false)
    private Taken taken;

    @MapsId("werknemerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;
}
