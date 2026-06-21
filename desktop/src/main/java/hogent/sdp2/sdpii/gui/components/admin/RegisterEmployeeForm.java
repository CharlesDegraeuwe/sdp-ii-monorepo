package hogent.sdp2.sdpii.gui.components.admin;

import domain.Beheerder;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RegisterEmployeeForm extends VBox {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker dobField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label feedbackLabel;

    public RegisterEmployeeForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/CreateEmployeeForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        roleComboBox.getItems().addAll("Werknemer", "Supervisor");
        roleComboBox.getSelectionModel().select("Werknemer");
    }

    @FXML
    private void handleBack() {
        Router.getInstance().navigeerNaar(Scherm.ADMIN_HOME);
    }

    @FXML
    private void handleRegistreer() {
        resetFieldStyles();

        String naam = nameField.getText();
        String voornaam = surnameField.getText();
        String email = emailField.getText();
        String telefoon = phoneField.getText();
        String geboortedatumStr = dobField.getValue() != null ? dobField.getValue().toString() : null;
        String rol = roleComboBox.getValue();

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return Beheerder.getInstance().getWerknemersFacade().registreerWerknemer(
                        naam, voornaam, email, telefoon, geboortedatumStr, rol);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                nameField.clear(); surnameField.clear(); emailField.clear();
                phoneField.clear(); dobField.setValue(null);
                feedbackLabel.setText("Werknemer succesvol toegevoegd aan het systeem!");
                feedbackLabel.setStyle("-fx-text-fill: #166534;"); // Modern donkergroen
            } else {
                feedbackLabel.setText("Fout bij opslaan. Bestaat deze e-mail al?");
                feedbackLabel.setStyle("-fx-text-fill: #e31b35;");
            }
        });

        task.setOnFailed(e -> {
            Throwable oorzaak = task.getException();
            if (oorzaak instanceof IllegalArgumentException) {
                feedbackLabel.setText(oorzaak.getMessage());
                highlightFout(oorzaak.getMessage());
            } else {
                feedbackLabel.setText("Er is een netwerkfout opgetreden.");
            }
            feedbackLabel.setStyle("-fx-text-fill: #e31b35;");
        });

        new Thread(task).start();
    }

    private void resetFieldStyles() {
        nameField.getStyleClass().remove("input-error");
        surnameField.getStyleClass().remove("input-error");
        emailField.getStyleClass().remove("input-error");
        phoneField.getStyleClass().remove("input-error");
        dobField.getStyleClass().remove("input-error");
    }

    private void highlightFout(String fout) {
        String f = fout.toLowerCase();

        if (f.contains("voornaam")) surnameField.getStyleClass().add("input-error");
        else if (f.contains("naam")) nameField.getStyleClass().add("input-error");
        else if (f.contains("mail") || f.contains("e-mail")) emailField.getStyleClass().add("input-error");
        else if (f.contains("telefoon") || f.contains("gsm")) phoneField.getStyleClass().add("input-error");
        else if (f.contains("geboorte") || f.contains("oud")) dobField.getStyleClass().add("input-error");
    }
}