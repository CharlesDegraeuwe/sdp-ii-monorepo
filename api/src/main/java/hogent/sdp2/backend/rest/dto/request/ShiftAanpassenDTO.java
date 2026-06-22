package hogent.sdp2.backend.rest.dto.request;

public record ShiftAanpassenDTO(
    String startDatum,
    String eindDatum,
    String startTijd,
    String eindTijd,
    String pauzeStart,
    String pauzeEind) {}
