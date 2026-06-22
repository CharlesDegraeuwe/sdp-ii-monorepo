package hogent.sdp2.backend.websocket.tools;

import hogent.sdp2.backend.rest.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.rest.service.afwezigheid.AfwezigheidService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AfwezigheidTools {

    private static final Set<String> SUPERVISOR_ROLES = Set.of("Supervisor", "Manager", "Admin");

    private final AfwezigheidService afwezigheidService;

    @Tool(description = "Haal de afwezigheden op van de ingelogde gebruiker zelf")
    public List<AfwezigheidsOverzichtDTO> getMyAbsence(ToolContext ctx) {
        Integer userId = (Integer) ctx.getContext().get("userId");
        System.out.println("=== TOOL CALL === getMyAbsence voor user " + userId);
        return afwezigheidService.geefAfwezighedenVanWerknemer(userId);
    }

    @Tool(
            description =
                    "Haal de afwezigheden op van de teamgenoten van de ingelogde gebruiker. Enkel beschikbaar voor supervisors en managers.")
    public Object getTeamAbsence(ToolContext ctx) {
        Integer userId = (Integer) ctx.getContext().get("userId");
        String role = (String) ctx.getContext().get("userRole");
        System.out.println(
                "=== TOOL CALL === getTeamAbsence voor user " + userId + " met rol " + role);

        if (!SUPERVISOR_ROLES.contains(role)) {
            return "GEEN_TOEGANG: deze gebruiker heeft rol "
                    + role
                    + " en kan geen afwezigheden van teamgenoten opvragen. Enkel supervisors en managers mogen dit.";
        }

        return afwezigheidService.geefAfwezighedenVanTeam();
    }

    @Tool(
            description =
                    "Haal de afwezigheden op van een specifieke werknemer op basis van zijn id. Enkel beschikbaar voor supervisors en managers.")
    public Object getSpecificUserAbsence(Integer werknemerId, ToolContext ctx) {
        String role = (String) ctx.getContext().get("userRole");
        System.out.println(
                "=== TOOL CALL === getSpecificUserAbsence voor werknemer "
                        + werknemerId
                        + " met rol "
                        + role);

        if (!SUPERVISOR_ROLES.contains(role)) {
            return "GEEN_TOEGANG: deze gebruiker heeft rol "
                    + role
                    + " en kan geen afwezigheden van andere werknemers opvragen. Enkel supervisors en managers mogen dit.";
        }

        return afwezigheidService.geefAfwezighedenVanWerknemer(werknemerId);
    }
}
