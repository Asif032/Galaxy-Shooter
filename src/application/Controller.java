package application;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Controller {
  
  String selectedStyle = "-fx-background-color: #006400; -fx-border-color: #FF0000; -fx-border-width: 5;";
  String unSelectedStyle = "-fx-background-color: transparent; -fx-border-color: #7FFFD4; -fx-border-width: 2;";
  
  Stage stage;
  Scene scene;
  Parent root;
  
  @FXML
  CheckBox musicBox, soundsBox;
  
  public void switchToMainMenu(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToPlay(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Play.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToOptions(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Options.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToHelp(MouseEvent event) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/Help.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  
  public void switchToLevel1(MouseEvent event) throws Exception {
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    switchToGameScreen(stage, 0);
  }
  
  public void switchToLevel2(MouseEvent event) throws Exception {
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    switchToGameScreen(stage, 1);
  }
  
  public void switchToLevel3(MouseEvent event) throws Exception {
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    switchToGameScreen(stage, 2);
  }
  
  public void switchToGameScreen(Stage st, int level) throws Exception {
    Game game = new Game(st, level);
  }
  
  public void switchToGameOver(Stage st, String message) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/fxml/GameOverScreen.fxml"));
    scene = new Scene(root);
    st.setScene(scene);
    st.show();
  }
  
  public void mouseEntered(MouseEvent event) throws Exception {
    ((Node) event.getSource()).setStyle(selectedStyle);
  }
  
  public void mouseExited(MouseEvent event) throws Exception {
    ((Node) event.getSource()).setStyle(unSelectedStyle);
  }
  
  public void playMusic(ActionEvent event) throws Exception {
    File f = new File("src/sounds/music.wav");
    Media media = new Media(f.toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    
    if (musicBox.isSelected()) {
      mediaPlayer.play();
      System.out.println("Music selected");
    }
  }
  
  public void Exit(MouseEvent event) throws Exception {
//    Alert alert = new Alert(null);
    System.exit(0);
  }
  
}
