import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.*;
import javafx.stage.*;

public class MainScreen extends GeneralScreen {

    MathDoku controller;
    Stage owner;
    Scene scene;
    VBox pane;
    HBox outer;
    int wHeight;
    int wWidth;
    ArrayList<Button> buttons = new ArrayList<Button>();

    public MainScreen() {
        this.createStatic();
        this.layout();
    }

    public MainScreen(Stage owner, MathDoku controller) {
        this.owner = owner;
        this.controller = controller;
        this.createStatic();
        this.layout();
        this.createScene();
    }

    private void createStatic() {
        this.pane = new VBox(); 
        this.outer = new HBox();
    }

    public void layout() {
        this.outer.setPadding(new Insets(20));
        this.pane.setSpacing(20);
        this.pane.setAlignment(Pos.CENTER);
        this.outer.setAlignment(Pos.CENTER);

        Label title = new Label("MathDoku");
        title.setStyle("-fx-text-fill: #003B45; -fx-font-size: 36px;");
        Button newGame = new Button("New Random Game");
        Button loadGame = new Button("Load Game from File");
        Button instructions = new Button("Instructions");

        newGame.addEventHandler(ActionEvent.ANY, new NewGame());
        loadGame.addEventHandler(ActionEvent.ANY, new GameLoad());
        instructions.addEventHandler(ActionEvent.ANY, new InstructionsLoad());
        this.buttons.add(newGame);
        this.buttons.add(loadGame);
        this.buttons.add(instructions);
        for(int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).setCursor(Cursor.HAND);
            this.buttons.get(i).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 16px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 5px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px; -fx-background-radius: 10px;");
            this.buttons.get(i).setMinSize(200, 50);
        }

        this.pane.getChildren().addAll(title, newGame, loadGame, instructions);
        this.outer.getChildren().add(this.pane);
        this.outer.setStyle("-fx-background-color: #dbecf0;");
    }

    public void addButton(String text) {
        this.pane.getChildren().add(new Button(text));
    }

    public void setButtonsDimensions() {
        for(int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).setMinSize(200, 50);
        }
    }

    public void resize(int w, int h) {
        this.owner.setWidth(w);
        this.owner.setHeight(h);
    }

    public void resize() { 

    }

    public void initialSize() {
        int minW = 0, minH = 0;
        for(int i = 0; i < this.buttons.size(); i++) {
            minH += 80;
        }
        minW += 210;
        minW += 40;
        minH += 36;
        minH += 60;
        this.owner.setMinHeight(minH);
        this.owner.setMinWidth(minW);

        int screenW = (int)Screen.getPrimary().getBounds().getWidth();
        int screenH = (int)Screen.getPrimary().getBounds().getHeight();
        double h, w;

        if(this.controller.mainStage.getHeight() > 400) {
            h = this.controller.mainStage.getHeight();
        }
        else {
            h = 400;
        }

        if(this.controller.mainStage.getWidth() > 500) {
            w = this.controller.mainStage.getWidth();
        }
        else {
            w = 500;
        }
        this.resize((int)w, (int)h);
    }

    public void createScene() {
        
        this.scene = new Scene(this.outer);
    }

    public Scene getScene() {
    
        return this.scene;
    }

    class NewGame implements EventHandler<ActionEvent> {

        public NewGame() {

        }

        public void handle(ActionEvent event) {
            MainScreen.this.controller.currentScreen = new NewGameScreen(MainScreen.this.controller.mainStage, MainScreen.this.controller);
            MainScreen.this.controller.showScreen(MainScreen.this.controller.currentScreen.getScene());
        }
    }

    class GameLoad implements EventHandler<ActionEvent> {

        public GameLoad() {

        }

        public void handle(ActionEvent event) {
            MainScreen.this.controller.currentScreen = new LoadGame(MainScreen.this.controller.mainStage, MainScreen.this.controller);
            MainScreen.this.controller.showScreen(MainScreen.this.controller.currentScreen.getScene());
        }
    }

    class InstructionsLoad implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            MainScreen.this.controller.currentScreen = new InfoScreen(MainScreen.this.controller.mainStage, MainScreen.this.controller);
            MainScreen.this.controller.showScreen(MainScreen.this.controller.currentScreen.getScene());
        }
    }
}