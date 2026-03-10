package hogent.sdp2.sdpii.gui.app.overzicht.uren;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.overzicht.open_taken.OpenTakenItemController;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class UrenController extends VBox {
    @FXML VBox itemContainer;
    @FXML ComboBox teamPicker;
    @FXML Button see_more;

    public UrenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/uren/GeplandeUren.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        itemContainer.getChildren().add(new UrenItemController("Deze week:", "33 uur"));
        itemContainer.getChildren().add(new UrenItemController("Volgende week:", "41 uur"));
        boolean role = Sessie.getInstance().isWerknemer();
        if (role) {
            teamPicker.setVisible(false);
        }
        if(Sessie.getInstance().isSuperVisor()) {
            teamPicker.setVisible(false);
        }
        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.PLANNING));
    }
}
