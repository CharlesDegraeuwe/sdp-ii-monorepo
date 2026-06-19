package hogent.sdp2.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "Type", nullable = false, length = 45)
    private String type;

    @Column(name = "Tabel", nullable = false, length = 45)
    private String tabel;

    @Column(name = "Record_ID", nullable = false)
    private Integer recordId;

    @Column(name = "Timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "Beschrijving", nullable = false, length = 45)
    private String Beschrijving;
}
