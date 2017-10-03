package ClearStartManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ClearStartManager extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ManagerUi.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("ClearStartManager/ManagerUi.css");
        stage.setTitle("ClearStartManager");
        stage.setMinHeight(300);
        stage.setMinWidth(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
