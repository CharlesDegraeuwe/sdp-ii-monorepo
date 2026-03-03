package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Integer> {
    boolean existsByNaam(String naam);
}