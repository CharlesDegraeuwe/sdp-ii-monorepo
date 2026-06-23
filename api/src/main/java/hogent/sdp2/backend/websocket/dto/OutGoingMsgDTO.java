package hogent.sdp2.backend.websocket.dto;

public record OutGoingMsgDTO(String type, String content, Boolean isAgentic) {

    public static OutGoingMsgDTO chunk(String content) {
        return new OutGoingMsgDTO("chunk", content, null);
    }

    public static OutGoingMsgDTO chunk(String content, boolean isAgentic) {
        return new OutGoingMsgDTO("chunk", content, isAgentic);
    }

    public static OutGoingMsgDTO done() {
        return new OutGoingMsgDTO("done", null, null);
    }

    public static OutGoingMsgDTO error(String message) {
        return new OutGoingMsgDTO("error", message, null);
    }
}