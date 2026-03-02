package domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class TeamwerknemerId implements Serializable {
    private static final long serialVersionUID = 5551010681777690664L;
    @Column(name = "Team_ID", nullable = false)
    private Integer teamId;

    @Column(name = "Werknemer_ID", nullable = false)
    private Integer werknemerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamwerknemerId entity = (TeamwerknemerId) o;
        return Objects.equals(this.werknemerId, entity.werknemerId) &&
                Objects.equals(this.teamId, entity.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(werknemerId, teamId);
    }

}