package hogent.sdp2.backend.websocket.tools;

import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.service.werknemer.WerknemerService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTools {

    private final WerknemerService werknemerService;

    @Tool(description = "Haalt info op over de user")
    public String getTheUser(ToolContext ctx) {
        System.out.println("=== TOOL CALL === userTools");
        System.out.println("=== TOOL CTX === " + ctx.getContext());

        Integer userId = (Integer) ctx.getContext().get("userId");
        WerknemerResponseDTO werknemer = werknemerService.getByID(userId);
        Date date = new Date();
        if (werknemer == null) {
            return "er ging iets grondig mis, user niet ingelogd.";
        }

        StringBuilder sb =
                new StringBuilder(ctx.getContext().get("userName") + " zijn persoonlijke info:\n");
        sb.append("- ")
                .append("voornaam & achternaam: ")
                .append(werknemer.voornaam() + " " + werknemer.naam())
                .append("\n")
                .append("De rol van de gebruiker: ")
                .append(werknemer.rol())
                .append("\n")
                .append("De geboortedatum van de gebruiker: ")
                .append(werknemer.geboortedatum())
                .append("De huidige datum is: ")
                .append(date)
                .append(" (wens de gebruiker eventueel een gelukkige verjaardag");

        return sb.toString();
    }
}
