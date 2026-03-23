package hogent.sdp2.sdpii.gui.components.admin;

import domain.Beheerder;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RegisterManagerForm extends VBox {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker dobField;
    @FXML private Label feedbackLabel;

    public RegisterManagerForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/CreateManagerForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleBack() {
        Router.getInstance().navigeerNaar(Scherm.ADMIN_HOME);
    }

    @FXML
    private void handleRegistreerManager() {
        resetFieldStyles();

        String naam = nameField.getText();
        String voornaam = surnameField.getText();
        String email = emailField.getText();
        String telefoon = phoneField.getText();
        String geboortedatumStr = dobField.getValue() != null ? dobField.getValue().toString() : null;

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                // Validatie zit in WerknemersFacade - gooit IllegalArgumentException bij fouten
                return Beheerder.getInstance().getWerknemersFacade().registreerWerknemer(
                        naam, voornaam, email, telefoon, geboortedatumStr, "Manager");
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                nameField.clear(); surnameField.clear(); emailField.clear();
                phoneField.clear(); dobField.setValue(null);
                feedbackLabel.setText("Manager succesvol geregistreerd!");
                feedbackLabel.setStyle("-fx-text-fill: green;");
            } else {
                feedbackLabel.setText("Fout bij opslaan. Bestaat deze e-mail al?");
                feedbackLabel.setStyle("-fx-text-fill: red;");
            }
        });

        task.setOnFailed(e -> {
            Throwable oorzaak = task.getException();
            if (oorzaak instanceof IllegalArgumentException) {
                feedbackLabel.setText(oorzaak.getMessage());
                highlightFout(oorzaak.getMessage());
            } else {
                feedbackLabel.setText("Netwerkfout.");
            }
            feedbackLabel.setStyle("-fx-text-fill: red;");
        });

        new Thread(task).start();
    }

    private void resetFieldStyles() {
        String normal = "-fx-border-color: transparent;";
        nameField.setStyle(normal);
        surnameField.setStyle(normal);
        emailField.setStyle(normal);
        phoneField.setStyle(normal);
        dobField.setStyle(normal);
    }

    private void highlightFout(String fout) {
        String error = "-fx-border-color: #E31B35; -fx-border-radius: 20; -fx-border-width: 1.5;";
        if (fout.contains("Naam")) nameField.setStyle(error);
        else if (fout.contains("Voornaam")) surnameField.setStyle(error);
        else if (fout.contains("mail")) emailField.setStyle(error);
        else if (fout.contains("telefoon") || fout.contains("Telefoon")) phoneField.setStyle(error);
        else if (fout.contains("Geboortedatum") || fout.contains("oud")) dobField.setStyle(error);
    }
}