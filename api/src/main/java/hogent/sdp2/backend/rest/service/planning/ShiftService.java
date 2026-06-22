package hogent.sdp2.backend.rest.service.planning;

import hogent.sdp2.backend.domain.Shift;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.ShiftAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.ShiftAanpassenDTO;
import hogent.sdp2.backend.rest.dto.response.ShiftResponseDTO;
import hogent.sdp2.backend.rest.repository.ShiftRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;

    public List<ShiftResponseDTO> geefShiftenVanTeamOpDatum(Integer teamId, LocalDate datum) {
        List<Teamwerknemer> teamleden = teamwerknemerRepository.findByTeamId(teamId);
        return teamleden.stream()
                .flatMap(
                        tw ->
                                shiftRepository
                                        .findByWerknemer_Id(tw.getWerknemer().getId())
                                        .stream())
                .filter(s -> !datum.isBefore(s.getStartDatum()) && !datum.isAfter(s.getEindDatum()))
                .map(this::mapToDTO)
                .toList();
    }

    public List<ShiftResponseDTO> geefShiftenVanWerknemerOpDatum(
            Integer werknemerId, LocalDate datum) {
        return shiftRepository.findByWerknemer_Id(werknemerId).stream()
                .filter(s -> !datum.isBefore(s.getStartDatum()) && !datum.isAfter(s.getEindDatum()))
                .map(this::mapToDTO)
                .toList();
    }

    public ShiftResponseDTO maakShift(ShiftAanmakenDTO dto) {
        Werknemer werknemer =
            werknemerRepository
                .findById(dto.werknemerId())
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Werknemer niet gevonden: " + dto.werknemerId()));
        Shift shift = new Shift();
        shift.setWerknemer(werknemer);

        // =======================================================
        // DE FIX: Koppel automatisch het team aan deze shift!
        // =======================================================
        List<Teamwerknemer> teamwerknemers = teamwerknemerRepository.findByWerknemerId(werknemer.getId());
        if (!teamwerknemers.isEmpty()) {
            shift.setTeam(teamwerknemers.get(0).getTeam());
        }
        // =======================================================

        shift.setStartDatum(LocalDate.parse(dto.startDatum()));
        shift.setEindDatum(LocalDate.parse(dto.eindDatum()));
        shift.setStartTijd(LocalTime.parse(dto.startTijd()));
        shift.setEindTijd(LocalTime.parse(dto.eindTijd()));

        if (dto.pauzeStart() != null && !dto.pauzeStart().isBlank()) {
            shift.setPauzeStart(LocalTime.parse(dto.pauzeStart()));
        }
        if (dto.pauzeEind() != null && !dto.pauzeEind().isBlank()) {
            shift.setPauzeEind(LocalTime.parse(dto.pauzeEind()));
        }

        return mapToDTO(shiftRepository.save(shift));
    }

    public ShiftResponseDTO pasAan(Integer shiftId, ShiftAanpassenDTO dto) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Shift niet gevonden: " + shiftId));

        shift.setStartDatum(LocalDate.parse(dto.startDatum()));
        shift.setEindDatum(LocalDate.parse(dto.eindDatum()));
        shift.setStartTijd(LocalTime.parse(dto.startTijd()));
        shift.setEindTijd(LocalTime.parse(dto.eindTijd()));

        if (dto.pauzeStart() != null && !dto.pauzeStart().isBlank()) {
            shift.setPauzeStart(LocalTime.parse(dto.pauzeStart()));
        } else {
            shift.setPauzeStart(null); // Pauze verwijderd
        }

        if (dto.pauzeEind() != null && !dto.pauzeEind().isBlank()) {
            shift.setPauzeEind(LocalTime.parse(dto.pauzeEind()));
        } else {
            shift.setPauzeEind(null); // Pauze verwijderd
        }

        return mapToDTO(shiftRepository.save(shift));
    }

    private ShiftResponseDTO mapToDTO(Shift shift) {
        Werknemer w = shift.getWerknemer();
        return new ShiftResponseDTO(
                shift.getId(),
                w.getId(),
                w.getVoornaam() + " " + w.getNaam(),
                shift.getStartDatum(),
                shift.getEindDatum(),
                shift.getStartTijd(),
                shift.getEindTijd(),
                shift.getPauzeStart(),
                shift.getPauzeEind());
    }
}
