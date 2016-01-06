package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("cardDecode.fxml"));
        primaryStage.setTitle("东陆写卡停车场加密工具");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("key_72.png")));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
