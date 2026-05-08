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
public class AfwezigheidTools {

    private final TakenService takenService;
    @Tool(description = "Deze tool helpt je met de afwezigheden van een gebruiker op te halen")
    public void getAbsence(ToolContext ctx) {
        System.out.println("=== TOOL CALL === getAbsence");
        System.out.println("=== TOOL CTX === " + ctx.getContext());
        System.out.println("tool reached");
    }
}
