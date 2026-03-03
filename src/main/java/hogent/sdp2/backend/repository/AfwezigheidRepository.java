package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Afwezigheid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AfwezigheidRepository extends JpaRepository<Afwezigheid, Integer> {
    List<Afwezigheid> findByWerknemerId(Integer werknemerId);
}