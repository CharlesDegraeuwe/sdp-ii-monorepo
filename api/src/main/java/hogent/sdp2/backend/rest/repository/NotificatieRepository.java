package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Notificatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificatieRepository extends JpaRepository<Notificatie, Integer> {
    List<Notificatie> findByWerknemerIdOrderByDatumDesc(Integer werknemerId);
    long countByWerknemerIdAndGelezen(Integer werknemerId, String gelezen);
}