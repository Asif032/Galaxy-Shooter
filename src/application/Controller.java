package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Controller {
  
  String selectedStyle = "-fx-background-color: #006400; -fx-border-color: #7FFFD4; -fx-border-width: 2;";
  String unSelectedStyle = "-fx-background-color: transparent; -fx-border-color: #7FFFD4; -fx-border-width: 2;";
  
  private Stage stage;
  private Scene scene;
  private Parent root;
  
  public void switchToMainMenu(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    String css = getClass().getResource("application.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToPlay(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Play.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    ((Node) event.getSource()).setEffect(new DropShadow());
    String css = getClass().getResource("application.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToOptions(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Options.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    String css = getClass().getResource("application.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToHelp(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Help.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    String css = getClass().getResource("application.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToLevel1(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Level1.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    String css = getClass().getResource("application.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
    stage.show();
  }
  
  public void mouseEntered(MouseEvent event) throws Exception {
    ((Node) event.getSource()).setStyle(selectedStyle);
  }
  
  public void mouseExited(MouseEvent event) throws Exception {
    ((Node) event.getSource()).setStyle(unSelectedStyle);
  }
  
  public void Exit(MouseEvent event) throws Exception {
//    Alert alert = new Alert(null);
    System.exit(0);
  }
  
}
