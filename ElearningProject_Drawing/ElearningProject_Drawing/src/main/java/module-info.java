module org.elearningprojectdrawing.elearningproject_drawing {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.elearningprojectdrawing.elearningproject_drawing to javafx.fxml;
    exports org.elearningprojectdrawing.elearningproject_drawing;
}