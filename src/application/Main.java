package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Galaxy Shooter");
      stage.getIcons().add(Images.ICON_IMG);
      stage.show();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  
  public static void main(String[] args) { launch(args); }
  
}