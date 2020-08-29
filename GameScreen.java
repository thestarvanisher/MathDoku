import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
import java.util.*;
import javafx.stage.*;
import java.lang.Math;
import javafx.animation.PauseTransition;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.application.*;
import javafx.animation.*;

public class GameScreen extends GeneralScreen {

    MDSolver solver;
    Stage owner;
    Field field;
    Scene scene;
    HBox pane;
    HBox paneb;
    BorderPane outer;
    HBox inner;
    int wHeight;
    int wWidth;
    ArrayList<Button> buttons = new ArrayList<Button>();
    ArrayList<Button> buttons2 = new ArrayList<Button>();
    int fieldSize;
    Stage stage;
    int fSize;
    GridPane grid;
    Entry selected;
    Pane selectedPane;
    Stack<StackElement> undoStack;
    Stack<StackElement> redoStack;
    MathDoku controller;
    boolean toggleMistakes;
    boolean isSolved;
    int[][] solution;
    Thread timeConsumer;
    int scopedI;
    int scopedJ;
    boolean showedSolution;

    public GameScreen(int fieldSize, Stage owner, int fSize, MathDoku controller, Field field) {
        this.owner = owner;
        this.fieldSize = fieldSize;
        this.fSize = fSize;
        this.field = field;
        this.controller = controller;
        this.isSolved = false;
        this.showedSolution = false;
        this.toggleMistakes = false;
        this.undoStack = new Stack<StackElement>();
        this.redoStack = new Stack<StackElement>();
        this.createStatic();
        this.setDimensions(400, 400);
        this.createScene();
        this.layout();
    }

    public void createStatic() {
        this.pane = new HBox();
        this.paneb = new HBox(); 
        this.outer = new BorderPane();
        this.pane.setPadding(new Insets(5, 5, 0, 5));
        this.pane.setSpacing(5);
        this.paneb.setPadding(new Insets(0, 5, 5, 5));
        this.paneb.setSpacing(5);
        this.pane.setAlignment(Pos.BOTTOM_RIGHT);
        this.paneb.setAlignment(Pos.TOP_CENTER);
        
    }

    public void layout() {
        Button undo = new Button("Undo");
        Button redo = new Button("Redo");
        Button back = new Button("Menu");
        Button solveb = new Button("Solve");
        Button hint = new Button("Hint");
        Button showmistakes = new Button("Show Mistakes");
        Button clear = new Button("Clear");
        Button fSizeP = new Button("Font Size +");
        Button fSizeM = new Button("Font Size -");
        showmistakes.addEventHandler(ActionEvent.ANY, new ToggleMistakes());
        
        back.addEventHandler(ActionEvent.ANY, new GoBack());

        fSizeP.addEventHandler(ActionEvent.ANY, new IncreaseFont());
        fSizeM.addEventHandler(ActionEvent.ANY, new DecreaseFont());
        this.disableButton(fSizeM);
        undo.setCursor(Cursor.HAND);
        this.buttons.add(undo);
        this.buttons.add(redo);
        this.buttons.add(clear);
        this.buttons.add(fSizeM);
        this.buttons.add(fSizeP);
        this.buttons.add(solveb);
        this.buttons.add(hint);
        this.buttons.add(showmistakes);
        this.buttons.add(back);
        this.pane.getChildren().addAll(undo, redo, clear,fSizeM, fSizeP, solveb, hint, showmistakes, back);
        this.outer.setTop(this.pane);
        this.setButtonsDimensions();


        for(int i = 0; i < this.fieldSize; i++) {
            Button b = new Button("" + (i + 1));
            this.buttons2.add(b);
            this.paneb.getChildren().add(b);
        }
        Button del = new Button("Del");
        this.buttons2.add(del);
        this.paneb.getChildren().add(del);

        this.outer.setBottom(this.paneb);
        this.setButtonsDimensions2();
        undo.addEventHandler(ActionEvent.ANY, new UndoHandler());
        redo.addEventHandler(ActionEvent.ANY, new RedoHandler());
        this.disableButton(undo);
        this.disableButton(redo);
        this.disableButton(hint);
        
        solveb.addEventHandler(ActionEvent.ANY, new Solve());
        hint.addEventHandler(ActionEvent.ANY, new ShowHint());

        this.grid = new GridPane();
        clear.addEventHandler(ActionEvent.ANY, new AskClear());
        double maxN = 0;

        for(int i = 0; i < this.buttons2.size(); i++) {
            this.buttons2.get(i).addEventHandler(ActionEvent.ANY, new HandleOnScreenKeys(this.buttons2.get(i)));
        }


        for(int i = 0; i < this.fieldSize; i ++) {
            for(int j = 0; j < this.fieldSize; j++) {
                Pane p = new Pane();
                Label l1;
                if(this.field.getField()[i][j].isDisplayable() == true) {
                    if(this.field.getField()[i][j].getGroup().getOperation().equals("*")) {
                        l1 = new Label("" + this.field.getField()[i][j].getGroup().getTarget() + "x");
                    }
                    else if(this.field.getField()[i][j].getGroup().getOperation().equals("/")) {
                        l1 = new Label("" + this.field.getField()[i][j].getGroup().getTarget() + "÷");
                    }
                    else {
                        l1 = new Label("" + this.field.getField()[i][j].getGroup().getTarget() + this.field.getField()[i][j].getGroup().getOperation());
                    }
                }
                else {
                    l1 = new Label(" ");
                }

                Label l2 = new Label("");

                VBox v = new VBox();
                HBox h1 = new HBox();
                HBox h2 = new HBox();
                
                h1.getChildren().add(l1);
                h2.getChildren().add(l2);
                h2.setAlignment(Pos.CENTER);
                v.getChildren().addAll(h1, h2);
                p.getChildren().add(v);
                h2.setStyle("-fx-font-weight: bold;");
                String styleText1 = "-fx-text-fill: #003B45; -fx-font-size:" + (this.fSize*6 + 14) + "px;";
                l1.setStyle(styleText1);
                String styleText2 = "-fx-text-fill: #003B45; -fx-font-size:" + (this.fSize*9 + 28) + "px; -fx-padding: -20 0 0 0;";
                l2.setStyle(styleText2);

                String t, d, l, r;
                String st, sd, sl, sr;
                if(i > 0 && this.field.getField()[i][j].getGroup() == this.field.getField()[i - 1][j].getGroup()) {
                    t = "#66A4AC";
                    st = "dashed";
                }
                else {
                    t = "#003B45";
                    st = "solid";
                }

                if(j > 0 && this.field.getField()[i][j].getGroup() == this.field.getField()[i][j - 1].getGroup()) {
                    l = "#66A4AC";
                    sl = "dashed";
                }
                else {
                    l = "#003B45";
                    sl = "solid";
                }

                if(j < this.fieldSize - 1 && this.field.getField()[i][j].getGroup() == this.field.getField()[i][j + 1].getGroup()) {
                    r = "#66A4AC";
                    sr = "dashed";
                }
                else {
                    r = "#003B45";
                    sr = "solid";
                }

                if(i < this.fieldSize - 1 && this.field.getField()[i][j].getGroup() == this.field.getField()[i + 1][j].getGroup()) {
                    d = "#66A4AC";
                    sd = "dashed";
                }
                else {
                    d = "#003B45";
                    sd = "solid";
                }

                String styleTxt = "-fx-border-style: " + st + " " + sr + " " + sd + " " + sl + "; -fx-border-width: 2px 2px 2px 2px; -fx-border-color: " + t + " " + r + " " + d + " " + l + ";";

                p.setStyle((String)styleTxt);
                p.addEventHandler(MouseEvent.MOUSE_CLICKED, new PaneClickHandler(p, this.selected, this.field, this.selectedPane));
                this.grid.add(p, j, i); 
                maxN = Math.max(maxN, Math.max(Math.max(l1.getWidth(), l2.getWidth()), l1.getHeight() + l2.getHeight()));
            }
        }
        this.grid.setStyle("-fx-border-style: solid; -fx-border-width: 2px; -fx-border-color: #003B45;");

        BorderPane.setAlignment(grid, Pos.CENTER);
        VBox vCenter = new VBox();
        HBox hCenter = new HBox();
        hCenter.setAlignment(Pos.CENTER);
        vCenter.setAlignment(Pos.CENTER);
        
        vCenter.getChildren().add(grid);

        hCenter.getChildren().add(vCenter);
        this.outer.setCenter(hCenter); 
    }

    public void setDimensions(int h, int w) {
        this.wHeight = h;
        this.wWidth = w;
    }

    public void addButton(String text) {
        this.pane.getChildren().add(new Button(text));
    }

    public void setButtonsDimensions() {
        for(int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).setCursor(Cursor.HAND);
            this.buttons.get(i).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 0px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 0px 0px; -fx-background-radius: 10px 10px 0px 0px; -fx-padding: 5px 10px 5px 10px;");
        }
    }

    public void setButtonsDimensions2() {
        for(int i = 0; i < this.buttons2.size(); i++) {
            this.buttons2.get(i).setCursor(Cursor.HAND);
            this.buttons2.get(i).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 0px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 0px 0px 5px 5px; -fx-background-radius: 0px 0px 10px 10px; -fx-padding: 5px 10px 5px 10px;");
        }
    }


    public void resize(int w, int h) {
        this.owner.setWidth(w);
        this.owner.setHeight(h);
        
    }
    public void resize() {
        // Does nothing
    }

    public void resizeWithFont(int ff) {

        for(int i = 0; i < this.grid.getChildren().size(); i++) {
            Pane d = (Pane)(this.grid.getChildren().get(i));
            VBox tv = (VBox)(d.getChildren().get(0));
            HBox h1 = (HBox)(tv.getChildren().get(0));
            HBox h2 = (HBox)(tv.getChildren().get(1));
            
            Label l1 = (Label)(h1.getChildren().get(0));
            Label l2 = (Label)(h2.getChildren().get(0)); 
            String styleText1 = "-fx-text-fill: #003B45; -fx-font-size:" + (ff*6 + 14) + "px;";
            l1.setStyle(styleText1);
            String styleText2 = "-fx-text-fill: #003B45; -fx-font-size:" + (ff*9 + 28) + "px; -fx-padding: -20 0 0 0;";
            l2.setStyle(styleText2);
        }        
    }

    public void initialSize(int fontSize) {
        
    }

    public void initialSize() {
        double maxN = 0;
        double hh = 0;
        for(int i = 0; i < this.grid.getChildren().size(); i++) {
            Pane d = (Pane)(this.grid.getChildren().get(i));
            VBox tv = (VBox)(d.getChildren().get(0));
            tv.setPadding(new Insets(4, 7, 4, 7));
            HBox h1 = (HBox)(tv.getChildren().get(0));
            HBox h2 = (HBox)(tv.getChildren().get(1));
            
            Label l1 = (Label)(h1.getChildren().get(0));
            Label l2 = (Label)(h2.getChildren().get(0));
            
            Text ttt1 = new Text(l1.getText());
            Font fff1 = new Font((this.fSize*6 + 14));
            ttt1.setFont(fff1);
            Text ttt2 = new Text(l1.getText());
            Font fff2 = new Font((this.fSize*9 + 28));
            ttt2.setFont(fff2);
            hh = Math.max(hh, ttt1.getBoundsInLocal().getHeight());
            maxN = Math.max(maxN, Math.max(Math.max(ttt1.getBoundsInLocal().getWidth(), l2.getHeight()), ttt1.getBoundsInLocal().getHeight() + ttt2.getBoundsInLocal().getHeight()));
                
        }
        maxN += 12;
        for(int i = 0; i < this.grid.getChildren().size(); i++) {
            Pane d = (Pane)(this.grid.getChildren().get(i));
            VBox tv = (VBox)(d.getChildren().get(0));
            HBox h2 = (HBox)(tv.getChildren().get(1));
            tv.setMinWidth(maxN);
            d.setMinWidth(maxN);
            d.setMinHeight(maxN);
            d.setMaxHeight(maxN);
            d.setMaxWidth(maxN);
            h2.setMinHeight(maxN - hh);
            h2.setMaxHeight(maxN - hh);
        }

        Label l = new Label("SSSSSSS");
        Text ttt2 = new Text(l.getText());
        ttt2.setFont(Font.font("System", FontWeight.BOLD, 14));

        double iH, iW;
        if(this.owner.getHeight() > (int)(maxN*this.fieldSize + (this.fieldSize-1)*4 + 40 + this.pane.getHeight())) {
            iH = this.owner.getHeight();
        }
        else {
            iH = (int)(maxN*this.fieldSize + (this.fieldSize-1)*4 + 40 + this.pane.getHeight());
        }

        if(this.owner.getWidth() > 820) {
            iW = this.owner.getWidth();
        }
        else {
            iW = 820;
        }

        this.resize((int)iW, (int)iH);
        this.owner.setMinHeight((maxN*this.fieldSize + (this.fieldSize+1)*4 + 60 + (ttt2.getBoundsInLocal().getHeight() + 5 + 3 + 4 + 10)*2));
        this.owner.setMinWidth(Math.max((maxN*this.fieldSize + (this.fieldSize-1)*4 + 40), 820));
    }

    public void disableButton(Button b) {
        b.setDisable(true);
    }

    public void enableButton(Button b) {
        b.setDisable(false);
    }

    public void clearBoard() {
        for(int i =0; i < this.grid.getChildren().size(); i++) {
            Pane pp = (Pane)this.grid.getChildren().get(i);
            Label l = (Label)((((HBox)((VBox)pp.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
            l.setText("");
        }
    }



    public void showMistakes(ArrayList<Object> keeper) {
        ArrayList<Cage> problemCages = (ArrayList<Cage>)keeper.get(0);
        ArrayList<Integer> problemRows = (ArrayList<Integer>)keeper.get(1);
        ArrayList<Integer> problemColumns = (ArrayList<Integer>)keeper.get(2);

        for(int i = 0; i < this.grid.getChildren().size(); i++) {
            Pane d = (Pane)this.grid.getChildren().get(i);
            d.setStyle(d.getStyle() + " -fx-background-color: transparent;");
        }

        boolean flag = false;
        for(int i = 0; i < problemCages.size(); i++) {
            for(int j = 0; j < problemCages.get(i).getEntries().size(); j++) {
                if(problemCages.get(i).getEntries().get(j).getValue() == 0) {
                    
                    flag = true;
                    break;
                }
            }   
            if(flag == false) {
                for(int j = 0; j < problemCages.get(i).getEntries().size(); j++) {
                    Pane d = (Pane)this.grid.getChildren().get(problemCages.get(i).getEntries().get(j).getX()*this.fieldSize + problemCages.get(i).getEntries().get(j).getY());
                    d.setStyle(d.getStyle() + " -fx-background-color: #ff8080;");
                } 
            }
            flag = false;
        }

        for(int i = 0; i < problemRows.size(); i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                Pane d = (Pane)this.grid.getChildren().get(problemRows.get(i)*this.fieldSize + j);
                d.setStyle(d.getStyle() + " -fx-background-color: #ff8080;");
            }
        }

        for(int i = 0; i < problemColumns.size(); i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                Pane d = (Pane)this.grid.getChildren().get(j*this.fieldSize + problemColumns.get(i));
                d.setStyle(d.getStyle() + " -fx-background-color: #ff8080;");
            }
        }

        if(this.selectedPane != null) {
            this.selectedPane.setStyle(this.selectedPane.getStyle() + "-fx-background-color: lightgray;");
        }
    }

    public void winAnimation() {

        this.scopedI = 0;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), event -> {
                this.grid.setStyle("-fx-border-color: transparent;");
                Pane d = (Pane)(this.grid.getChildren().get(this.scopedI));
                VBox tv = (VBox)(d.getChildren().get(0));
                tv.setPadding(new Insets(4, 7, 4, 7));
                HBox h1 = (HBox)(tv.getChildren().get(0));
                HBox h2 = (HBox)(tv.getChildren().get(1));
                Label l1 = (Label)(h1.getChildren().get(0));
                Label l2 = (Label)(h2.getChildren().get(0));
                d.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
                l1.setVisible(false);
                l2.setVisible(false);
                this.scopedI++;
                if(this.scopedI == this.fieldSize*this.fieldSize){
                    Label win = new Label("You Win!");
                    Label info = new Label("Click the Menu button to go to main menu");
                    win.setStyle("-fx-text-fill: #003B45; -fx-font-size: 36px;");
                    info.setStyle("-fx-text-fill: #003B45; -fx-font-size: 16px;");
                    Button show_grid = new Button("Show Grid");
                    show_grid.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
                    show_grid.addEventHandler(ActionEvent.ANY, new ShowGrid());
                    show_grid.setCursor(Cursor.HAND);
                    HBox c1 = new HBox();
                    VBox c2 = new VBox();
                    c1.setAlignment(Pos.CENTER);
                    c2.setAlignment(Pos.CENTER);
                    c2.getChildren().addAll(win, info, show_grid);
                    c1.getChildren().add(c2);
                    this.outer.setCenter(c1);
                    for(int i = 0; i < this.buttons.size() - 1; i++) {
                        this.disableButton(this.buttons.get(i));
                    }
                    for(int i = 0; i < this.buttons2.size(); i++) {
                        this.disableButton(this.buttons2.get(i));
                    }
                }
            })
        );
        timeline.setCycleCount(this.fieldSize*this.fieldSize);
        timeline.play();
    }


    public void createScene() {

        this.pane.setStyle("-fx-background-color: #dbecf0; -fx-border-style: solid; -fx-border-width: 4px; -fx-border-color: transparent transparent #003B45 transparent;");
        this.pane.setMaxHeight(40);
        this.paneb.setStyle("-fx-background-color: #dbecf0; -fx-border-style: solid; -fx-border-width: 4px; -fx-border-color: #003B45 transparent transparent transparent;");
        this.paneb.setMaxHeight(40);
        this.outer.setStyle("-fx-background-color: #dbecf0;");
        this.scene = new Scene(this.outer, this.wWidth, this.wHeight);
        this.scene.addEventHandler(KeyEvent.KEY_PRESSED, new KeyPressedHandler());
    }

    public Scene getScene() {
        return this.scene;
    }

    class PaneClickHandler implements EventHandler<MouseEvent>{

        Pane pp;
        Entry selected;
        Field field;
        Pane selectedPane;
        public PaneClickHandler(Pane pp, Entry selected, Field field, Pane selectedPane) {
            this.pp = pp;
            this.selected = selected;
            this.field = field;
            this.selectedPane = selectedPane;
        }

        public void handle(MouseEvent event) {
            
            Label l = (Label)((((HBox)((VBox)pp.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

            GameScreen.this.selected = this.field.getField()[GridPane.getRowIndex(this.pp)][GridPane.getColumnIndex(this.pp)];
            
            if(GameScreen.this.selectedPane != null) {
                GameScreen.this.selectedPane.setStyle(GameScreen.this.selectedPane.getStyle() + "-fx-background-color: transparent;");
            }
            
            this.pp.setStyle(this.pp.getStyle() + "-fx-background-color: lightgray;");
            GameScreen.this.selectedPane = this.pp;
            if(GameScreen.this.toggleMistakes == true) {
                GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
            }
        }
    }

    class ClearButtonHandler implements EventHandler<MouseEvent> {

        GridPane gridd;

        public ClearButtonHandler(GridPane gridd) {
            this.gridd = gridd;
        }

        public void handle(MouseEvent event) {
            for(int i =0; i < this.gridd.getChildren().size(); i++) {
                Pane pp = (Pane)this.gridd.getChildren().get(i);
                Label l = (Label)((((HBox)((VBox)pp.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
                l.setText("");
            }   
        }
    }

    class KeyPressedHandler implements EventHandler<KeyEvent> {

        public KeyPressedHandler() {

        }

        public void handle(KeyEvent event) {
            if(GameScreen.this.selectedPane == null || GameScreen.this.showedSolution == true) {
                return;
            }
            if(event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 0) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 0, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(0);
                    l.setText("");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    }
                }
                
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }
            
            else if(event.getCode() == KeyCode.DIGIT1 || event.getCode() == KeyCode.NUMPAD1 && GameScreen.this.fieldSize >= 1) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
                
                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 1) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 1, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(1);
                    l.setText("1");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT2 || event.getCode() == KeyCode.NUMPAD2 && GameScreen.this.fieldSize >= 2) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
               
                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() !=2) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 2, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(2);
                    l.setText("2");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    }
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT3 || event.getCode() == KeyCode.NUMPAD3 && GameScreen.this.fieldSize >= 3) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 3) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 3, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(3);
                    l.setText("3");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT4 || event.getCode() == KeyCode.NUMPAD4 && GameScreen.this.fieldSize >= 4) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 4) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 4, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(4);
                    l.setText("4");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    }
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.NUMPAD5 && GameScreen.this.fieldSize >= 5) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 5) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 5, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(5);
                    l.setText("5");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    }
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT6 || event.getCode() == KeyCode.NUMPAD6 && GameScreen.this.fieldSize >= 6) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 6) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 6, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(6);
                    l.setText("6");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.NUMPAD7 && GameScreen.this.fieldSize >= 7) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 7) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 7, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(7);
                    l.setText("7");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            else if(event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.NUMPAD8 && GameScreen.this.fieldSize == 8) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 8) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 8, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(8);
                    l.setText("8");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
                GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
                GameScreen.this.redoStack.clear();
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }

            if(GameScreen.this.field.checkSolved() == true) {
                GameScreen.this.isSolved = true;
                GameScreen.this.winAnimation();
            }
        }
    }

    class HandleOnScreenKeys implements EventHandler<ActionEvent> {

        Button b;

        public HandleOnScreenKeys(Button b) {
            this.b = b;
        }

        public void handle(ActionEvent event) {
            if(GameScreen.this.selectedPane == null || GameScreen.this.showedSolution == true) {
                return;
            }

            if(this.b.getText().equals("1")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 1) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 1, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(1);
                    l.setText("1");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("2")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() !=2) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 2, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(2);
                    l.setText("2");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("3")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 3) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 3, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(3);
                    l.setText("3");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("4")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 4) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 4, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(4);
                    l.setText("4");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }
            
            else if(this.b.getText().equals("5")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 5) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 5, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(5);
                    l.setText("5");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("6")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 6) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 6, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(6);
                    l.setText("6");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("7")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 7) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 7, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(7);
                    l.setText("7");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("8")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));

                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 8) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 8, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(8);
                    l.setText("8");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    } 
                }
            }

            else if(this.b.getText().equals("Del")) {
                Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
                if(GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue() != 0) {
                    GameScreen.this.undoStack.push(new StackElement(GridPane.getRowIndex(GameScreen.this.selectedPane), GridPane.getColumnIndex(GameScreen.this.selectedPane), 0, GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].getValue(), GameScreen.this.selectedPane));
                    GameScreen.this.field.getField()[GridPane.getRowIndex(GameScreen.this.selectedPane)][GridPane.getColumnIndex(GameScreen.this.selectedPane)].updateValue(0);
                    l.setText("");
                    if(GameScreen.this.toggleMistakes == true) {
                        GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
                    }
                } 
            }

            GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
            GameScreen.this.redoStack.clear();
            GameScreen.this.disableButton(GameScreen.this.buttons.get(1));

            if(GameScreen.this.field.checkSolved() == true) {
                GameScreen.this.isSolved = true;
                GameScreen.this.winAnimation();
            }
        }
    }


    class UndoHandler implements EventHandler<ActionEvent> {

        public UndoHandler() {
            
        }

        public void handle(ActionEvent event) {
            
            StackElement t = GameScreen.this.undoStack.pop();
            Label l = (Label)((((HBox)((VBox)t.getPane().getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
            
            GameScreen.this.redoStack.push(t);
            GameScreen.this.field.getField()[t.getX()][t.getY()].updateValue(t.getPValue());
            if(t.getPValue() == 0) {
                l.setText("");
            }
            else {
                l.setText("" + t.getPValue());
            }
            if(GameScreen.this.undoStack.size() == 0) {
                GameScreen.this.disableButton(GameScreen.this.buttons.get(0));
            }
            GameScreen.this.enableButton(GameScreen.this.buttons.get(1));
            if(GameScreen.this.toggleMistakes == true) {
                GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
            }
        }
    }

    class RedoHandler implements EventHandler<ActionEvent> {

        public RedoHandler() {

        }

        public void handle(ActionEvent event) {
            StackElement t = GameScreen.this.redoStack.pop();
            Label l = (Label)((((HBox)((VBox)t.getPane().getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
            GameScreen.this.undoStack.push(t);
            GameScreen.this.field.getField()[t.getX()][t.getY()].updateValue(t.getValue());
            if(t.getValue() == 0) {
                l.setText("");
            }
            else {
                l.setText("" + t.getValue());
            }
            if(GameScreen.this.redoStack.size() == 0) {
                GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            }
            GameScreen.this.enableButton(GameScreen.this.buttons.get(0));
            if(GameScreen.this.toggleMistakes == true) {
                GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
            }
        }
    }

    class AskClear implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            Stage modal = new Stage();
            VBox u = new VBox();
            Label l = new Label("Are you sure you want to clear the board?\nThis action cannot be undone!");
            HBox hor = new HBox();
            modal.setTitle("Clear the board?");
            Button yes = new Button("Yes");
            Button cancel = new Button("Cancel");
            hor.getChildren().addAll(yes, cancel);
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
            yes.setCursor(Cursor.HAND);
            cancel.setCursor(Cursor.HAND);
            yes.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            cancel.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            yes.addEventHandler(ActionEvent.ANY, new YesClicked(modal));
            cancel.addEventHandler(ActionEvent.ANY, new CancelClicked(modal));
            modal.initOwner(GameScreen.this.owner);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();  
        }

    }

    class YesClicked implements EventHandler<ActionEvent> {

        Stage parent;
        public YesClicked(Stage parent) {
            this.parent = parent;
        }

        public void handle(ActionEvent event) {
            GameScreen.this.clearBoard();
            GameScreen.this.undoStack.clear();
            GameScreen.this.redoStack.clear();
            GameScreen.this.disableButton(GameScreen.this.buttons.get(0));
            GameScreen.this.disableButton(GameScreen.this.buttons.get(1));
            this.parent.close();
            for(int i = 0; i < GameScreen.this.fieldSize; i++) {
                for(int j = 0; j < GameScreen.this.fieldSize; j++) {
                    GameScreen.this.field.getField()[i][j].updateValue(0);
                }
            }
            if(GameScreen.this.toggleMistakes == true) { 
                GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
            }
        }
    }

    class CancelClicked implements EventHandler<ActionEvent> {

        Stage parent;
        public CancelClicked(Stage parent) {
            this.parent = parent;
        }

        public void handle(ActionEvent event) {
            this.parent.close();
        }
    }

    class IncreaseFont implements EventHandler<ActionEvent> {

        public IncreaseFont() {

        }

        public void handle(ActionEvent event) {
            GameScreen.this.fSize++;
            GameScreen.this.enableButton(GameScreen.this.buttons.get(3));
            if(GameScreen.this.fSize == 2) {
                GameScreen.this.disableButton(GameScreen.this.buttons.get(4));
            }
            GameScreen.this.resizeWithFont(GameScreen.this.fSize);
            GameScreen.this.owner.show();
            GameScreen.this.initialSize();
        }
    }

    class DecreaseFont implements EventHandler<ActionEvent> {

        public DecreaseFont() {

        }

        public void handle(ActionEvent event) {
            GameScreen.this.fSize--;
            GameScreen.this.enableButton(GameScreen.this.buttons.get(4));
            if(GameScreen.this.fSize == 0) {
                GameScreen.this.disableButton(GameScreen.this.buttons.get(3));
            }
            GameScreen.this.resizeWithFont(GameScreen.this.fSize);
            GameScreen.this.owner.show();
            GameScreen.this.initialSize();
        }
    }

    class GoBack implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            Stage modal = new Stage();
            VBox u = new VBox();
            Label l = new Label("Are you sure you want to go back to main menu?\nYour progress will be lost!");
            HBox hor = new HBox();
            modal.setTitle("Go to main menu?");
            Button yes = new Button("Yes");
            Button cancel = new Button("Cancel");
            hor.getChildren().addAll(yes, cancel);
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
            yes.setCursor(Cursor.HAND);
            cancel.setCursor(Cursor.HAND);
            yes.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            cancel.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
            yes.addEventHandler(ActionEvent.ANY, new MenuClicked(modal));
            cancel.addEventHandler(ActionEvent.ANY, new CancelClicked(modal));
            modal.initOwner(GameScreen.this.owner);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait(); 
        }
    }

    class ToggleMistakes implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            if(GameScreen.this.toggleMistakes == false) {
                GameScreen.this.buttons.get(7).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #ffcc00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 0px 3px; -fx-border-color: #ffcc00; -fx-background-insets: 0; -fx-border-radius: 5px 5px 0px 0px; -fx-background-radius: 10px 10px 0px 0px; -fx-padding: 5px 10px 5px 10px;");
                GameScreen.this.toggleMistakes = true;
                GameScreen.this.showMistakes(GameScreen.this.field.returnMistakes());
            }
            else {
                GameScreen.this.buttons.get(7).setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 0px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 0px 0px; -fx-background-radius: 10px 10px 0px 0px; -fx-padding: 5px 10px 5px 10px;");
                GameScreen.this.toggleMistakes = false;
                for(int i = 0; i < GameScreen.this.grid.getChildren().size(); i++) {
                    Pane d = (Pane)GameScreen.this.grid.getChildren().get(i);
                    d.setStyle(d.getStyle() + " -fx-background-color: transparent;");
                }
            }
        }
    }

    class Solve implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            if(GameScreen.this.isSolved == false) {
                Stage modal = new Stage();
                VBox u = new VBox();
                u.setAlignment(Pos.CENTER);
                Label l = new Label("Click Solve to solve the puzzle.\nBe patient, it may take time to solve it!\nHint will be enabled when the puzzle is solved!\nAfter solving, you can click Show solution!");
                HBox hor = new HBox();
                modal.setTitle("Solve puzzle?");
                Button solveb = new Button("Solve");
                Button cancel = new Button("Cancel");
                hor.getChildren().addAll(solveb, cancel);
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
                solveb.setCursor(Cursor.HAND);
                cancel.setCursor(Cursor.HAND);
                solveb.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
                cancel.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
                solveb.addEventHandler(ActionEvent.ANY, new SolveClicked(modal, l, solveb, cancel, hor));
                cancel.addEventHandler(ActionEvent.ANY, new CancelClicked(modal));
                modal.initOwner(GameScreen.this.owner);
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.showAndWait();
            }
            else {
                Stage modal = new Stage();
                VBox u = new VBox();
                u.setAlignment(Pos.CENTER);
                Label l = new Label("Are you sure you want to see the solution?\nThis will finish the game and you won't be\nallowed to continue it!");
                HBox hor = new HBox();
                modal.setTitle("Show solution?");
                Button solveb = new Button("OK");
                Button cancel = new Button("Cancel");
                hor.getChildren().addAll(solveb, cancel);
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
                solveb.setCursor(Cursor.HAND);
                cancel.setCursor(Cursor.HAND);
                solveb.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
                cancel.setStyle("-fx-background-color: #66A4AC; -fx-text-fill: #003B45; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; -fx-border-color: #003B45; -fx-background-insets: 0; -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 10px 10px 10px 10px;");
                solveb.addEventHandler(ActionEvent.ANY, new ShowSolution(modal));
                cancel.addEventHandler(ActionEvent.ANY, new CancelClicked(modal));
                modal.initOwner(GameScreen.this.owner);
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.showAndWait();
            }
        }
    }

    class SolveClicked implements EventHandler<ActionEvent> {

        Stage parent;
        Label l;
        Button solve, cancel;
        HBox hor;
        public SolveClicked(Stage parent, Label l, Button solve, Button cancel, HBox hor) {
            this.parent = parent;
            this.l = l;
            this.solve = solve;
            this.cancel = cancel;
            this.hor = hor;
        }

        public void handle(ActionEvent event) {
            this.solve.setVisible(false);
            this.hor.getChildren().remove(this.solve);
            this.l.setStyle("-fx-text-fill: #003B45; -fx-font-size: 14px; -fx-text-alignment: center;");
            this.l.setText("Solving...");
            this.cancel.setText("Done");
            this.cancel.setDisable(true);

            GameScreen.this.timeConsumer = new Thread(() -> {
                if(GameScreen.this.field.generated_solution == null) {
                    GameScreen.this.solver = new MDSolver(GameScreen.this.fieldSize, GameScreen.this.field);
                    GameScreen.this.solver.solve();
                    Platform.runLater(() -> {
                        if(GameScreen.this.solver.isSolved() == true) {
                            GameScreen.this.isSolved = true;
                            GameScreen.this.solution = GameScreen.this.solver.getSolutionValues();
                            this.l.setText("Solved!");
                            this.l.setStyle("-fx-text-fill: #003B45; -fx-font-size: 14px; -fx-text-alignment: center;");
                            GameScreen.this.buttons.get(5).setText("Show Solution");
                            GameScreen.this.enableButton(GameScreen.this.buttons.get(6));
                            this.cancel.setDisable(false);
                        }
                        else {
                            GameScreen.this.isSolved = false;
                            this.l.setText("Not solved!");
                            this.l.setStyle("-fx-text-fill: #003B45; -fx-font-size: 14px; -fx-text-alignment: center;");
                            this.cancel.setDisable(false);
                        }
                    });
                }

                else {
                    Platform.runLater(() -> {
                        GameScreen.this.isSolved = true;
                        GameScreen.this.solution = GameScreen.this.field.generated_solution;
                        this.l.setText("Solved!");
                        this.l.setStyle("-fx-text-fill: #003B45; -fx-font-size: 14px; -fx-text-alignment: center;");
                        GameScreen.this.buttons.get(5).setText("Show Solution");
                        GameScreen.this.enableButton(GameScreen.this.buttons.get(6));
                        this.cancel.setDisable(false);
                    });
                }
            });
            GameScreen.this.timeConsumer.setDaemon(true);
            GameScreen.this.timeConsumer.start();
        }
    }
    
    class ShowSolution implements EventHandler<ActionEvent> {
        
        Stage parent;
        public ShowSolution(Stage parent) {
            this.parent = parent;
        }
        public void handle(ActionEvent event) {
            int p = 0;
            GameScreen.this.showedSolution = true;
            for(int i = 0; i < GameScreen.this.fieldSize; i++) {
                for(int j = 0; j < GameScreen.this.fieldSize; j++) {
                    Pane d = (Pane) GameScreen.this.grid.getChildren().get(p);
                    Label l = (Label)((((HBox)((VBox)d.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
                    l.setText("" + GameScreen.this.solution[i][j]);
                    p++;
                }
            }

            for(int i = 0; i < GameScreen.this.buttons.size() - 1; i++) {
                GameScreen.this.disableButton(GameScreen.this.buttons.get(i));
            }

            for(int i = 0; i < GameScreen.this.buttons2.size(); i++) {
                GameScreen.this.disableButton(GameScreen.this.buttons2.get(i));
            }

            this.parent.close();
        }
    }

    class ShowHint implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            if(GameScreen.this.selectedPane == null) {
                return;
            }
            Label l = (Label)((((HBox)((VBox)GameScreen.this.selectedPane.getChildren().get(0)).getChildren().get(1)).getChildren().get(0)));
            String val = l.getText();
            l.setText("" + GameScreen.this.solution[GameScreen.this.selected.getX()][GameScreen.this.selected.getY()]);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished((a) -> {
                l.setText("" + val);
            });
            pause.play();
        }
    }

    class MenuClicked implements EventHandler<ActionEvent> {

        Stage parent;
        public MenuClicked(Stage parent) {
            this.parent = parent;
        }

        public void handle(ActionEvent event) {
            GameScreen.this.controller.currentScreen = new MainScreen(GameScreen.this.controller.mainStage, GameScreen.this.controller);
            GameScreen.this.controller.showScreen(GameScreen.this.controller.currentScreen.getScene());
            this.parent.close();
        }
    }

    class ShowGrid implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {
            for(int i = 0; i < GameScreen.this.fieldSize; i++) {
                for(int j = 0; j < GameScreen.this.fieldSize; j++) {
                    Pane p = (Pane)(GameScreen.this.grid.getChildren().get(i*GameScreen.this.fieldSize + j));
                    VBox tv = (VBox)(p.getChildren().get(0));
                    tv.setPadding(new Insets(4, 7, 4, 7));
                    HBox h1 = (HBox)(tv.getChildren().get(0));
                    HBox h2 = (HBox)(tv.getChildren().get(1));
                    Label l1 = (Label)(h1.getChildren().get(0));
                    Label l2 = (Label)(h2.getChildren().get(0));

                    String t, d, l, r;
                    String st, sd, sl, sr;
                    if(i > 0 && GameScreen.this.field.getField()[i][j].getGroup() == GameScreen.this.field.getField()[i - 1][j].getGroup()) {
                        t = "#66A4AC";
                        st = "dashed";
                    }
                    else {
                        t = "#003B45";
                        st = "solid";
                    }

                    if(j > 0 && GameScreen.this.field.getField()[i][j].getGroup() == GameScreen.this.field.getField()[i][j - 1].getGroup()) {
                        l = "#66A4AC";
                        sl = "dashed";
                    }
                    else {
                        l = "#003B45";
                        sl = "solid";
                    }

                    if(j < GameScreen.this.fieldSize - 1 && GameScreen.this.field.getField()[i][j].getGroup() == GameScreen.this.field.getField()[i][j + 1].getGroup()) {
                        r = "#66A4AC";
                        sr = "dashed";
                    }
                    else {
                        r = "#003B45";
                        sr = "solid";
                    }

                    if(i < GameScreen.this.fieldSize - 1 && GameScreen.this.field.getField()[i][j].getGroup() == GameScreen.this.field.getField()[i + 1][j].getGroup()) {
                        d = "#66A4AC";
                        sd = "dashed";
                    }
                    else {
                        d = "#003B45";
                        sd = "solid";
                    }

                    String styleTxt = "-fx-border-style: " + st + " " + sr + " " + sd + " " + sl + "; -fx-border-width: 2px 2px 2px 2px; -fx-border-color: " + t + " " + r + " " + d + " " + l + ";";

                    p.setStyle((String)styleTxt);
                    l1.setVisible(true);
                    l2.setVisible(true);
                }
            }

            GameScreen.this.showedSolution = true;
            VBox vCenter = new VBox();
            HBox hCenter = new HBox();
            hCenter.setAlignment(Pos.CENTER);
            vCenter.setAlignment(Pos.CENTER);
            GameScreen.this.grid.setStyle("-fx-border-style: solid; -fx-border-width: 2px; -fx-border-color: #003B45;");
            vCenter.getChildren().add(GameScreen.this.grid);
            hCenter.getChildren().add(vCenter);
            GameScreen.this.outer.setCenter(hCenter);
        }
    }
}