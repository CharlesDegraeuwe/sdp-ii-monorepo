package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    boolean existsByNaam(String naam);
}

