package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogDTO(@JsonProperty(access = JsonProperty.Access.READ_ONLY) Integer id,
                     Integer werknemerId,
                     String type,
                     String tabel,
                     LocalDateTime timestamp,
                     String details) {

}