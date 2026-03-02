package hogent.sdp2.sdpii.gui.components.app;

import domain.Sessie;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PageTitleController extends HBox {
    @FXML Label page_title_title;
    @FXML Label page_date;

    public PageTitleController(String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/PageTitle.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        this.page_title_title.setText(title);

        //hele datum bs
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.page_date.setText(now.format(formatter));
    }
}
