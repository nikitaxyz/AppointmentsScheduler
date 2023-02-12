package xyz.bubblesort.software2wgu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import xyz.bubblesort.software2wgu.db.Queries;
import xyz.bubblesort.software2wgu.models.Appointment;
import xyz.bubblesort.software2wgu.models.Repository;
import xyz.bubblesort.software2wgu.models.User;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * JavaFX controller for log in scene (login-view.fxml).
 */
public class LoginController implements Initializable {
    @FXML
    private Label locale;
    @FXML
    private TextField userIDField;
    @FXML
    private Label errors;
    @FXML
    private PasswordField passwordField;

    private ScenesAndControllers scenesAndControllers;
    private ResourceBundle bundle;

    /**
     * Sets resource bundle for the controller and displays users Locale.
     *
     * @param location used to resolve relative paths for the root object
     * @param bundle   resource bundle for the controller
     */
    @FXML
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;
        locale.setText("ZoneID: " + ZoneId.systemDefault());
    }

    /**
     * Setter for the scenesAndControllers.
     *
     * @param scenesAndControllers object with all controllers, scenes and a stage
     */
    public void setScenesAndControllers(ScenesAndControllers scenesAndControllers) {
        this.scenesAndControllers = scenesAndControllers;
    }

    /**
     * Track user activity by recording all user log in attempts in a file named login_activity.txt.
     *
     * @param isSuccessful true if user was found in the database and a password matched and false otherwise
     * @param userID       user input for the userID field
     * @param password     user input for the password field
     * @param ts           time information about user attempt
     * @throws IOException if file is not accessible or other IO error has occurred
     */
    public void appendLog(boolean isSuccessful, int userID, String password, Timestamp ts) throws IOException {
        String attempt = isSuccessful ? "SUCCEED" : "FAILED";
        String text = "%s user %s to log in with userID = %s and password = %s on %s at %s".formatted(ts, attempt, userID, password, ts.toLocalDateTime().toLocalDate(), ts.toLocalDateTime().toLocalTime());
        Files.writeString(Path.of("login_activity.txt"), text + System.lineSeparator(), CREATE, APPEND);
    }

    /**
     * Tries to log in user with provided userID and password data. If log in was successful, user can proceed with customers view.
     */
    public void onLogin() {
        try {
            int userID = Integer.parseInt(userIDField.getText());
            String userPassword = passwordField.getText();
            User user = Queries.getUser(userID, userPassword);
            if (user != null) {
                appendLog(true, userID, userPassword, Timestamp.from(Instant.now()));
                Repository.setUser(user);
                Appointment appointmentWithin15minutes = Queries.appointmentWithin15Minutes(ZonedDateTime.now(ZoneId.systemDefault()));
                if (appointmentWithin15minutes != null) {
                    scenesAndControllers.getCustomersController().setAlert(appointmentWithin15minutes);
                }
                scenesAndControllers.setStageToCustomers();
            } else {
                appendLog(false, userID, userPassword, Timestamp.from(Instant.now()));
                errors.setText(bundle.getString("errorNoUser"));
            }
        } catch (NumberFormatException e) {
            errors.setText(bundle.getString("errorNoIntID"));
        } catch (Exception e) {
            errors.setText(bundle.getString("error") + e.getLocalizedMessage());
        }
    }
}
