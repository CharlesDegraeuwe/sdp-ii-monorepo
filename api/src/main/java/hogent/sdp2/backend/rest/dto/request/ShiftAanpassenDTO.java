package hogent.sdp2.backend.rest.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftAanpassenDTO(
        LocalDate startDatum,
        LocalDate eindDatum,
        LocalTime startTijd,
        LocalTime eindTijd,
        LocalTime pauzeStart,
        LocalTime pauzeEind) {}
