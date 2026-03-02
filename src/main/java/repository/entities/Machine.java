package repository.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "machine")
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Machine_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Site_ID", nullable = false)
    private Site site;

    @Column(name = "Naam", nullable = false, length = 45)
    private String naam;

    @Column(name = "Status", nullable = false, length = 45)
    private String status;

}