module tn.esprit.sirine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens tn.esprit.sirine to javafx.fxml;
    exports tn.esprit.sirine;
}