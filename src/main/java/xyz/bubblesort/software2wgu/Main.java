package xyz.bubblesort.software2wgu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.bubblesort.software2wgu.controllers.AppointmentsController;
import xyz.bubblesort.software2wgu.controllers.LoginController;
import xyz.bubblesort.software2wgu.controllers.CustomersController;
import xyz.bubblesort.software2wgu.controllers.ScenesAndControllers;
import xyz.bubblesort.software2wgu.db.DBManager;
import xyz.bubblesort.software2wgu.models.Repository;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Appointments Scheduler.
 * GUI-based scheduling JavaFX with FXML desktop application connected to MySQL database.
 * Users can log in and perform CRUD operations on different tables,
 * like adding a new customer or scheduling a new appointment.
 * Also, users can generate various reports.
 *
 * @version 1.0.0
 * @author Nikita Kukshynsky
 */
public class Main extends Application {
    /**
     * Starts the JavaFX application. Initializes all views and controllers. Opens database connection.
     * @param stage the main stage of JavaFX application
     * @throws IOException failed I/O operations exception
     */
    @Override
    public void start(Stage stage) throws IOException {
        DBManager.openConnection();
        Repository.fetchData();

        ResourceBundle loginStrings = ResourceBundle.getBundle("LoginStrings", Locale.getDefault());

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"), loginStrings);
        Scene loginScene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();

        fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load());
        CustomersController customersController = fxmlLoader.getController();

        fxmlLoader = new FXMLLoader(Main.class.getResource("appointments-view.fxml"));
        Scene appointmentsScene = new Scene(fxmlLoader.load());
        AppointmentsController appointmentsController = fxmlLoader.getController();


        ScenesAndControllers sc = new ScenesAndControllers(stage, loginScene, loginController, mainScene, customersController, appointmentsScene, appointmentsController);
        loginController.setScenesAndControllers(sc);
        customersController.setScenesAndControllers(sc);
        appointmentsController.setScenesAndControllers(sc);
        sc.setStageToLogin();

        stage.setOnCloseRequest(event -> DBManager.closeConnection());
        stage.show();
    }

    /**
     * Entrance of the application
     * Javadoc documentation located in the "Documentation" folder
     * @param args program arguments
     */
    public static void main(String[] args) {
        launch();
    }
}