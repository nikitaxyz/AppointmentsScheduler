package xyz.bubblesort.software2wgu.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Helps to switch between scenes and access stage and controllers
 */
public class ScenesAndControllers {
    private final Stage stage;
    private final Scene loginScene;
    private final LoginController loginController;
    private final Scene customersScene;
    private final CustomersController customersController;
    private final Scene appointmentScene;
    private final AppointmentsController appointmentsController;

    /**
     * Constructs a new object that holds a stage, and all scenes and controllers.
     *
     * @param stage main stage of the application
     * @param loginScene login view scene
     * @param loginController login view controller
     * @param customersScene customers view scene
     * @param customersController customers view controller
     * @param appointmentScene appointments view scene
     * @param appointmentsController appointments view controller
     */
    public ScenesAndControllers(Stage stage, Scene loginScene, LoginController loginController, Scene customersScene, CustomersController customersController, Scene appointmentScene, AppointmentsController appointmentsController) {
        this.stage = stage;
        this.loginScene = loginScene;
        this.loginController = loginController;
        this.customersScene = customersScene;
        this.customersController = customersController;
        this.appointmentScene = appointmentScene;
        this.appointmentsController = appointmentsController;
    }

    /**
     * Getter for the loginController.
     *
     * @return login Controller
     */
    public LoginController getLoginController() {
        return loginController;
    }

    /**
     * Getter for the customersController.
     *
     * @return customers Controller
     */
    public CustomersController getCustomersController() {
        return customersController;
    }

    /**
     * Getter for the appointmentsController.
     *
     * @return appointments Controller
     */
    public AppointmentsController getAppointmentsController() {
        return appointmentsController;
    }

    /**
     * Getter for the stage.
     *
     * @return main stage of the application
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets stage to the log in scene
     */
    public void setStageToLogin() {
        stage.setTitle("Login");
        stage.setScene(loginScene);
    }

    /**
     * Sets stage to the customers scene
     */
    public void setStageToCustomers() {
        stage.setTitle("Customers");
        stage.setScene(customersScene);
    }

    /**
     * Sets stage to the appointments scene
     */
    public void setStageToAppointment() {
        stage.setTitle("Appointments");
        stage.setScene(appointmentScene);
    }
}
