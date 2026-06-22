package hogent.sdp2.backend.rest.repository;

import hogent.sdp2.backend.domain.Team;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.TeamwerknemerId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamwerknemerRepository extends JpaRepository<Teamwerknemer, TeamwerknemerId> {
    List<Teamwerknemer> findByWerknemerId(Integer werknemerId);

    List<Teamwerknemer> findByTeamId(Integer teamId);

    long countByTeamId(Integer teamId);

    boolean existsByTeamIdAndWerknemerId(Integer teamId, Integer werknemerId);

    @Query(
            "SELECT tw FROM Teamwerknemer tw WHERE tw.team.id = :teamId AND tw.werknemer.rol = 'Manager'")
    List<Teamwerknemer> findGoedkeurderVanTeam(@Param("teamId") Integer teamId);

    List<Teamwerknemer> findByTeam(Team team);

    @Query(
            "SELECT tw.team.manager FROM Teamwerknemer tw WHERE tw.id.werknemerId = :werknemerId AND tw.team.manager IS NOT NULL")
    List<hogent.sdp2.backend.domain.Werknemer> findManagersByWerknemerId(
            @Param("werknemerId") Integer werknemerId);
}
