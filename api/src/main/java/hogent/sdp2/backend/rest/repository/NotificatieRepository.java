package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Notificaty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificatieRepository extends JpaRepository<Notificaty, Integer> {
    List<Notificaty> findByWerknemerIdOrderByDatumDesc(Integer werknemerId);
    long countByWerknemerIdAndGelezen(Integer werknemerId, String gelezen);
}