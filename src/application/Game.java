package application;

import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.scene.*;
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

public class Game implements Runnable {
  
  private AnchorPane root;
  Scene scene;
  private double t = 0;
  boolean gameOver = false;
  int level;
  
  private static final int HEIGHT = 600;
  private static final int WIDTH = 800;
  private static final int PLAYER_SIZE = 50;
  private Sprite player = new Sprite(WIDTH / 2, HEIGHT - PLAYER_SIZE, 50, 50, "player", Color.BLUE);
  
  public Game(Stage stage, int level) {
    this.level = level;
    root = new AnchorPane();
    
    BackgroundImage bImg = new BackgroundImage(Images.BACK_IMG[level], BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
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
        update();
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
    t += 0.032;
    
    sprites().forEach(s -> {
      switch(s.type) {
      
        case "enemybullet":
          s.moveDown();
          if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
            player.dead = true;
            s.dead = true;
          }
          break;
          
        case "playerbullet": {
          s.moveUp();
          sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy -> {
            if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
              enemy.dead = true;
              s.dead = true;
            }
          });
          break;
        }
        
        case "enemy":
          if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
            player.dead = true;
            s.dead = true;
          }
          if (s.tx < 40) {
            s.moveLeft();
          } else {
            s.moveRight();
          }
          s.tx = (s.tx + 1) % 80;
          if (t > 2 - Math.random()) {
            if (Math.random() < 0.6) {
              shoot(s);
            }
            t = 0;
          }
          break;
          
        case "player": {
          if (s.moveLeft && s.getTranslateX() > 0d) {
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
    Sprite s = new Sprite((int) who.getTranslateX() + 20, (int) who.getTranslateY(), 10, 20, who.type + "bullet",    Color.BLACK);
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
        this.setFill(new ImagePattern(Images.BULLET_IMG[level + 1]));
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

  @Override
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