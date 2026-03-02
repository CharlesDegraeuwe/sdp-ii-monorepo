package domain.oud.logger;

import domain.facades.entities.Werknemer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Log_ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Werknemer_ID", nullable = false)
    private Werknemer werknemer;

    //is het create/update/delete
    @Column(name = "Type", nullable = false, length = 45)
    private String type;

    //in welke tabel
    @Column(name = "Tabel", nullable = false, length = 45)
    private String tabel;

    @Column(name = "Record_ID", nullable = false)
    private Integer recordId;

    @Column(name = "Timestamp", nullable = false)
    private LocalDateTime timestamp;

    //de details?
    @Column(name = "test", nullable = false, length = 45)
    private String test;

}