package hogent.sdp2.sdpii.gui.components.admin;

import domain.Beheerder;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class RegisterEmployeeForm extends VBox {
    private MainFrameController mf;
    private Stage stage;

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
        String naam = nameField.getText();
        String voornaam = surnameField.getText();
        String email = emailField.getText();
        String telefoon = phoneField.getText();

        LocalDate datum = dobField.getValue();
        String rol = roleComboBox.getValue();

        if (naam.isBlank() || voornaam.isBlank() || email.isBlank() || telefoon.isBlank() || datum == null || rol == null) {
            feedbackLabel.setText("Vul aub alle velden in.");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            System.out.println("Niet alle velden zijn ingevuld!");
            return;
        }
        String geboortedatumStr = datum.toString();

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
                System.out.println("Werknemer succesvol aangemaakt!");
                nameField.clear();
                surnameField.clear();
                emailField.clear();
                phoneField.clear();
                dobField.setValue(null);

                feedbackLabel.setText("Werknemer succesvol geregistreerd!");
                feedbackLabel.setStyle("-fx-text-fill: green;");
            } else {
                System.out.println("Er is iets misgegaan bij het registreren.");
                feedbackLabel.setText("Fout bij opslaan.");
            }
        });

        task.setOnFailed(e -> {
            System.out.println("Netwerkfout.");
        });

        new Thread(task).start();
    }
}
