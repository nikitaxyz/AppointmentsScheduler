module xyz.bubblesort.software2wgu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens xyz.bubblesort.software2wgu to javafx.fxml;
    exports xyz.bubblesort.software2wgu;
    exports xyz.bubblesort.software2wgu.controllers;
    opens xyz.bubblesort.software2wgu.controllers to javafx.fxml;
    exports xyz.bubblesort.software2wgu.models;
    opens xyz.bubblesort.software2wgu.models to javafx.fxml;
}