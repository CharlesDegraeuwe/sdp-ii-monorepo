package domain.services;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SseListenerService {

    private static final SseListenerService INSTANCE = new SseListenerService();

    private final String BASE_URL = Dotenv.load().get("BASE_URL");
    private final HttpClient client = HttpClient.newHttpClient();
    private volatile boolean actief = false;
    private Thread sseThread;
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    private SseListenerService() {}

    public static SseListenerService getInstance() {
        return INSTANCE;
    }

    public void voegListenerToe(Runnable listener) {
        listeners.add(listener);
    }

    public void verwijderListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void start(int werknemerId, String token) {
        if (actief) return;
        actief = true;
        sseThread = new Thread(() -> verbind(werknemerId, token));
        sseThread.setDaemon(true);
        sseThread.setName("sse-listener");
        sseThread.start();
    }

    private void verbind(int werknemerId, String token) {
        long vertraging = 2_000;
        while (actief) {
            try {
                String url = BASE_URL + "/sse/subscribe/" + werknemerId
                        + (token != null && !token.isBlank() ? "?token=" + token : "");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "text/event-stream")
                        .GET()
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofLines())
                        .body()
                        .filter(line -> line.startsWith("data:"))
                        .forEach(line -> {
                            if (actief) {
                                Platform.runLater(this::notificeerListeners);
                            }
                        });

                vertraging = 2_000;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[SSE] Verbinding verbroken: " + e.getMessage());
            }

            if (actief) {
                try {
                    Thread.sleep(vertraging);
                    vertraging = Math.min(vertraging * 2, 30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void notificeerListeners() {
        listeners.forEach(Runnable::run);
    }

    public void stop() {
        actief = false;
        if (sseThread != null) {
            sseThread.interrupt();
            sseThread = null;
        }
        listeners.clear();
    }
}
