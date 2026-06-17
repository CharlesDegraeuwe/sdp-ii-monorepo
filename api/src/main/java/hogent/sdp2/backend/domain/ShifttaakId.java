package hogent.sdp2.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ShifttaakId implements Serializable {
    private static final long serialVersionUID = 757884138956322449L;

    @Column(name = "Shift_ID", nullable = false)
    private Integer shiftId;

    @Column(name = "Taak_ID", nullable = false)
    private Integer taakId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShifttaakId entity = (ShifttaakId) o;
        return Objects.equals(this.shiftId, entity.shiftId)
                && Objects.equals(this.taakId, entity.taakId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId, taakId);
    }
}
