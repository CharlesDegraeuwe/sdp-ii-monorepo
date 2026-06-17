package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Log;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {
    List<Log> findTop10ByOrderByTimestampDesc();
}
