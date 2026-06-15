package domain.dto;
import java.time.LocalDateTime;

public record LogDTO(Integer id,
                     Integer werknemerId,
                     String type,
                     String tabel,
                     LocalDateTime timestamp,
                     String test) {

}