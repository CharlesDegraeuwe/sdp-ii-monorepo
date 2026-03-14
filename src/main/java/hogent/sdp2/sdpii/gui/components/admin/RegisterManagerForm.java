package hogent.sdp2.sdpii.gui.components.admin;

import domain.Beheerder;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;

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
        String naam = nameField.getText();
        String voornaam = surnameField.getText();
        String email = emailField.getText();
        String telefoon = phoneField.getText();
        LocalDate datum = dobField.getValue();

        if (naam.isBlank() || voornaam.isBlank() || email.isBlank() || telefoon.isBlank() || datum == null) {
            feedbackLabel.setText("Vul aub alle velden in.");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String geboortedatumStr = datum.toString();
        String rol = "Manager";
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return Beheerder.getInstance().getWerknemersFacade().registreerWerknemer(
                        naam, voornaam, email, telefoon, geboortedatumStr, rol
                );
            }
        };

        task.setOnSucceeded(e -> {
            boolean gelukt = task.getValue();
            if (gelukt) {
                feedbackLabel.setText("Manager succesvol geregistreerd!");
                feedbackLabel.setStyle("-fx-text-fill: green;");

                // Velden leegmaken
                nameField.clear();
                surnameField.clear();
                emailField.clear();
                phoneField.clear();
                dobField.setValue(null);
            } else {
                feedbackLabel.setText("Fout bij opslaan. Bestaat deze e-mail al?");
                feedbackLabel.setStyle("-fx-text-fill: red;");
            }
        });

        task.setOnFailed(e -> {
            feedbackLabel.setText("Netwerkfout.");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        });

        new Thread(task).start();
    }
}
