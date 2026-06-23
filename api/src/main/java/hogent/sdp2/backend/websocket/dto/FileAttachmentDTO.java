package hogent.sdp2.backend.websocket.dto;

public record FileAttachmentDTO(String id, String name, String mimeType, long size) {
}
