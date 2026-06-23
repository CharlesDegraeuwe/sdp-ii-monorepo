package hogent.sdp2.backend.websocket.dto;

import java.util.List;

public record IncomingMsgDTO(String content, List<String> fileIds) {
}
