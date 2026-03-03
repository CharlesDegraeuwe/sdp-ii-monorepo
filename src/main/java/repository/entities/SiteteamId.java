package repository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SiteteamId implements Serializable {
    private static final long serialVersionUID = 1444990995349335052L;
    @Column(name = "Site_ID", nullable = false)
    private Integer siteId;

    @Column(name = "Team_ID", nullable = false)
    private Integer teamId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteteamId entity = (SiteteamId) o;
        return Objects.equals(this.teamId, entity.teamId) &&
                Objects.equals(this.siteId, entity.siteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, siteId);
    }

}