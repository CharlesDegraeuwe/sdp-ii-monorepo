package hogent.sdp2.backend.WebSocket.tools;

import hogent.sdp2.backend.REST.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.REST.service.taken.TakenService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TakenTools {
    private final TakenService takenService;

    @Tool(description = "Haalt alle taken op van de ingelogde gebruiker. Gebruik deze tool wanneer de gebruiker vraagt naar zijn taken, opdrachten, to-dos of werk.")
    public String getMyTasks(ToolContext ctx) {
        System.out.println("=== TOOL CALL === getMyTasks");
        System.out.println("=== TOOL CTX === " + ctx.getContext());
        System.out.println("tool reached");
        Integer userId = (Integer) ctx.getContext().get("userId");
        List<TaakResponseDTO> taken = takenService.geefTakenVanWerknemer(userId);

        if (taken.isEmpty()) {
            System.out.println("Geen taken gevonden");
            return " heeft momenteel geen taken.";
        }

        System.out.println("Strinb builder gestart");
        StringBuilder sb = new StringBuilder("De gebruiker heeft de volgende taken:\n");
        for (TaakResponseDTO t : taken) {
            sb.append("- ")
                    .append(t.titel())
                    .append(" (status: ").append(t.afgewerkt())
                    .append(", deadline: ").append(t.deadline())
                    .append(")");
            if (t.beschrijving() != null && !t.beschrijving().isBlank()) {
                sb.append(" - ").append(t.beschrijving());
            }
            sb.append("\n");
        }
        String result = sb.toString();
        System.out.println("=== TOOL RESULT === " + result);
        return result;
    }
}
