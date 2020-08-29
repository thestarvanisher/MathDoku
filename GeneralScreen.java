import javafx.scene.*;

public abstract class GeneralScreen {

    public abstract void initialSize();

    public abstract void resize();

    public abstract void resize(int w, int h);

    public abstract Scene getScene();
}