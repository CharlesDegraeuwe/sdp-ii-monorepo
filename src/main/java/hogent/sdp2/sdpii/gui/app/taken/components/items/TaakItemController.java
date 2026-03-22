package hogent.sdp2.sdpii.gui.app.taken.components.items;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Optional;

public class TaakItemController extends HBox {
    @FXML Label title;
    @FXML Label datum;
    @FXML CheckBox taak_checkbox;
    @FXML HBox taak_delete_btn;

    public void setOnAfgewerkt(Runnable callback) {
        taak_checkbox.setOnAction(e -> {
            if (taak_checkbox.isSelected()) callback.run();
        });
    }

    public void setOnVerwijder(Runnable callback) {
        taak_delete_btn.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Taak verwijderen");
            alert.setHeaderText(null);
            alert.setContentText("Bent u zeker dat u deze taak wilt verwijderen?");

            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            dialogPane.getStylesheets().add(getClass().getResource("/css/tasks.css").toExternalForm());
            dialogPane.getStyleClass().add("card");

            javafx.scene.control.Button okBtn = (javafx.scene.control.Button) dialogPane.lookupButton(ButtonType.OK);
            okBtn.setText("Verwijderen");
            okBtn.getStyleClass().setAll("create-task-btn");

            javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) dialogPane.lookupButton(ButtonType.CANCEL);
            cancelBtn.getStyleClass().setAll("filter-knop");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                callback.run();
            }
        });
    }

    public TaakItemController(String title, String datum) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/items/TaakItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.title.setText(title);
        this.datum.setText(datum);
    }
}
