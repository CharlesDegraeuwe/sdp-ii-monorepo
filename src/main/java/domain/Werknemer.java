package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "werknemers")
public class Werknemer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Werknemer_ID", nullable = false)
    private Integer id;

    @Column(name = "Naam", nullable = false, length = 45)
    private String naam;

    @Column(name = "Voornaam", nullable = false, length = 45)
    private String voornaam;

    @Column(name = "Email", nullable = false, length = 90)
    private String email;

    @Column(name = "Wachtwoord", nullable = false, length = 45)
    private String wachtwoord;

    @Column(name = "Telefoonnummer", nullable = false, length = 20)
    private String telefoonnummer;

    @Column(name = "Geboortedatum", nullable = false)
    private LocalDate geboortedatum;

    @Column(name = "Rol", nullable = false, length = 45)
    private String rol;

    @Column(name = "Status", nullable = false, length = 45)
    private String status;

}