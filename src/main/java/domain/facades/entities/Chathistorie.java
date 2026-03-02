package domain.facades.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "chathistorie")
public class Chathistorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chat_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    @Column(name = "Vraag", nullable = false)
    private String vraag;

    @Column(name = "Antwoord", nullable = false)
    private String antwoord;

    @Column(name = "Timestamp", nullable = false)
    private LocalDate timestamp;

}