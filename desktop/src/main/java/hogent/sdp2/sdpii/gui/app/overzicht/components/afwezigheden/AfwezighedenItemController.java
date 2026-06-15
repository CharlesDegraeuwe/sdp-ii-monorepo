package hogent.sdp2.sdpii.gui.app.overzicht.components.afwezigheden;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AfwezighedenItemController extends HBox {
    @FXML Label emoji;
    @FXML Label naam;

    public AfwezighedenItemController(String name, String type) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/afwezigheden/AfwezighedenItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        naam.setText(name);
        emoji.setText(bepaalEmoji(type));
    }

    private String bepaalEmoji(String type) {
        if (type == null) return "❓";
        return switch (type.toLowerCase()) {
            case "vakantie", "verlof" -> "🌴";
            case "ziekte", "ziek"     -> "🤒";
            default                   -> "📋";
        };
    }
}