package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Integer> {
    List<Machine> findBySiteId(Integer siteId);
    boolean existsByNaam(String naam);
}