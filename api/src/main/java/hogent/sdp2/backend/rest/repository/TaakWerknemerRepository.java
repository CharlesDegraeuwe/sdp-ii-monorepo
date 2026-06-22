package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.TaakWerknemer;
import hogent.sdp2.backend.domain.TaakWerknemerId;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaakWerknemerRepository extends JpaRepository<TaakWerknemer, TaakWerknemerId> {

    List<TaakWerknemer> findByIdTaakId(Integer taakId);

    @Transactional
    void deleteByIdTaakId(Integer taakId);
}
