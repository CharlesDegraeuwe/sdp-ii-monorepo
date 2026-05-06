package hogent.sdp2.backend.WebSocket;

import org.springframework.stereotype.Service;
import java.util.function.Consumer;

@Service
public class ChatService {

    public void streamReply(String userMessage, int userId, Consumer<String> onChunk, Runnable onDone) {
        System.out.println("=== CHAT SERVICE === userId=" + userId + " msg=" + userMessage);

        new Thread(() -> {
            try {
                onChunk.accept("Hallo gebruiker " + userId + ", ");
                Thread.sleep(300);
                onChunk.accept("je vroeg: \"" + userMessage + "\". ");
                Thread.sleep(300);
                onChunk.accept("Dit is een dummy antwoord.");
                Thread.sleep(300);
                onDone.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}