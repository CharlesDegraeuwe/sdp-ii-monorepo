package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.domain.Siteteam;
import hogent.sdp2.backend.domain.SiteteamId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteteamRepository extends JpaRepository<Siteteam, SiteteamId> {

    List<Siteteam> findByTeamId(Integer teamId);

    @Query("SELECT st.site.id FROM Siteteam st WHERE st.team.id = :teamId")
    List<Integer> findSiteIdsByTeamId(@Param("teamId") Integer teamId);

    List<Siteteam> findBySite(Site site);
}
