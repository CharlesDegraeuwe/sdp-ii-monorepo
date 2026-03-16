package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "shifttaak")
public class Shifttaak {
    @EmbeddedId
    private ShifttaakId id;

    @MapsId("shiftId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Shift_ID", nullable = false)
    private Shift shift;

    @MapsId("taakId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Taak_ID", nullable = false)
    private Taken taak;

}