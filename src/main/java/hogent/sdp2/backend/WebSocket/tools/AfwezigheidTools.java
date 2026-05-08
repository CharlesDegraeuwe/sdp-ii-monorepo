package hogent.sdp2.backend.WebSocket.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AfwezigheidTools {

    @Tool(description = "Deze tool helpt je met de afwezigheden van een gebruiker op te halen")
    public void AfwezigheidTools() {

    }
}
