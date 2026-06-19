package hogent.sdp2.backend.websocket;

import hogent.sdp2.backend.websocket.tools.AfwezigheidTools;
import hogent.sdp2.backend.websocket.tools.TakenTools;
import hogent.sdp2.backend.websocket.tools.UserTools;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final TakenTools takenTools;
    private final UserTools userTools;

    private final AfwezigheidTools afwezigheidTools;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            TakenTools takenTools,
            UserTools userTools,
            AfwezigheidTools afwezigheidTools) {
        System.out.println(
                "=== API KEY LOADED === "
                        + (System.getenv("GEMINI_API_KEY") != null ? "yes" : "NO"));
        this.chatMemory = chatMemory;
        this.takenTools = takenTools;
        this.userTools = userTools;
        this.afwezigheidTools = afwezigheidTools;
        this.chatClient =
                chatClientBuilder
                        .defaultSystem(
                                """
                        Je bent Benoit, een vriendelijke assistent binnen Delaware Suite,
                        een workforce management tool. Je beantwoordt enkel vragen over
                        taken, planning en afwezigheden van de ingelogde gebruiker.
                        Antwoord altijd in het Nederlands.

                        BELANGRIJK: voor vragen over taken, planning of afwezigheden MOET je
                        altijd een tool gebruiken om actuele data op te halen. Verzin nooit
                        zelf taken of data.

                        Sommige tools zijn enkel beschikbaar voor supervisors en managers. Als een tool
                        "GEEN_TOEGANG" terugstuurt, leg dan vriendelijk uit dat de gebruiker geen rechten
                        heeft voor die actie. Probeer niet de data alsnog op een andere manier op te halen.

                        Voor vragen buiten deze scope, verwijs beleefd terug naar je expertisegebied.
                        """)
                        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .build();
    }

    public void streamReply(
            String userMessage,
            Integer userId,
            String userRole,
            Consumer<String> onChunk,
            Runnable onDone) {
        String conversationId = "user-" + userId;

        // Gemini's OpenAI-compatibiliteitslaag ondersteunt geen streaming met tool calls.
        // Daarom gebruiken we een synchrone call en sturen het resultaat als één chunk.
        Thread.startVirtualThread(
                () -> {
                    try {
                        String response =
                                chatClient
                                        .prompt()
                                        .user(userMessage)
                                        .tools(takenTools, userTools, afwezigheidTools)
                                        .toolContext(
                                                Map.of(
                                                        "userId", userId,
                                                        "userRole", userRole))
                                        .advisors(
                                                a ->
                                                        a.param(
                                                                ChatMemory.CONVERSATION_ID,
                                                                conversationId))
                                        .call()
                                        .content();

                        if (response != null) {
                            onChunk.accept(response);
                        }
                        onDone.run();
                    } catch (Exception e) {
                        System.err.println("Chat error: " + e.getMessage());
                        e.printStackTrace();
                        onChunk.accept(
                                "Er ging iets mis bij het verwerken van je bericht. Probeer het opnieuw.");
                        onDone.run();
                    }
                });
    }
}
