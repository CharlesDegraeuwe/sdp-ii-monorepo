package hogent.sdp2.sdpii.gui.app.overzicht.components.afwezigheden;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class AfwezighedenItemController extends HBox {
    @FXML FontIcon icon;
    @FXML Label naam;

    public AfwezighedenItemController(String name, String typeAbsence) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/afwezigheden/AfwezighedenItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        naam.setText(name);
        icon.setIconLiteral(typeAbsence == "vakantie" ? "ion4-md-car" : "ion4-md-close-circle-outline");
    }
}
