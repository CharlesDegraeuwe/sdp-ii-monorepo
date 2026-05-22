package hogent.sdp2.sdpii.gui.components.auth;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.auth.LoginController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController extends VBox {

    @FXML private TextField txtEmail;
    @FXML private VBox wachtwoordSection;
    @FXML private PasswordField txtWachtwoord;
    @FXML private TextField txtCode;
    @FXML private Label lblFout;
    @FXML private Label lblTitle;
    @FXML private Button mainBtn;
    @FXML private Label lblToggle;

    private MainFrameController mf;
    private Stage stage;

    private enum Modus { CODE, WACHTWOORD }
    private enum Stap  { EMAIL, CODE }

    private Modus modus = Modus.CODE;
    private Stap  stap  = Stap.EMAIL;
    private String huidigEmail;

    public LoginFormController(MainFrameController mf, Stage stage, LoginController login) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/auth/LoginForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.mf    = mf;
        this.stage = stage;
        init();
    }

    private void init() {
        txtEmail.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleBtn(); });
        txtWachtwoord.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleBtn(); });
        txtCode.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleBtn(); });
    }

    // ── Hoofd actie ──────────────────────────────────────────────────────────

    @FXML
    private void handleBtn(javafx.event.ActionEvent e) { handleBtn(); }

    private void handleBtn() {
        lblFout.setText("");
        lblFout.setStyle("");

        if (modus == Modus.WACHTWOORD) {
            loginMetWachtwoord();
        } else if (stap == Stap.EMAIL) {
            verzendCode();
        } else {
            verifieerCode();
        }
    }

    // ── Login met wachtwoord ─────────────────────────────────────────────────

    private void loginMetWachtwoord() {
        setBusy(true);
        String email     = txtEmail.getText();
        String wachtwoord = txtWachtwoord.getText();

        Task<WerknemerDTO> task = new Task<>() {
            @Override protected WerknemerDTO call() {
                return Beheerder.getInstance().getAuthFacade()
                        .loginMetWachtwoord(email, wachtwoord);
            }
        };

        task.setOnSucceeded(e -> {
            WerknemerDTO w = task.getValue();
            if (w == null || "Geblokkeerd".equalsIgnoreCase(w.status())) {
                toonFout("Je account is geblokkeerd.");
                Sessie.getInstance().uitloggen();
            } else {
                navigeerNaarApp();
            }
        });

        task.setOnFailed(e -> toonFout("Ongeldig email of wachtwoord."));

        new Thread(task).start();
    }

    // ── Login met e-mailcode, stap 1: stuur code ─────────────────────────────

    private void verzendCode() {
        setBusy(true);
        String email = txtEmail.getText();

        Task<Void> task = new Task<>() {
            @Override protected Void call() {
                Beheerder.getInstance().getAuthFacade().verzendLoginEmail(email);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            huidigEmail = email;
            schakelNaarCodeStap();
        });

        task.setOnFailed(e -> toonFout("Email niet gevonden of er ging iets mis."));

        new Thread(task).start();
    }

    // ── Login met e-mailcode, stap 2: verifieer code ─────────────────────────

    private void verifieerCode() {
        setBusy(true);
        String code = txtCode.getText();

        Task<WerknemerDTO> task = new Task<>() {
            @Override protected WerknemerDTO call() {
                return Beheerder.getInstance().getAuthFacade()
                        .loginMetCode(huidigEmail, code);
            }
        };

        task.setOnSucceeded(e -> {
            WerknemerDTO w = task.getValue();
            if (w == null || "Geblokkeerd".equalsIgnoreCase(w.status())) {
                toonFout("Je account is geblokkeerd.");
                Sessie.getInstance().uitloggen();
            } else {
                navigeerNaarApp();
            }
        });

        task.setOnFailed(e -> toonFout("Ongeldige code. Probeer opnieuw."));

        new Thread(task).start();
    }

    // ── Modus wisselen ───────────────────────────────────────────────────────

    @FXML
    private void toggleModus(javafx.scene.input.MouseEvent e) {
        if (modus == Modus.CODE) {
            schakelNaarWachtwoordModus();
        } else {
            schakelNaarCodeModus();
        }
    }

    private void schakelNaarWachtwoordModus() {
        modus = Modus.WACHTWOORD;
        stap  = Stap.EMAIL;

        lblTitle.setText("Log in op je account");
        txtEmail.setDisable(false);
        txtEmail.setText("");

        wachtwoordSection.setVisible(true);
        wachtwoordSection.setManaged(true);
        txtCode.setVisible(false);
        txtCode.setManaged(false);

        mainBtn.setText("Login");
        lblToggle.setText("Login met token");
        lblFout.setText("");
        setBusy(false);
    }

    private void schakelNaarCodeModus() {
        modus = Modus.CODE;
        stap  = Stap.EMAIL;

        lblTitle.setText("Log in op je account");
        txtEmail.setDisable(false);
        txtEmail.setText("");

        wachtwoordSection.setVisible(false);
        wachtwoordSection.setManaged(false);
        txtCode.setVisible(false);
        txtCode.setManaged(false);

        mainBtn.setText("Verdergaan");
        lblToggle.setText("Login met wachtwoord");
        lblFout.setText("");
        setBusy(false);
    }

    private void schakelNaarCodeStap() {
        stap = Stap.CODE;

        txtEmail.setDisable(true);
        txtCode.setVisible(true);
        txtCode.setManaged(true);
        txtCode.setText("");
        txtCode.requestFocus();

        mainBtn.setText("Login");
        setBusy(false);
        lblFout.setText("Code verzonden naar " + huidigEmail);
        lblFout.setStyle("-fx-text-fill: #2e9e4f;");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void toonFout(String bericht) {
        lblFout.setText(bericht);
        lblFout.setStyle("-fx-text-fill: #E31B35;");
        setBusy(false);
    }

    private void setBusy(boolean bezig) {
        mainBtn.setDisable(bezig);
        mainBtn.setText(bezig ? "Laden..." : huidigBtnLabel());
    }

    private String huidigBtnLabel() {
        if (modus == Modus.WACHTWOORD) return "Login";
        return stap == Stap.EMAIL ? "Verdergaan" : "Login";
    }

    private void navigeerNaarApp() {
        mf.setCenter(new AppController(stage, mf));
    }

    public void reset() {
        schakelNaarCodeModus();
        huidigEmail = null;
    }
}
