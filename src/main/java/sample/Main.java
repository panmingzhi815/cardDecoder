package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    final static String title_version ="东陆写卡停车场加密工具_1.2";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("cardDecode.fxml"));
        primaryStage.setTitle(title_version);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("key_72.png")));
        primaryStage.show();

        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
