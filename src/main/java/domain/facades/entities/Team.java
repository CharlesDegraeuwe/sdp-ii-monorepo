package domain.facades.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Team_ID", nullable = false)
    private Integer id;

    @Column(name = "Naam", nullable = false, length = 45)
    private String naam;

}