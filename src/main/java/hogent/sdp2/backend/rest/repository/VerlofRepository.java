package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Verlof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerlofRepository extends JpaRepository<Verlof, Integer> {
    List<Verlof> findByWerknemerId(Integer werknemerId);
}
