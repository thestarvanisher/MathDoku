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

public class NewGameScreen extends GeneralScreen {

    Stage owner;
    Scene scene;
    BorderPane outer;
    MathDoku controller;
    HBox container;
    VBox pane;
    ArrayList<Button> sizeButtons;
    ArrayList<Button> difficultyButtons;
    Button selectedSize;
    Button selectedDiff;
    int fSize;
    int difficulty;
    HBox toolbar;
    Button start;

    public NewGameScreen(Stage owner, MathDoku controller) {
        this.owner = owner;
        this.controller = controller;
        this.sizeButtons = new ArrayList<Button>();
        this.difficultyButtons = new ArrayList<Button>();
        this.createStatic();
        this.layout();
        this.createScene();
    }

    public void createStatic() {
        this.pane = new VBox();
        this.container = new HBox();
        this.outer = new BorderPane();
        this.toolbar = new HBox();
    }

    public void layout() {

        this.toolbar.setPadding(new Insets(5, 5, 0, 5));
        this.toolbar.setSpacing(5);
        this.toolbar.setAlignment(Pos.BOTTOM_RIGHT);
        this.toolbar.setStyle("-fx-background-color: #dbecf0; -fx-border-style: solid; -fx-border-width: 4px; -fx-border-color: transparent transparent #003B45 transparent;");

        this.toolbar.setMaxHeight(40);

        VBox container2 = new VBox();
        this.pane.setPadding(new Insets(10));
        VBox sz = new VBox();
        HBox sizes = new HBox();
        sizes.setSpacing(5);
        Label selectSize = new Label("Grid size:");
        selectSize.setStyle("-fx-text-fill: #003B45; -fx-font-size: 20px;");
        Button s2 = new Button("2x2");
        Button s3 = new Button("3x3");
        Button s4 = new Button("4x4");
        Button s5 = new Button("5x5");
        Button s6 = new Button("6x6");
        Button s7 = new Button("7x7");
        Button s8 = new Button("8x8");
        Button back = new Button("Back");
        back.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 0px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 0px 0px; -fx-background-radius: 10px 10px 0px 0px;");
        this.toolbar.getChildren().add(back);
        back.setCursor(Cursor.HAND);
        back.addEventHandler(ActionEvent.ANY, new GoBack());

        s2.addEventHandler(ActionEvent.ANY, new SelectButton(s2, 2));
        s3.addEventHandler(ActionEvent.ANY, new SelectButton(s3, 3));
        s4.addEventHandler(ActionEvent.ANY, new SelectButton(s4, 4));
        s5.addEventHandler(ActionEvent.ANY, new SelectButton(s5, 5));
        s6.addEventHandler(ActionEvent.ANY, new SelectButton(s6, 6));
        s7.addEventHandler(ActionEvent.ANY, new SelectButton(s7, 7));
        s8.addEventHandler(ActionEvent.ANY, new SelectButton(s8, 8));

        this.sizeButtons.add(s2);
        this.sizeButtons.add(s3);
        this.sizeButtons.add(s4);
        this.sizeButtons.add(s5);
        this.sizeButtons.add(s6);
        this.sizeButtons.add(s7);
        this.sizeButtons.add(s8);

        sizes.getChildren().addAll(s2, s3, s4, s5, s6, s7, s8);
        sz.getChildren().addAll(selectSize, sizes);
        
        VBox df = new VBox();
        HBox diff = new HBox();
        diff.setSpacing(5);
        Label difficulty = new Label("Difficulty:");
        difficulty.setStyle("-fx-text-fill: #003B45; -fx-font-size: 20px;");
        Button d1 = new Button("Easy");
        Button d2 = new Button("Medium");
        Button d3 = new Button("Hard");
        Button start = new Button("Start");
        this.start = start;
        start.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
        this.difficultyButtons.add(d1);
        this.difficultyButtons.add(d2);
        this.difficultyButtons.add(d3);
        d1.addEventHandler(ActionEvent.ANY, new SelectButtonDiff(d1, 1));
        d2.addEventHandler(ActionEvent.ANY, new SelectButtonDiff(d2, 2));
        d3.addEventHandler(ActionEvent.ANY, new SelectButtonDiff(d3, 3));
        diff.getChildren().addAll(d1, d2, d3);
        df.getChildren().addAll(difficulty, diff);
        container2.getChildren().addAll(sz, df, start);
        this.disableButton(start);
        start.setCursor(Cursor.HAND);
        start.addEventHandler(ActionEvent.ANY, new StartGame());
        this.container.getChildren().add(container2);
        this.container.setAlignment(Pos.CENTER);
        this.designButtons();
        container2.setSpacing(10);
        this.pane.setAlignment(Pos.CENTER);
        this.pane.getChildren().add(this.container);

        this.pane.setStyle("-fx-background-color: #dbecf0;");
        this.outer.setTop(this.toolbar);
        this.outer.setCenter(this.pane);
    }

    public void resize() {

    }

    public void designButtons() {
        for(int i = 0; i < this.sizeButtons.size(); i++) {
            this.sizeButtons.get(i).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            this.sizeButtons.get(i).setCursor(Cursor.HAND);
        }

        for(int i = 0; i < this.difficultyButtons.size(); i++) {
            this.difficultyButtons.get(i).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            this.difficultyButtons.get(i).setCursor(Cursor.HAND);
        }
    }

    public void disableButton(Button b) {
        b.setDisable(true);
    }

    public void enableButton(Button b) {
        b.setDisable(false);
    }


    public void resize(int w, int h) {

    }

    public void initialSize() {
        this.owner.setMinHeight(340);
        this.owner.setMinWidth(500);
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

        this.owner.setHeight(h);
        this.owner.setWidth(w);
    }

    public void createScene() {
        this.scene = new Scene(this.outer);
    }

    public Scene getScene() {
        return this.scene;
    }

    class StartGame implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            RandomField rf = new RandomField(NewGameScreen.this.fSize, NewGameScreen.this.difficulty);
            rf.createRandomField();
            NewGameScreen.this.controller.currentScreen = new GameScreen(NewGameScreen.this.fSize, NewGameScreen.this.controller.mainStage, 0, NewGameScreen.this.controller, rf.getRandomField());
            NewGameScreen.this.controller.showScreen(NewGameScreen.this.controller.currentScreen.getScene());
            
        }
    }

    class SelectButton implements EventHandler<ActionEvent> {

        Button b;
        int fsize;
        public SelectButton(Button b, int fsize) {
            this.b = b;
            this.fsize = fsize;
        }

        public void handle(ActionEvent event) {
            NewGameScreen.this.fSize = this.fsize;
            if(NewGameScreen.this.selectedSize != null) {
                NewGameScreen.this.selectedSize.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            }
            
            NewGameScreen.this.selectedSize = this.b;
            if(NewGameScreen.this.selectedDiff != null && NewGameScreen.this.selectedSize != null) {
                NewGameScreen.this.enableButton(NewGameScreen.this.start);
            }
            NewGameScreen.this.selectedSize.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #ffcc00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #ffcc00; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;"); 
        }
    }

    class SelectButtonDiff implements EventHandler<ActionEvent> {

        Button b;
        int diff;
        public SelectButtonDiff(Button b, int diff) {
            this.b = b;
            this.diff = diff;
        }

        public void handle(ActionEvent event) {
            NewGameScreen.this.difficulty = this.diff;
            if(NewGameScreen.this.selectedDiff != null) {
                NewGameScreen.this.selectedDiff.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            }
            
            NewGameScreen.this.selectedDiff = this.b;
            if(NewGameScreen.this.selectedDiff != null && NewGameScreen.this.selectedSize != null) {
                NewGameScreen.this.enableButton(NewGameScreen.this.start);
            }
            NewGameScreen.this.selectedDiff.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #ffcc00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #ffcc00; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
        }
    }

    class GoBack implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            NewGameScreen.this.controller.currentScreen = new MainScreen(NewGameScreen.this.controller.mainStage, NewGameScreen.this.controller);
            NewGameScreen.this.controller.showScreen(NewGameScreen.this.controller.currentScreen.getScene());
        }
    }
}