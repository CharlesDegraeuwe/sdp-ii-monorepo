package hogent.sdp2.backend.rest.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftResponseDTO(
        Integer id,
        Integer werknemerId,
        String werknemerNaam,
        LocalDate startDatum,
        LocalDate eindDatum,
        LocalTime startTijd,
        LocalTime eindTijd,
        LocalTime pauzeStart,
        LocalTime pauzeEind
) {}
