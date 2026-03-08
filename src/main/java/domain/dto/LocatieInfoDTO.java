package domain.dto;

public class LocatieInfoDTO {
    private String naam;
    private String locatie;
    private String status;
    private String quickStats;

    public LocatieInfoDTO(String naam, String locatie, String status, String quickStats) {
        this.naam = naam;
        this.locatie = locatie;
        this.status = status;
        this.quickStats = quickStats;
    }

    // getters
    public String getNaam() { return naam; }
    public String getLocatie() { return locatie; }
    public String getStatus() { return status; }
    public String getQuickStats() { return quickStats; }
}