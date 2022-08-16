package application;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Game {
  
//  @FXML
//  Label result;
  
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
  
  private static final int HEIGHT = 600;
  private static final int WIDTH = 800;
  private static final int PLAYER_SIZE = 50;
  private Sprite player = new Sprite(WIDTH / 2, HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE, "player", Color.BLUE);
  
  public Game(Stage stage, int level) {
    this.level = level;
    root = new AnchorPane();
    this.stage = stage;
    
    BackgroundImage bImg = new BackgroundImage(Images.BACK_IMG[level], BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
    Background background = new Background(bImg);
    root.setBackground(background);
    
    scene = new Scene(createContent());
    stage.setScene(scene);
    stage.show();
    run();
  }
  
  private Parent createContent() {
    root.setPrefSize(WIDTH, HEIGHT);
    root.getChildren().add(player);
    
    AnimationTimer timer = new AnimationTimer() {

      @Override
      public void handle(long now) {
        if (!pause && !win && !gameOver) {
          update();
        } else if (win || gameOver) {
//          pause = true;
          stop();
          String message = "";
          Controller c = new Controller();
          if (win) {
            message = "You Win!";
          } else {
            message = "You lose!";
          }
          try {
            root = FXMLLoader.load(getClass().getResource("/fxml/GameOverScreen.fxml"));
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          scene = new Scene(root);
//          result.setText(message);
          stage.setScene(scene);
          stage.show();
        }
      }
      
    };
   
    timer.start();
    nextLevel();
    return root;
  }
  
  private void nextLevel() {
    for (int i = 0; i < 44; i++) {
      Sprite s = new Sprite(50 + i % 11 * 70, i / 11 * 50 + 20, PLAYER_SIZE - 5, PLAYER_SIZE - 5, "enemy", Color.RED);
      root.getChildren().add(s);
    }
  }
  
  private List<Sprite> sprites() {
    return root.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
  }
  public void update() {
    
    t += 0.032 * level;
    enemyBullets = 0;
    
    sprites().forEach(s -> {
      if (s.type.equals("enemybullet")) {
        enemyBullets++;
      }
    });
    
    if (enemyCount == 0 && player.dead == false && enemyBullets == 0) {
      win = true;
    }
    
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
          
        case "playerbullet": {
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
        }
        
        case "enemy":
          if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
            player.dead = true;
            gameOver = true;
            s.dead = true;
            enemyCount--;
          }
          if (s.tx < 40) {
            s.moveLeft();
          } else {
            s.moveRight();
          }
          s.tx = (s.tx + 1) % 80;
          double x = 0;
          if (sprites().size() > 20) {
            x++;
          }
          if (sprites().size() <= 5) {
            x--;
          }
          if (t > 2 - Math.random() * x) {
            if (Math.random() < 0.6 * level) {
              shoot(s);
            }
            t = 0;
          }
          break;
          
        case "player": {
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
      }
    });
    
    root.getChildren().removeIf(n -> {
      Sprite s = (Sprite) n;
      return s.dead;
    });
    
  }
  
  public void shoot(Sprite who) {
    String type = who.type;
    int h = 10;
    int w = type.equals("player") ? 20 : 10;
    Sprite s = new Sprite((int) who.getTranslateX() + 20, (int) who.getTranslateY(), h, w, type + "bullet", Color.BLACK);
    root.getChildren().add(s);
  }
  
  class Sprite extends Rectangle {
    boolean dead = false;
    final String type;
    boolean moveLeft, moveRight, moveUp, moveDown;
    int speed, originX, tx;
    
    Sprite(int x, int y, int w, int h, String type, Color color) {
      super(w, h, color);
      originX = x;
      this.type = type;
      
      if (type.equals("player")) {
        setFill(new ImagePattern(Images.PLAYER_IMG[level]));
        speed = 5;
      } else if (type.equals("enemy")) {
        setFill(new ImagePattern(Images.ENEMY_IMG[level]));
        speed = 1;
      } else if (type.equals("enemybullet")) {
        this.setFill(new ImagePattern(Images.BULLET_IMG[1]));
        speed = 2;
      } else {
        this.setFill(new ImagePattern(Images.BULLET_IMG[0]));
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