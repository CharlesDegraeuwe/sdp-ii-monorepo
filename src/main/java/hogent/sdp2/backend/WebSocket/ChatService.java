package hogent.sdp2.backend.WebSocket;

import hogent.sdp2.backend.WebSocket.tools.TakenTools;
import hogent.sdp2.backend.WebSocket.tools.UserTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final TakenTools takenTools;

    private final UserTools userTools;


    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
             TakenTools takenTools,
            UserTools userTools
    ) {
        this.chatMemory = chatMemory;
        this.takenTools = takenTools;
        this.userTools = userTools;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                         Je bent Benoit, een vriendelijke assistent binnen Delaware Suite,
                         een workforce management tool. Je beantwoordt enkel vragen over
                         taken, planning en afwezigheden van de ingelogde gebruiker.
                         Antwoord altijd in het Nederlands.
                         
                         BELANGRIJK: voor vragen over taken, planning of afwezigheden MOET je
                         altijd een tool gebruiken om actuele data op te halen. Verzin nooit
                         zelf taken of data.
                         
                         Voor vragen buiten deze scope, verwijs beleefd terug naar je expertisegebied.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public void streamReply(String userMessage, Integer userId, Consumer<String> onChunk, Runnable onDone) {
        String conversationId = "user-" + userId;

        try {
            String response = chatClient.prompt()
                    .user(userMessage)
                    .tools(takenTools, userTools)
                    .toolContext(Map.of("userId", userId))
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .call()
                    .content();

            onChunk.accept(response);
        } catch (Exception e) {
            System.err.println("Chat error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            onDone.run();
        }
    }
}
