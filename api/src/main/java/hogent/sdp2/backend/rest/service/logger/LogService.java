package hogent.sdp2.backend.rest.service.logger;

import hogent.sdp2.backend.domain.Log;
import hogent.sdp2.backend.rest.dto.request.LogDTO;
import hogent.sdp2.backend.rest.repository.LogRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public String maakLog(LogDTO dto) {
        Log nieuweLog = new Log();
        nieuweLog.setWerknemer(dto.werknemer());
        nieuweLog.setType(dto.type());
        nieuweLog.setTabel(dto.tabel());
        nieuweLog.setRecordId(dto.recordId());
        nieuweLog.setTimestamp(LocalDateTime.from(dto.timestamp()));
        nieuweLog.setBeschrijving(dto.beschrijving());

        logRepository.save(nieuweLog);

        return "Log "
                + dto.type()
                + " "
                + dto.tabel()
                + " door "
                + dto.werknemer()
                + " is succesvol aangemaakt ";
    }

    public List<LogDTO> getAlleLogs() {
        return logRepository.findAll().stream()
                .map(
                        l ->
                                new LogDTO(
                                        l.getId(),
                                        l.getWerknemer(),
                                        l.getType(),
                                        l.getTabel(),
                                        l.getRecordId(),
                                        l.getTimestamp(),
                                        l.getBeschrijving()))
                .toList();
    }

    public LogDTO getByID(Integer id) {
        return logRepository
                .findById(id)
                .map(
                        l ->
                                new LogDTO(
                                        l.getId(),
                                        l.getWerknemer(),
                                        l.getType(),
                                        l.getTabel(),
                                        l.getRecordId(),
                                        l.getTimestamp(),
                                        l.getBeschrijving()))
                .orElseThrow(() -> new RuntimeException("Log niet gevonden"));
    }

    public List<LogDTO> getRecenteLogs() {
        return logRepository.findTop10ByOrderByTimestampDesc().stream()
                .map(
                        l ->
                                new LogDTO(
                                        l.getId(),
                                        l.getWerknemer(),
                                        l.getType(),
                                        l.getTabel(),
                                        l.getRecordId(),
                                        l.getTimestamp(),
                                        l.getBeschrijving()))
                .toList();
    }
}
