package hogent.sdp2.backend.REST.repository;

import hogent.sdp2.backend.domain.Taken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TakenRepository extends JpaRepository<Taken, Integer> {
    List<Taken> findByWerknemer_Id(Integer werknemerId);
}