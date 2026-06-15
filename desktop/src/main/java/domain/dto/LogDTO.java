package domain.dto;
import java.time.LocalDateTime;

public record LogDTO(int id,
                     WerknemerDTO werknemer,
                     String type,
                     String tabel,
                     LocalDateTime timestamp,
                     String test) {

}
