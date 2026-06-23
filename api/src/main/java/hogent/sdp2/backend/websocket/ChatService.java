package hogent.sdp2.backend.websocket;

import hogent.sdp2.backend.websocket.tools.AfwezigheidTools;
import hogent.sdp2.backend.websocket.tools.PlanningTools;
import hogent.sdp2.backend.websocket.tools.TakenTools;
import hogent.sdp2.backend.websocket.tools.UserTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final TakenTools takenTools;
    private final UserTools userTools;
    private final AfwezigheidTools afwezigheidTools;
    private final PlanningTools planningTools;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            TakenTools takenTools,
            UserTools userTools,
            AfwezigheidTools afwezigheidTools,
            PlanningTools planningTools
    ) {
        System.out.println("=== API KEY LOADED === " + (System.getenv("GEMINI_API_KEY") != null ? "yes" : "NO"));
        this.chatMemory = chatMemory;
        this.takenTools = takenTools;
        this.userTools = userTools;
        this.afwezigheidTools = afwezigheidTools;
        this.planningTools = planningTools;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        Je bent Benoit, een vriendelijke assistent binnen Delaware Suite,
                        een workforce management tool. Je beantwoordt vragen over
                        taken, planning, shiften en afwezigheden van de ingelogde gebruiker.
                        Antwoord altijd in het Nederlands.

                        BELANGRIJK: voor vragen over taken, planning of afwezigheden MOET je
                        altijd een tool gebruiken om actuele data op te halen. Verzin nooit
                        zelf taken of data.

                        Sommige tools zijn enkel beschikbaar voor supervisors en managers. Als een tool
                        "GEEN_TOEGANG" terugstuurt, leg dan vriendelijk uit dat de gebruiker geen rechten
                        heeft voor die actie. Probeer niet de data alsnog op een andere manier op te halen.

                        EXCEL PLANNING: wanneer een gebruiker een Excel-bestand uploadt met een planning:
                        1. Gebruik parseExcelFile met het fileId om de inhoud te lezen
                        2. Interpreteer de data (namen, datums, tijden)
                        3. Gebruik findEmployee om werknemerIds op te halen voor elke naam
                        4. Maak de shifts aan met createShift voor elke rij
                        5. Geef een samenvatting van wat je hebt aangemaakt
                        Vraag bevestiging aan de gebruiker voordat je shifts aanmaakt.

                        Voor vragen buiten deze scope, verwijs beleefd terug naar je expertisegebied.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public void streamReply(
            String userMessage,
            Integer userId,
            String userRole,
            List<FileStorageService.StoredFile> files,
            Consumer<String> onChunk,
            Runnable onDone
    ) {
        String conversationId = "user-" + userId;

        // Gemini's OpenAI-compatibiliteitslaag ondersteunt geen streaming met tool calls.
        // Daarom gebruiken we een synchrone call en sturen het resultaat als één chunk.
        Thread.startVirtualThread(() -> {
            try {
                // Only send visual files (images, PDFs) as Media to Gemini.
                // Excel/Word files are handled via tools (parseExcelFile).
                Set<String> visualTypes = Set.of(
                        "image/png", "image/jpeg", "image/gif", "application/pdf");

                List<Media> mediaList = files.stream()
                        .filter(f -> visualTypes.contains(f.mimeType()))
                        .map(f -> Media.builder()
                                .mimeType(f.mimeTypeObj())
                                .data(f.asResource())
                                .name(f.originalName())
                                .build())
                        .toList();

                // Append file metadata so the LLM knows which fileIds are available
                String enrichedMessage = userMessage;
                if (!files.isEmpty()) {
                    StringBuilder fileMeta = new StringBuilder("\n\n[Bijgevoegde bestanden:\n");
                    for (var f : files) {
                        fileMeta.append("- ").append(f.originalName())
                                .append(" (type: ").append(f.mimeType())
                                .append(", fileId: ").append(f.id()).append(")\n");
                    }
                    fileMeta.append("]");
                    enrichedMessage = userMessage + fileMeta;
                }

                var prompt = chatClient.prompt();
                if (mediaList.isEmpty()) {
                    prompt.user(enrichedMessage);
                } else {
                    String msg = enrichedMessage;
                    prompt.user(u -> u.text(msg).media(mediaList.toArray(new Media[0])));
                }

                String response = prompt
                        .tools(takenTools, userTools, afwezigheidTools, planningTools)
                        .toolContext(Map.of(
                                "userId", userId,
                                "userRole", userRole
                        ))
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .call()
                        .content();

                if (response != null) {
                    onChunk.accept(response);
                }
                onDone.run();
            } catch (Exception e) {
                System.err.println("Chat error: " + e.getMessage());
                e.printStackTrace();
                onChunk.accept("Er ging iets mis bij het verwerken van je bericht. Probeer het opnieuw.");
                onDone.run();
            }
        });
    }
}
