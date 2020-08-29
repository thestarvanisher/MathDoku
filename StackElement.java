import javafx.scene.layout.*;

public class StackElement {

    Pane pane;
    int value, pValue;
    int x, y;

    public StackElement(int x, int y, int value, int pValue, Pane pane) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.pValue = pValue;
        this.pane = pane;
    }

    public int getValue() {
        return this.value;
    }

    public int getPValue() {
        return this.pValue;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Pane getPane() {
        return this.pane;
    }
}