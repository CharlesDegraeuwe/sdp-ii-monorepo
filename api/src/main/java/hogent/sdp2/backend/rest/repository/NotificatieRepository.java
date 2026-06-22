package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Notificatie;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificatieRepository extends JpaRepository<Notificatie, Integer> {
    @Query("SELECT n FROM Notificatie n WHERE n.werknemer.id = :werknemerId ORDER BY n.datum DESC")
    List<Notificatie> findByWerknemerIdOrderByDatumDesc(@Param("werknemerId") Integer werknemerId);

    long countByWerknemerIdAndGelezen(Integer werknemerId, String gelezen);

    boolean existsByWerknemerIdAndTitelAndReferentieId(
            Integer werknemerId, String titel, Integer referentieId);
}
