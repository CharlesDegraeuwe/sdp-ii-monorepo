package hogent.sdp2.sdpii.gui.app.dashboard.afwezigheden;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AfwezighedenController extends VBox {
    @FXML VBox itemContainer;
    @FXML ComboBox teamPicker;
    @FXML Button see_more;
    public AfwezighedenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/afwezigheden/Afwezigheden.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        itemContainer.getChildren().add(new AfwezighedenItemController("Milan Van Bellingen", "ziekte"));
        itemContainer.getChildren().add(new AfwezighedenItemController("Marte De Backer", "vakantie"));
        String role = Sessie.getInstance().userRole();
        if (role.equals("manager") || role.equals("admin")) {
            teamPicker.setVisible(false);
            teamPicker.setManaged(false);
        }
        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.AFWEZIGHEID));
    }
}
