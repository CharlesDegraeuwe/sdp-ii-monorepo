package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query("SELECT t FROM Team t JOIN Siteteam st ON st.team.id = t.id WHERE st.site.id = :siteId")
    List<Team> findBySiteId(@Param("siteId") Integer siteId);
}
