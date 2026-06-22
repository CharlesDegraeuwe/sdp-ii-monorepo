package hogent.sdp2.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TaakWerknemerId implements Serializable {

    @Column(name = "Taak_ID")
    private Integer taakId;

    @Column(name = "Werknemer_ID")
    private Integer werknemerId;
}
