package OOPAirlines;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OOPAirlinesApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Register user");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("OOPAirlinesRegisterApp.fxml"))));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

}
