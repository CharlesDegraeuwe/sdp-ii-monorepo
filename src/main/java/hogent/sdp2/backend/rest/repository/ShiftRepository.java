package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findByWerknemer_Id(Integer werknemerId);
}
