package hogent.sdp2.sdpii.gui.app.overzicht.components.jouw_uren;

import domain.auth.Sessie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class JouwUrenController extends VBox {
    @FXML VBox thisweek_item_container;
    @FXML VBox nextweek_item_container;
    @FXML VBox laterthismonth_item_container;
    @FXML ComboBox teamPicker;

    public JouwUrenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/jouw_uren/JouwUren.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // This week
        thisweek_item_container.getChildren().addAll(
                new JouwUrenItemController("Vandaag", "08:30 - 12:30", "Locatie B"),
                new JouwUrenItemController("Vandaag", "13:30 - 16:30", "Locatie A"),
                new JouwUrenItemController("Morgen", "15:30 - 19:30", "Locatie C")
        );

        // Next week
        nextweek_item_container.getChildren().addAll(
                new JouwUrenItemController("13/12/2025", "15:30 - 18:30", "Locatie A"),
                new JouwUrenItemController("15/12/2025", "08:30 - 16:30", "Locatie B"),
                new JouwUrenItemController("22/12/2025", "18:30 - 23:30", "Locatie C")
        );

        // Later this month
        laterthismonth_item_container.getChildren().addAll(
                new JouwUrenItemController("25/12/2025", "18:00 - 21:30", "Locatie B"),
                new JouwUrenItemController("27/12/2025", "08:00 - 16:30", "Locatie C")
        );

        if (!Sessie.getInstance().isMangerOrAdmin()) {
            teamPicker.setVisible(false);
        }
    }
}
