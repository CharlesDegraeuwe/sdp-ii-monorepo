package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Site;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    boolean existsByNaam(String naam);

    @Query(
            "SELECT DISTINCT st.site FROM Siteteam st "
                    + "JOIN Teamwerknemer tw ON st.team.id = tw.team.id "
                    + "WHERE tw.werknemer.id = :werknemerId")
    List<Site> findSitesByWerknemerId(@Param("werknemerId") Integer werknemerId);

    long countByStatus(String status);
}
