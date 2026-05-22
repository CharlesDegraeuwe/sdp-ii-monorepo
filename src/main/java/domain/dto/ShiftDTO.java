package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShiftDTO(
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
