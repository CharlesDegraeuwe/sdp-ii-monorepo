package hogent.sdp2.backend.websocket.tools;

import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.ShiftAanmakenDTO;
import hogent.sdp2.backend.rest.dto.response.ShiftResponseDTO;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.planning.ShiftService;
import hogent.sdp2.backend.websocket.ExcelParserService;
import hogent.sdp2.backend.websocket.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PlanningTools {

    private static final Set<String> ALLOWED_ROLES = Set.of("Supervisor", "Manager", "Admin");

    private final ShiftService shiftService;
    private final WerknemerRepository werknemerRepository;
    private final ExcelParserService excelParserService;
    private final FileStorageService fileStorageService;

    @Tool(description = """
            Maak een shift aan voor een werknemer. Parameters:
            - werknemerId: ID van de werknemer
            - startDatum: startdatum (yyyy-MM-dd)
            - eindDatum: einddatum (yyyy-MM-dd)
            - startTijd: starttijd (HH:mm)
            - eindTijd: eindtijd (HH:mm)
            - pauzeStart: pauzestart (HH:mm, optioneel)
            - pauzeEind: pauzeeind (HH:mm, optioneel)
            Enkel beschikbaar voor supervisors, managers en admins.
            """)
    public Object createShift(
            Integer werknemerId,
            String startDatum,
            String eindDatum,
            String startTijd,
            String eindTijd,
            String pauzeStart,
            String pauzeEind,
            ToolContext ctx
    ) {
        String role = (String) ctx.getContext().get("userRole");
        if (!ALLOWED_ROLES.contains(role)) {
            return "GEEN_TOEGANG: je hebt rol " + role + " en mag geen shiften aanmaken.";
        }

        try {
            ShiftAanmakenDTO dto = new ShiftAanmakenDTO(
                    werknemerId,
                    LocalDate.parse(startDatum),
                    LocalDate.parse(eindDatum),
                    parseTime(startTijd),
                    parseTime(eindTijd),
                    pauzeStart != null && !pauzeStart.isBlank() ? parseTime(pauzeStart) : null,
                    pauzeEind != null && !pauzeEind.isBlank() ? parseTime(pauzeEind) : null
            );
            ShiftResponseDTO result = shiftService.maakShift(dto);
            return "Shift aangemaakt: " + result.werknemerNaam() +
                    " van " + result.startDatum() + " " + result.startTijd() +
                    " tot " + result.eindDatum() + " " + result.eindTijd() +
                    " (ID: " + result.id() + ")";
        } catch (DateTimeParseException e) {
            return "Fout bij het parsen van datum/tijd: " + e.getMessage();
        } catch (Exception e) {
            return "Fout bij aanmaken shift: " + e.getMessage();
        }
    }

    @Tool(description = """
            Zoek een werknemer op naam (voornaam en achternaam). Geeft het ID en de naam terug.
            Gebruik dit om een werknemerId op te halen voordat je een shift aanmaakt.
            """)
    public Object findEmployee(String voornaam, String achternaam, ToolContext ctx) {
        String role = (String) ctx.getContext().get("userRole");
        if (!ALLOWED_ROLES.contains(role)) {
            return "GEEN_TOEGANG: je hebt rol " + role + " en mag geen werknemers opzoeken.";
        }

        Optional<Werknemer> werknemer = werknemerRepository
                .findByVoornaamIgnoreCaseAndNaamIgnoreCase(voornaam.trim(), achternaam.trim());
        if (werknemer.isPresent()) {
            Werknemer w = werknemer.get();
            return Map.of("id", w.getId(), "naam", w.getVoornaam() + " " + w.getNaam());
        }
        return "Werknemer niet gevonden: " + voornaam + " " + achternaam;
    }

    @Tool(description = """
            Parse een geüpload Excel-bestand en geeft de inhoud als gestructureerde tekst terug.
            Gebruik dit wanneer de gebruiker een Excel-bestand uploadt met een planning of shiftschema.
            Na het parsen kun je de data interpreteren en shifts aanmaken met createShift.
            Parameter: fileId - het ID van het geüploade bestand.
            """)
    public Object parseExcelFile(String fileId, ToolContext ctx) {
        String role = (String) ctx.getContext().get("userRole");
        if (!ALLOWED_ROLES.contains(role)) {
            return "GEEN_TOEGANG: je hebt rol " + role + " en mag geen planningsbestanden verwerken.";
        }

        Integer userId = (Integer) ctx.getContext().get("userId");
        List<FileStorageService.StoredFile> files = fileStorageService.resolve(userId, List.of(fileId));

        if (files.isEmpty()) {
            return "Bestand niet gevonden met ID: " + fileId;
        }

        FileStorageService.StoredFile file = files.getFirst();
        if (!file.mimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return "Dit is geen Excel-bestand (.xlsx). Upload een .xlsx bestand.";
        }

        try {
            List<ExcelParserService.ParsedSheet> sheets = excelParserService.parse(file.data());
            if (sheets.isEmpty()) {
                return "Het Excel-bestand is leeg of bevat geen data.";
            }
            return excelParserService.toText(sheets);
        } catch (Exception e) {
            return "Fout bij het parsen van het Excel-bestand: " + e.getMessage();
        }
    }

    @Tool(description = """
            Haal de shifts op van een werknemer voor een bepaalde periode.
            Parameters: werknemerId, vanDatum (yyyy-MM-dd), totDatum (yyyy-MM-dd).
            Enkel beschikbaar voor supervisors, managers en admins.
            """)
    public Object getShifts(Integer werknemerId, String vanDatum, String totDatum, ToolContext ctx) {
        String role = (String) ctx.getContext().get("userRole");
        if (!ALLOWED_ROLES.contains(role)) {
            return "GEEN_TOEGANG: je hebt rol " + role + " en mag geen shifts van anderen opvragen.";
        }

        try {
            List<ShiftResponseDTO> shifts = shiftService.geefShiftenVanWerknemerInBereik(
                    werknemerId, LocalDate.parse(vanDatum), LocalDate.parse(totDatum));
            if (shifts.isEmpty()) {
                return "Geen shifts gevonden voor werknemer " + werknemerId + " in deze periode.";
            }
            StringBuilder sb = new StringBuilder("Shifts gevonden:\n");
            for (ShiftResponseDTO s : shifts) {
                sb.append("- ").append(s.werknemerNaam())
                        .append(": ").append(s.startDatum()).append(" ").append(s.startTijd())
                        .append(" tot ").append(s.eindDatum()).append(" ").append(s.eindTijd())
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Fout bij ophalen shifts: " + e.getMessage();
        }
    }

    private LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) return null;
        return LocalTime.parse(time.trim(), DateTimeFormatter.ofPattern("[HH:mm][H:mm][HH:mm:ss]"));
    }
}
