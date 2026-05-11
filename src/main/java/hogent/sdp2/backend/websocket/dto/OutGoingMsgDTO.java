package hogent.sdp2.backend.websocket.dto;

public record OutGoingMsgDTO(String type, String content) {

    public static OutGoingMsgDTO chunk(String content) {
        return new OutGoingMsgDTO("chunk", content);
    }

    public static OutGoingMsgDTO done() {
        return new OutGoingMsgDTO("done", null);
    }

    public static OutGoingMsgDTO error(String message) {
        return new OutGoingMsgDTO("error", message);
    }
}