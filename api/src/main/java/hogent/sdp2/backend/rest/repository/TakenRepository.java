package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Taken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TakenRepository extends JpaRepository<Taken, Integer> {
    List<Taken> findByWerknemer_Id(Integer werknemerId);
}
