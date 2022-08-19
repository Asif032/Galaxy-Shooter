package application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Game {
  
  @FXML
  CheckBox musicBox;
  
  private AnchorPane root;
  Scene scene;
  Stage stage;
  
  private double t = 0;
  boolean gameOver = false;
  int level;
  boolean win = false;
  int enemyCount = 44;
  int enemyBullets = 0;
  boolean pause = false;
  int score = 0;
  boolean bgMusic = true;
  
  MediaPlayer mediaPlayer;
  Media media;
  
  private static final int HEIGHT = 600;
  private static final int WIDTH = 800;
  private static final int PLAYER_SIZE = 50;
  private Sprite player;
  
  public Game(Stage stage, int level) {
    this.level = level;
    root = new AnchorPane();
    this.stage = stage;
    
    player = new Sprite(WIDTH / 2, HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE, "player", Color.BLUE);
    
    BackgroundImage bImg = new BackgroundImage(Images.BACK_IMG[level + 1], BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
    Background background = new Background(bImg);
    root.setBackground(background);
    
//    File f = new File("src/sounds/music.wav");
//    media = new Media(f.toURI().toString());
//    mediaPlayer = new MediaPlayer(media);
//    mediaPlayer.play();
    
    scene = new Scene(createContent());
    stage.setScene(scene);
    stage.show();
    run();
  }
  
//  public void backToGame(MouseEvent event) {
//    scene = new Scene(root);
//    stage.setScene(scene);
//    stage.show();
//  }
  
  
  private Parent createContent() {
    root.setPrefSize(WIDTH, HEIGHT);
    root.getChildren().add(player);
    
    AnimationTimer timer = new AnimationTimer() {

      @Override
      public void handle(long now) {
        if (!pause && !win && !gameOver) {
          update();
        } else if (win || gameOver) {
          stop();
//          mediaPlayer.stop();
          String message = "YOU";
          message += win ? " WIN!" : " LOSE!";
          try {
            root = FXMLLoader.load(getClass().getResource("/fxml/GameOverScreen.fxml"));
          } catch (IOException e) {}
          
          Label l = new Label(message);
          l.setLayoutX(400);
          l.setLayoutY(210);
          l.setFont(new Font("Lucida Console", 55));
          l.setTextFill(Color.ANTIQUEWHITE);
          root.getChildren().add(l);
          
          scene = new Scene(root);
          stage.setScene(scene);
        }
      }
      
    };
   
    timer.start();
    nextLevel();
    return root;
  }
  
  private void nextLevel() {
    for (int i = 0; i < 44; i++) {
      Sprite s = new Sprite(50 + 10 * level + i % 11 * 70, i / 11 * 50 + 20, PLAYER_SIZE - 5, PLAYER_SIZE - 5, "enemy", Color.RED);
      root.getChildren().add(s);
    }
  }
  
  private List<Sprite> sprites() {
    return root.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
  }
  
  public void shoot(Sprite who) {
    String type = who.type;
    int h = 10;
    int w = type.equals("player") ? 20 : 10;
    Sprite s = new Sprite((int) who.getTranslateX() + 20, (int) who.getTranslateY(), h, w, type + "bullet", Color.BLACK);
    root.getChildren().add(s);
  }
  
  public void update() {
    
//    if (bgMusic) {
//      mediaPlayer.play();
//    }
    
    int y = level == 0 ? 2 : level + 1;
    t += 0.032 * y;
    
    sprites().forEach(s -> {
      switch(s.type) {
      
        case "enemybullet":
          s.moveDown();
          if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
            player.dead = true;
            gameOver = true;
            s.dead = true;
          }
          
          if (s.getTranslateY() > HEIGHT) {
            s.dead = true;
          }
          break;
          
        case "playerbullet":
          s.moveUp();
          sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy -> {
            if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
              enemy.dead = true;
              s.dead = true;
              enemyCount--;
            }
          });
          
          if (s.getTranslateY() < 0) {
            s.dead = true;
          }
          break;
        
        case "enemy":
          if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
            player.dead = true;
            gameOver = true;
            s.dead = true;
            enemyCount--;
          }
          
          // enemy sidewise movement
          if (s.tx < 40) {
            s.moveLeft();
          } else {
            s.moveRight();
          }
          s.tx = (s.tx + 1) % 80;
          
          
          // adjusting probability of shooting if enemy number decreases
          double x = 0;
          if (enemyCount <= 15 + 5 * level) {
            x = 1;
          }
          if (enemyCount <= 5) {
            x = 2;
          }
          if (enemyCount == 1) {
            x = 4;
          }
          if (enemyCount <= 10 && level == 2) {
            x = 4;
          }
          
          if (t > 2 - Math.random() + x) {
            double p = 0.3 * (level + 1);
            if (Math.random() < p) {
              shoot(s);
            }
            t = 0;
          }
          break;
          
        case "player":
          if (s.moveLeft && s.getTranslateX() > 0) {
            s.moveLeft();
          } else if (s.moveRight && s.getTranslateX() < WIDTH - PLAYER_SIZE) {
            s.moveRight();
          } else if (s.moveDown && s.getTranslateY() < HEIGHT - PLAYER_SIZE) {
              s.moveDown();
          } else if (s.moveUp && s.getTranslateY() > 0) {
            s.moveUp();
          }
          break;
      }
    });
    
    root.getChildren().removeIf(n -> {
      Sprite s = (Sprite) n;
      return s.dead;
    });
    
    enemyBullets = 0;
    
    sprites().forEach(s -> {
      if (s.type.equals("enemybullet")) {
        enemyBullets++;
      }
    });
    
    if (enemyCount == 0 && player.dead == false && enemyBullets == 0) {
      win = true;
    }
    
  }
  
  class Sprite extends Rectangle {
    boolean dead = false;
    final String type;
    boolean moveLeft, moveRight, moveUp, moveDown;
    int tx;
    double speed;
    
    Sprite(int x, int y, int w, int h, String type, Color color) {
      super(w, h, color);
      this.type = type;
      
      if (type.equals("player")) {
        setFill(new ImagePattern(Images.PLAYER_IMG[0]));
        speed = 5;
      } else if (type.equals("enemy")) {
        setFill(new ImagePattern(Images.ENEMY_IMG[level]));
        speed = level > 0 ? 2 : 1;
//        speed = level + 1;
      } else if (type.equals("enemybullet")) {
        this.setFill(new ImagePattern(Images.BULLET_IMG[1]));
        speed = level > 0 ? 2 : 1;
//        speed = level + 1;
      } else {
        this.setFill(new ImagePattern(Images.BULLET_IMG[2]));
        speed = 6;
      }
      
      setTranslateX(x);
      setTranslateY(y);
    }
    
    void moveLeft() {
      setTranslateX(getTranslateX() - speed);
    }
    
    void moveRight() {
      setTranslateX(getTranslateX() + speed);
    }
    
    void moveUp() {
      setTranslateY(getTranslateY() - speed);
    }
    
    void moveDown() {
      setTranslateY(getTranslateY() + speed);
    }
    
  }

  public void run() {
    
    scene.setOnKeyPressed(e -> {
      switch(e.getCode()) {
      case A:
        player.moveLeft = true;
        player.moveDown = false;
        player.moveRight = false;
        player.moveUp = false;
        break;
        
      case S:
        player.moveDown = true;
        player.moveRight = false;
        player.moveUp = false;
        player.moveLeft = false;
        break;
        
      case D:
        player.moveRight = true;
        player.moveDown = false;
        player.moveUp = false;
        player.moveLeft = false;
        break;
        
      case W:
        player.moveUp = true;
        player.moveLeft = false;
        player.moveDown = false;
        player.moveRight = false;
        break;
        
      case P:
        pause = true;
//        try {
//          Parent p = FXMLLoader.load(getClass().getResource("/fxml/PauseMenu.fxml"));
//          scene = new Scene(p);
//          stage.setScene(scene);
//          stage.show();
//          bgMusic = false;
//          if (musicBox.isSelected()) {
//            bgMusic = true;
//          }
//        } catch (IOException e1) {
//          System.out.println(e1);
//        }
        break;
        
      case R:
        pause = false;
        break;
      }
    });
    
    scene.setOnKeyReleased(e -> {
      switch(e.getCode()) {
      case A:
        player.moveLeft = false;
        break;
      case S:
        player.moveDown = false;
      case D:
        player.moveRight = false;
        break;
      case W:
        player.moveUp = false;
        break;
      }
    });
    
    scene.setOnMouseClicked(e -> {
      shoot(player);
    });
  }
  
}