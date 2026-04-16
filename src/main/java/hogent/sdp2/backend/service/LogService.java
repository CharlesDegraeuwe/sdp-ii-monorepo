package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Log;
import hogent.sdp2.backend.dto.request.*;
import hogent.sdp2.backend.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public String maakLog(LogDTO dto) {
        Log nieuweLog = new Log();
        nieuweLog.setWerknemer(dto.werknemer());
        nieuweLog.setType(dto.type());
        nieuweLog.setTabel(dto.tabel());
        nieuweLog.setTimestamp(LocalDateTime.from(dto.timestamp()));
        nieuweLog.setTest(dto.test());

        logRepository.save(nieuweLog);

        return "Log " + dto.type() + " " + dto.tabel() + " door "+dto.werknemer() + " is succesvol aangemaakt ";
    }

    public List<LogDTO> getAlleLogs() {
        return logRepository.findAll().stream()
                .map(l -> new LogDTO(
                        l.getId(),
                        l.getWerknemer(),
                        l.getType(),
                        l.getTabel(),
                        l.getTimestamp(),
                        l.getTest()
                ))
                .toList();

    }

    public LogDTO getByID(Integer id) {
        return logRepository.findById(id)
                .map(l -> new LogDTO(
                        l.getId(),
                        l.getWerknemer(),
                        l.getType(),
                        l.getTabel(),
                        l.getTimestamp(),
                        l.getTest()
                ))
                .orElseThrow(() -> new RuntimeException("Log niet gevonden"));
    }
}
