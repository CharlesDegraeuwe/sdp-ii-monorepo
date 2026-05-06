package hogent.sdp2.backend.WebSocket;

import hogent.sdp2.backend.WebSocket.dto.IncomingMsgDTO;
import hogent.sdp2.backend.WebSocket.dto.OutGoingMsgDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        System.out.println("=== WS CONNECTED === session: " + session.getId() + " userId: " + userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            IncomingMsgDTO incoming = objectMapper.readValue(message.getPayload(), IncomingMsgDTO.class);
            int userId = (Integer) session.getAttributes().get("userId");

            System.out.println("=== WS RECEIVED === " + incoming.content());

            chatService.streamReply(
                    incoming.content(),
                    userId,
                    chunk -> send(session, OutGoingMsgDTO.chunk(chunk)),
                    () -> send(session, OutGoingMsgDTO.done())
            );

        } catch (Exception e) {
            send(session, OutGoingMsgDTO.error(e.getMessage()));
        }
    }

    private void send(WebSocketSession session, OutGoingMsgDTO msg) {
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    String json = objectMapper.writeValueAsString(msg);
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to send WS message: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("=== WS CLOSED === " + status);
    }
}