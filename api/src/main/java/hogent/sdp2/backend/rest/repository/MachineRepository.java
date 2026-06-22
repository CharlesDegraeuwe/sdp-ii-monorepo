package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Machine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Integer> {
    List<Machine> findBySiteId(Integer siteId);

    boolean existsByNaam(String naam);
}
