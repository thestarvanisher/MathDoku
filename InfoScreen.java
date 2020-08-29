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
import javafx.scene.control.*;

public class InfoScreen extends GeneralScreen {

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

    public InfoScreen(Stage owner, MathDoku controller) {
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

        this.pane.setPadding(new Insets(10));
        VBox cont2 = new VBox();
        
        Label instructions = new Label("Instructions:");
        
        instructions.setStyle("-fx-text-fill: #003B45; -fx-font-size: 20px; -fx-underline: true;");
        instructions.setWrapText(true);
        Button back = new Button("Back");
        back.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 0px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 0px 0px; -fx-background-radius: 10px 10px 0px 0px;");
        this.toolbar.getChildren().add(back);
        back.setCursor(Cursor.HAND);
        back.addEventHandler(ActionEvent.ANY, new GoBack());

        Label instr = new Label("The application uses different kinds of buttons, although they seem to looks similar at first. There are:\n" + 
            "Clickable button - you click it and action happens\nToggle buttons - when you click them they become highlighted in yellow. Click them again to undo the action\n\n\n" + 
            "Click on 'New Random Game' and select the grid size and difficulty and hit start. A random game will be generated for you! If you need help " + 
            "click on the solve button. Once the game is solved, the 'Hint' button is enabled and you can briefly see the answer for that cell. You can always click on " + 
            "the 'Show Solution' button (which is enabled once the grid is solved) and get the soluton for the grid!\n\n\nClick on the 'Load Game from File' to load a grid " + 
            "from a file. Once the text file is loaded, you will see its content in the text area, where you can edit it or just ener a new puzzle! After hitting start you will " + 
            "be redirected to the game screen!\n\nWARNING! For grids that are loaded externally from a text file or entered in the text area it might take some time for the " + 
            "Solver to solve them, so be patient!\n\n");
        Label author = new Label("Created by Ivalin Chobanov. ORTools is a copyrighted product of Google. The license could be found in the folder!");
        author.setStyle("-fx-text-fill: #003B45; -fx-font-size: 16px; -fx-font-style: italic;");
        instr.setWrapText(true);
        instr.setStyle("-fx-text-fill: #003B45; -fx-font-size: 16px;");
        author.setWrapText(true);

        cont2.getChildren().addAll(instructions, instr, author);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(cont2);
        scroll.setStyle("-fx-background: #dbecf0; -fx-background-color:transparent;");
        scroll.setMaxWidth(800);
        scroll.setFitToWidth(true);
        HBox.setHgrow(scroll, Priority.ALWAYS);
        VBox.setVgrow(this.container, Priority.ALWAYS);
    
        this.container.getChildren().addAll(scroll);
        this.container.setAlignment(Pos.CENTER);
        this.pane.setAlignment(Pos.TOP_CENTER);
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

    class GoBack implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            InfoScreen.this.controller.currentScreen = new MainScreen(InfoScreen.this.controller.mainStage, InfoScreen.this.controller);
            InfoScreen.this.controller.showScreen(InfoScreen.this.controller.currentScreen.getScene());
        }
    }
}