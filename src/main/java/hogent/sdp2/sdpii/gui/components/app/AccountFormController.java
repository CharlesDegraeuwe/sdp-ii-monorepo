package hogent.sdp2.sdpii.gui.components.app;

import domain.auth.Sessie;
import domain.werknemer.Werknemer;
import domain.werknemer.WerknemerService;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AccountFormController extends VBox {

    @FXML private TextField naamField;
    @FXML private TextField voornaamField;
    @FXML private TextField emailField;
    @FXML private TextField wachtwoordField;
    @FXML private TextField telefoonnummerField;
    @FXML private TextField geboortedatumField;
    private AppController app;
    private final WerknemerService service;

    private Werknemer huidigeWerknemer;

    @FXML private Button editButton;


    private boolean editing = false;

    public AccountFormController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/AccountForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.app = app;
        this.service = new WerknemerService();

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initializeForm();

    }
    private void initializeForm() {
        loadUserData();
        setEditable(false);
    }

    private void setEditable(boolean value) {
        naamField.setEditable(value);
        voornaamField.setEditable(value);
        emailField.setEditable(value);
        wachtwoordField.setEditable(value);
        telefoonnummerField.setEditable(value);
        geboortedatumField.setEditable(value);
    }

    private void loadUserData() {
        huidigeWerknemer = Sessie.getIngelogdeWerknemer();

        if (huidigeWerknemer != null) {
            naamField.setText(huidigeWerknemer.getNaam());
            voornaamField.setText(huidigeWerknemer.getVoornaam());
            emailField.setText(huidigeWerknemer.getEmail());
            wachtwoordField.setText(huidigeWerknemer.getWachtwoord());
            telefoonnummerField.setText(huidigeWerknemer.getTelefoonnummer());
            geboortedatumField.setText(huidigeWerknemer.getGeboortedatum().toString());
        }
    }

    @FXML
    private void toggleEdit() {

        editing = !editing;

        if (editing) {
            setEditable(true);
            editButton.setText("Opslaan");
        } else {
            saveUser();
            setEditable(false);
            editButton.setText("Wijzig");
        }


    }


    private void saveUser() {

        if (huidigeWerknemer == null) return;

        huidigeWerknemer.setNaam(naamField.getText());
        huidigeWerknemer.setVoornaam(voornaamField.getText());
        huidigeWerknemer.setEmail(emailField.getText());
        huidigeWerknemer.setWachtwoord(wachtwoordField.getText());
        huidigeWerknemer.setTelefoonnummer(telefoonnummerField.getText());
        huidigeWerknemer.setGeboortedatum(java.time.LocalDate.parse(geboortedatumField.getText()));

        service.update(huidigeWerknemer);
    }



}
