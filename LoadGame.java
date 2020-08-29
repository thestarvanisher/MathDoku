import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import javafx.stage.*;
import java.lang.Math;
import javafx.scene.control.*;

public class LoadGame extends GeneralScreen {

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
    TextArea text;
    Label chosen;
    ArrayList<String> stringPuzzle;

    public LoadGame(Stage owner, MathDoku controller) {
        this.owner = owner;
        this.controller = controller;
        this.sizeButtons = new ArrayList<Button>();
        this.difficultyButtons = new ArrayList<Button>();
        this.stringPuzzle = new ArrayList<String>();
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
        sizes.setAlignment(Pos.CENTER_LEFT);
        Label selectSize = new Label("Load game from file:");
        selectSize.setStyle("-fx-text-fill: #003B45; -fx-font-size: 20px;");

        Button back = new Button("Back");

        back.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 0px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 0px 0px; -fx-background-radius: 10px 10px 0px 0px;");
        this.toolbar.getChildren().add(back);
        back.setCursor(Cursor.HAND);
        back.addEventHandler(ActionEvent.ANY, new GoBack());

        Label chosen = new Label("No puzzle selected!");
        this.chosen = chosen;
        chosen.setStyle("-fx-text-fill: #003B45; -fx-font-size: 16px;");
        Button selectFile = new Button("Select file");
        selectFile.setCursor(Cursor.HAND);
        //this.sizeButtons.getChildren().addAll(chosen, selectFile);
        selectFile.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px; -fx-padding: 5px 10px 5px 10px;");
        selectFile.addEventHandler(ActionEvent.ANY, new SelectFile());

        sizes.getChildren().addAll(chosen, selectFile);
        sz.getChildren().addAll(selectSize, sizes);
        
        VBox df = new VBox();
        HBox diff = new HBox();
        diff.setSpacing(5);
        Label difficulty = new Label("Enter puzzle:");
        difficulty.setStyle("-fx-text-fill: #003B45; -fx-font-size: 20px;");
        Button start = new Button("Start");
        this.start = start;
        start.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
        TextArea text = new TextArea();
        text.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #dbecf0; -fx-text-fill: #003B45; -fx-font-size: 16px; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
        
        this.text = text;
        diff.getChildren().addAll(text);
        df.getChildren().addAll(difficulty, diff);
        VBox.setVgrow(text, Priority.ALWAYS);
        container2.getChildren().addAll(sz, df, start);

        start.setCursor(Cursor.HAND);
        start.addEventHandler(ActionEvent.ANY, new StartGame());
        this.container.getChildren().add(container2);
        this.container.setAlignment(Pos.CENTER);
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
            
            LoadGame.this.stringPuzzle.clear();
            String[] cnt = LoadGame.this.text.getText().split("\n");
            for(int i = 0; i < cnt.length; i++) {
                LoadGame.this.stringPuzzle.add(cnt[i]);
            }

            int maxM = 0;
            ArrayList<Integer> cells = new ArrayList<Integer>();
            ArrayList<ArrayList<Integer>> container = new ArrayList<ArrayList<Integer>>();
            ArrayList<Integer> targets = new ArrayList<Integer>();
            ArrayList<String> operations = new ArrayList<String>();
            try{
            for(int i = 0; i < LoadGame.this.stringPuzzle.size(); i++) {
                if(LoadGame.this.stringPuzzle.get(i).contains("+")) {
                    String[] s = LoadGame.this.stringPuzzle.get(i).split("\\+");
                    targets.add(Integer.valueOf(s[0]));
                    operations.add("+");
                    s[1] = s[1].trim();
                    String[] cages = s[1].split(",");
                    ArrayList<Integer> values = new ArrayList<Integer>();
                    for(int j = 0; j < cages.length; j++) {
                        maxM = Math.max(maxM, Integer.valueOf(cages[j]));
                        values.add(Integer.valueOf(cages[j]));
                        if(cells.contains(Integer.valueOf(cages[j]))) {
                            new DisplayWarning("A cell is contained in more than one cage!");
                            return;
                        }
                        else {
                            cells.add(Integer.valueOf(cages[j]));
                        }
                    }
                    container.add(values);
                }

                else if(LoadGame.this.stringPuzzle.get(i).contains("-")) {
                    String[] s = LoadGame.this.stringPuzzle.get(i).split("-");
                    targets.add(Integer.valueOf(s[0]));
                    operations.add("-");
                    s[1] = s[1].trim();
                    String[] cages = s[1].split(",");
                    ArrayList<Integer> values = new ArrayList<Integer>();
                    for(int j = 0; j < cages.length; j++) {
                        maxM = Math.max(maxM, Integer.valueOf(cages[j]));
                        values.add(Integer.valueOf(cages[j]));
                        if(cells.contains(Integer.valueOf(cages[j]))) {
                            new DisplayWarning("A cell is contained in more than one cage!");
                            return;
                        }
                        else {
                            cells.add(Integer.valueOf(cages[j]));
                        }
                    }
                    container.add(values);
                }

                else if(LoadGame.this.stringPuzzle.get(i).contains("x") || LoadGame.this.stringPuzzle.get(i).contains("*")) {
                    String[] s;
                    if(LoadGame.this.stringPuzzle.get(i).contains("x")) {
                        s = LoadGame.this.stringPuzzle.get(i).split("x");
                    }
                    else {
                        s = LoadGame.this.stringPuzzle.get(i).split("*");
                    }
                    targets.add(Integer.valueOf(s[0]));
                    operations.add("*");
                    s[1] = s[1].trim();
                    String[] cages = s[1].split(",");
                    ArrayList<Integer> values = new ArrayList<Integer>();
                    for(int j = 0; j < cages.length; j++) {
                        maxM = Math.max(maxM, Integer.valueOf(cages[j]));
                        values.add(Integer.valueOf(cages[j]));
                        if(cells.contains(Integer.valueOf(cages[j]))) {
                            new DisplayWarning("A cell is contained in more than one cage!");
                            return;
                        }
                        else {
                            cells.add(Integer.valueOf(cages[j]));
                        }
                    }
                    container.add(values);
                }

                else if(LoadGame.this.stringPuzzle.get(i).contains("÷") || LoadGame.this.stringPuzzle.get(i).contains("/")) {
                    String[] s;
                    if(LoadGame.this.stringPuzzle.get(i).contains("÷")) {
                        s = LoadGame.this.stringPuzzle.get(i).split("÷");
                    }
                    else {
                        s = LoadGame.this.stringPuzzle.get(i).split("/");
                    }
                    targets.add(Integer.valueOf(s[0]));
                    operations.add("/");
                    s[1] = s[1].trim();
                    String[] cages = s[1].split(",");
                    ArrayList<Integer> values = new ArrayList<Integer>();
                    for(int j = 0; j < cages.length; j++) {
                        maxM = Math.max(maxM, Integer.valueOf(cages[j]));
                        values.add(Integer.valueOf(cages[j]));
                        if(cells.contains(Integer.valueOf(cages[j]))) {
                            new DisplayWarning("A cell is contained in more than one cage!");
                            return;
                        }
                        else {
                            cells.add(Integer.valueOf(cages[j]));
                        }
                    }
                    container.add(values);
                }

                else {
                    String[] s = LoadGame.this.stringPuzzle.get(i).split(" ");
                    targets.add(Integer.valueOf(s[0]));
                    operations.add("");
                    s[1] = s[1].trim();
                    ArrayList<Integer> values = new ArrayList<Integer>();
                    maxM = Math.max(maxM, Integer.valueOf(s[1]));
                    values.add(Integer.valueOf(s[1]));
                    if(cells.contains(Integer.valueOf(s[1]))) {
                            new DisplayWarning("A cell is contained in more than one cage!");
                            return;
                        }
                    else {
                            cells.add(Integer.valueOf(s[1]));
                    }
                    container.add(values);
                }
            }

            for(int i = 0; i < container.size(); i++) {
                if(this.checkCageCondition(container.get(i), (int)Math.sqrt(maxM)) == false) {
                    new DisplayWarning("Cells within a cage are not adjacent!");
                    return;
                }
            }

            Field f = new Field((int)Math.sqrt(maxM));
            for(int i = 0; i < container.size(); i++) {

                Cage c = new Cage(targets.get(i), operations.get(i));
                for(int j = 0; j < container.get(i).size(); j++) {
                    int row, col;
                    if(container.get(i).get(j)%(int)Math.sqrt(maxM) == 0) {
                        row = container.get(i).get(j)/(int)Math.sqrt(maxM) - 1;
                        col = (int)Math.sqrt(maxM) - 1;
                    }
                    else {
                        row = container.get(i).get(j)/(int)Math.sqrt(maxM);
                        col = container.get(i).get(j)%(int)Math.sqrt(maxM) - 1;
                    }
                    c.addEntry(new Entry(row, col, 0));
                    
                }
                c.setDisplayable();
                f.addGroup(c);
            }
            LoadGame.this.controller.currentScreen = new GameScreen((int)Math.sqrt(maxM), LoadGame.this.controller.mainStage, 0, LoadGame.this.controller, f);
            LoadGame.this.controller.showScreen(LoadGame.this.controller.currentScreen.getScene());
        }
        catch (Exception e) {
            new DisplayWarning("The format of the puzzle is incorrect!");
        }
    }

        public boolean checkCageCondition(ArrayList<Integer>values, int fsize) {

            for(int i = 0; i < values.size(); i++) {
                if(values.size() > 1) {
                    if((values.contains(values.get(i) + 1) && values.get(i)%fsize != 0) || (values.contains(values.get(i) - 1) && values.get(i)%fsize != 1) || (values.contains(values.get(i) - fsize) && values.get(i) - fsize >= 0) || (values.contains(values.get(i) + fsize) && values.get(i) + fsize <= fsize*fsize)) {
                        continue;
                    }
                    else {
                        return false;
                    }
                }
            }
            return true;
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
            LoadGame.this.fSize = this.fsize;
            if(LoadGame.this.selectedSize != null) {
                LoadGame.this.selectedSize.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            }
            
            LoadGame.this.selectedSize = this.b;
            if(LoadGame.this.selectedDiff != null && LoadGame.this.selectedSize != null) {
                LoadGame.this.enableButton(LoadGame.this.start);
            }
            LoadGame.this.selectedSize.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #ffcc00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #ffcc00; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
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
            LoadGame.this.difficulty = this.diff;
            if(LoadGame.this.selectedDiff != null) {
                LoadGame.this.selectedDiff.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            }
            
            LoadGame.this.selectedDiff = this.b;
            if(LoadGame.this.selectedDiff != null && LoadGame.this.selectedSize != null) {
                LoadGame.this.enableButton(LoadGame.this.start);
            }
            LoadGame.this.selectedDiff.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #ffcc00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #ffcc00; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
        }
    }

    class GoBack implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            LoadGame.this.controller.currentScreen = new MainScreen(LoadGame.this.controller.mainStage, LoadGame.this.controller);
            LoadGame.this.controller.showScreen(LoadGame.this.controller.currentScreen.getScene());
        }
    }

    class SelectFile implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            
            FileChooser fChosen = new FileChooser();
            fChosen.setTitle("Select a puzzle file");
            ExtensionFilter txtFile = new ExtensionFilter("Text files (*.txt)", "*.txt");
            fChosen.getExtensionFilters().add(txtFile);
            File file = fChosen.showOpenDialog(LoadGame.this.controller.mainStage);
            if (file != null && file.exists() && file.canRead()) {
                try {

                    BufferedReader buffered = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                    String line;
                    LoadGame.this.text.clear();
                    while ((line = buffered.readLine()) != null) {
                        LoadGame.this.text.appendText(line + "\n");
                    }
                    LoadGame.this.chosen.setText(file.getName());
                    buffered.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DisplayWarning {

        String warning;
        public DisplayWarning(String warning) {
            this.warning = warning;
            this.handle();
        }

        public void handle() {
            Stage modal = new Stage();
            VBox u = new VBox();
            Label l = new Label(this.warning);
            HBox hor = new HBox();
            modal.setTitle("Warning");
            Button ok = new Button("OK");
            hor.getChildren().addAll(ok);
            u.getChildren().addAll(l, hor);
            Scene askScene = new Scene(u);
            modal.setScene(askScene);
            u.setSpacing(10);
            hor.setSpacing(10);
            hor.setAlignment(Pos.CENTER);
            u.setPadding(new Insets(10));
            u.setStyle("-fx-background-color: #dbecf0;");
            l.setStyle("-fx-text-fill: #003B45; -fx-font-size: 14px; -fx-text-alignment: center;");
            l.setWrapText(true);
            u.setMaxWidth(400);
            modal.setResizable(false);
            ok.setCursor(Cursor.HAND);
            ok.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            ok.addEventHandler(ActionEvent.ANY, new OkClicked(modal));
            modal.initOwner(LoadGame.this.owner);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();
        }
    }

    class OkClicked implements EventHandler<ActionEvent> {

        Stage parent;
        public OkClicked(Stage parent) {
            this.parent = parent;
        }

        public void handle(ActionEvent event) {
            this.parent.close();
        }
    }
}