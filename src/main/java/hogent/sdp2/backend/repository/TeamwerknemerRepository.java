package hogent.sdp2.backend.repository;

import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.TeamwerknemerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamwerknemerRepository extends JpaRepository<Teamwerknemer, TeamwerknemerId> {
    List<Teamwerknemer> findByWerknemerId(Integer werknemerId);
    List<Teamwerknemer> findByTeamId(Integer teamId);

    @Query("SELECT tw FROM Teamwerknemer tw WHERE tw.team.id = :teamId AND tw.werknemer.rol = 'Manager'")
    Optional<Teamwerknemer> findGoedkeurderVanTeam(@Param("teamId") Integer teamId);
}