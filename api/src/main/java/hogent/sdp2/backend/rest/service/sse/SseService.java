package hogent.sdp2.backend.rest.service.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

    private final Map<Integer, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public SseEmitter subscribe(Integer werknemerId) {
        SseEmitter emitter = new SseEmitter(180_000L);

        emitters.computeIfAbsent(werknemerId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable cleanup =
                () -> {
                    List<SseEmitter> list = emitters.get(werknemerId);
                    if (list != null) list.remove(emitter);
                };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        return emitter;
    }

    public void pushEvent(Integer werknemerId, String eventType, Object data) {
        List<SseEmitter> werknemerEmitters = emitters.get(werknemerId);
        if (werknemerEmitters == null || werknemerEmitters.isEmpty()) return;

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : werknemerEmitters) {
            try {
                String json = objectMapper.writeValueAsString(data);
                emitter.send(SseEmitter.event().name(eventType).data(json));
            } catch (IOException e) {
                deadEmitters.add(emitter);
                emitter.completeWithError(e);
            }
        }

        werknemerEmitters.removeAll(deadEmitters);
    }
}
