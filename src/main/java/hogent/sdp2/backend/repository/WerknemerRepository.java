package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Werknemer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WerknemerRepository extends JpaRepository<Werknemer, Integer> {
    boolean existsByEmail(String email);
    Optional<Werknemer> findByActivatieCode(String activatieCode);
    Optional<Werknemer> findByEmail(String email);
}