package hogent.sdp2.backend.REST.repository;

import hogent.sdp2.backend.domain.Siteteam;
import hogent.sdp2.backend.domain.SiteteamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteteamRepository extends JpaRepository<Siteteam, SiteteamId> {

    List<Siteteam> findByTeamId(Integer teamId);

    @Query("SELECT st.site.id FROM Siteteam st WHERE st.team.id = :teamId")
    List<Integer> findSiteIdsByTeamId(@Param("teamId") Integer teamId);


}
