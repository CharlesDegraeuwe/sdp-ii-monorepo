package hogent.sdp2.backend.WebSocket;

import hogent.sdp2.backend.REST.repository.WerknemerRepository;
import hogent.sdp2.backend.auth.JwtService;
import hogent.sdp2.backend.domain.Werknemer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final WerknemerRepository werknemerRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        System.out.println("=== WS HANDSHAKE === " + request.getURI());

        String query = request.getURI().getQuery();
        if (query == null) {
            System.out.println("=== WS HANDSHAKE FAIL === no query");
            return false;
        }

        String token = Arrays.stream(query.split("&"))
                .filter(p -> p.startsWith("token="))
                .map(p -> p.substring("token=".length()))
                .findFirst()
                .orElse(null);

        if (token == null) {
            System.out.println("=== WS HANDSHAKE FAIL === no token in query");
            return false;
        }

        try {
            if (!jwtService.isTokenValid(token)) {
                System.out.println("=== WS HANDSHAKE FAIL === invalid token");
                return false;
            }

            String username = jwtService.extractUsername(token);
            Werknemer user = werknemerRepository.findByEmail(username).orElse(null);

            if (user == null) {
                System.out.println("=== WS HANDSHAKE FAIL === user not found");
                return false;
            }

            attributes.put("userId", user.getId());
            attributes.put("username", username);
            System.out.println("=== WS HANDSHAKE OK === userId=" + user.getId());
            return true;

        } catch (Exception e) {
            System.out.println("=== WS HANDSHAKE FAIL === " + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // niks te doen
    }
}