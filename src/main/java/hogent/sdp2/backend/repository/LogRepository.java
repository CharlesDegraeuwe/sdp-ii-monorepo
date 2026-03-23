package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {
    List<Log> findByWerknemerIdOrderByTimestampDesc(Integer werknemerId);
    List<Log> findAllByOrderByTimestampDesc();
}