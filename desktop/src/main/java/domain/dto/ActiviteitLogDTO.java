package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ActiviteitLogDTO(
        int id,
        WerknemerKortDTO werknemer,
        String type,
        String tabel,
        Integer recordId,
        String timestamp,
        String beschrijving
) {}