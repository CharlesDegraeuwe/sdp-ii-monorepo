package hogent.sdp2.backend.auth;

import hogent.sdp2.backend.REST.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.REST.repository.WerknemerRepository;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Werknemer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Thread-safe session service - vervangt de Sessie singleton.
 * Gebruik dit als equivalent van useSession() in Next.js.
 * Injecteer via @Autowired of constructor injection in elke controller/service.
 */
@Service
@RequiredArgsConstructor
public class SessieService {

    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;

    /**
     * Haalt de ingelogde werknemer op uit de SecurityContext.
     * Elke request heeft zijn eigen SecurityContext (thread-safe).
     */
    public Werknemer getIngelogdeWerknemer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Niet ingelogd");
        }
        String email = auth.getName();
        return werknemerRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Gebruiker niet gevonden"));
    }

    public Integer getIngelogdeWerknemerId() {
        return getIngelogdeWerknemer().getId();
    }

    public String getRol() {
        return getIngelogdeWerknemer().getRol();
    }

    public boolean isAdmin() {
        return "Admin".equals(getIngelogdeWerknemer().getRol());
    }

    public boolean isAdminOfManager() {
        String rol = getIngelogdeWerknemer().getRol();
        return "Admin".equals(rol) || "Manager".equals(rol);
    }

    public boolean isSupervisor() {
        return "Supervisor".equals(getIngelogdeWerknemer().getRol());
    }

    /**
     * Controleert of de ingelogde gebruiker in hetzelfde team zit als de opgegeven werknemer.
     */
    public boolean isInZelfdeTeam(Integer werknemerId) {
        Integer ingelogdId = getIngelogdeWerknemerId();
        Set<Integer> mijnTeamIds = teamwerknemerRepository.findByWerknemerId(ingelogdId).stream()
                .map(tw -> tw.getTeam().getId())
                .collect(Collectors.toSet());
        return teamwerknemerRepository.findByWerknemerId(werknemerId).stream()
                .anyMatch(tw -> mijnTeamIds.contains(tw.getTeam().getId()));
    }

    /**
     * Controleert of de ingelogde gebruiker lid is van het opgegeven team.
     */
    public boolean isLidVanTeam(Integer teamId) {
        return teamwerknemerRepository.existsByTeamIdAndWerknemerId(teamId, getIngelogdeWerknemerId());
    }

    /**
     * Geeft alle team-IDs waar de ingelogde gebruiker lid van is.
     */
    public Set<Integer> getMijnTeamIds() {
        return teamwerknemerRepository.findByWerknemerId(getIngelogdeWerknemerId()).stream()
                .map(tw -> tw.getTeam().getId())
                .collect(Collectors.toSet());
    }

    /**
     * Geeft alle werknemer-IDs die in dezelfde teams zitten als de ingelogde gebruiker.
     */
    public Set<Integer> getTeamgenootIds() {
        Set<Integer> mijnTeamIds = getMijnTeamIds();
        return mijnTeamIds.stream()
                .flatMap(teamId -> teamwerknemerRepository.findByTeamId(teamId).stream())
                .map(tw -> tw.getWerknemer().getId())
                .collect(Collectors.toSet());
    }

    // --- Autorisatie-checks die AccessDeniedException gooien ---

    /**
     * Controleert of de ingelogde gebruiker toegang heeft tot de data van een specifieke werknemer.
     * Admin/Manager: altijd toegang
     * Supervisor: alleen als ze in hetzelfde team zitten
     * Werknemer: alleen eigen data
     */
    public void assertToegangTotWerknemer(Integer werknemerId) {
        if (isAdminOfManager()) return;
        Werknemer ingelogd = getIngelogdeWerknemer();
        if (ingelogd.getId().equals(werknemerId)) return;
        if (isSupervisor() && isInZelfdeTeam(werknemerId)) return;
        throw new AccessDeniedException("Geen toegang tot deze werknemer");
    }

    /**
     * Controleert of de ingelogde gebruiker toegang heeft tot een specifiek team.
     * Admin/Manager: altijd toegang
     * Supervisor: alleen als ze lid zijn van dat team
     */
    public void assertToegangTotTeam(Integer teamId) {
        if (isAdminOfManager()) return;
        if (isSupervisor() && isLidVanTeam(teamId)) return;
        throw new AccessDeniedException("Geen toegang tot dit team");
    }

    /**
     * Controleert of de ingelogde gebruiker admin of manager is.
     */
    public void assertAdminOfManager() {
        if (!isAdminOfManager()) {
            throw new AccessDeniedException("Alleen admins en managers hebben toegang");
        }
    }

    /**
     * Controleert of de ingelogde gebruiker admin is.
     */
    public void assertAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Alleen admins hebben toegang");
        }
    }
}
