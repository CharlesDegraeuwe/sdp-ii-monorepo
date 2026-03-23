package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Log_ID", nullable = false)
    private Integer id;

    @Column(name = "Werknemer_ID", nullable = false)
    private Integer werknemerId;

    @Column(name = "Type", nullable = false, length = 45)
    private String type;

    @Column(name = "Tabel", nullable = false, length = 45)
    private String tabel;

    @Column(name = "Record_ID", nullable = true)
    private Integer recordId;

    @Column(name = "Timestamp", nullable = false)
    private LocalDate timestamp;

    @Column(name = "test", nullable = true, length = 45)
    private String beschrijving;

}