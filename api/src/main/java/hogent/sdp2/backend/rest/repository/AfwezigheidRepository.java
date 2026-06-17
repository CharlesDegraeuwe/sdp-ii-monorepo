package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Afwezigheid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AfwezigheidRepository extends JpaRepository<Afwezigheid, Integer> {
    List<Afwezigheid> findByWerknemerId(Integer werknemerId);

    @Query("SELECT a FROM Afwezigheid a JOIN FETCH a.werknemer")
    List<Afwezigheid> findAllWithWerknemer();

    @Query(
            "SELECT COUNT(a) > 0 FROM Afwezigheid a WHERE a.werknemer.id = :werknemerId AND :vandaag BETWEEN a.startDatum AND a.eindDatum")
    boolean isWerknemerAfwezigOpDatum(
            @Param("werknemerId") Integer werknemerId, @Param("vandaag") LocalDate vandaag);

    @Query(
            "SELECT COUNT(a) FROM Afwezigheid a WHERE CURRENT_DATE BETWEEN a.startDatum AND a.eindDatum")
    long telHuidigeAfwezigen();
}
