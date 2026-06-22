package domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List; // <-- Vergeet deze import niet!

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaakDTO(
    int id,
    @JsonAlias("titel") String naam,
    @JsonAlias("beschrijving") String specificaties,
    String deadline,
    String duur,
    String locatie,
    Object belangrijk,
    Object afgewerkt,
    String afgewerktOp,
    List<Integer> werknemerIds,
    String startuur,
    String einduur
) {
    public boolean isAfgewerkt() {
        if (afgewerkt == null) return false;
        if (afgewerkt instanceof Boolean b) return b;
        return "ja".equalsIgnoreCase(afgewerkt.toString());
    }
}
