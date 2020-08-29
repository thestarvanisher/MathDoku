import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.Scene;

public class MathDoku extends Application {

    Stage mainStage;
    GeneralScreen currentScreen;

    public void showScreen(Scene scene) {
        this.mainStage.setScene(scene);
        this.mainStage.show();
        this.currentScreen.initialSize();
    }

    public void start(Stage stage) {
        this.mainStage = stage;
        this.mainStage.setTitle("MathDoku");
        this.currentScreen = new MainScreen(this.mainStage, this);
        this.currentScreen = new MainScreen(this.mainStage, this);
        
        this.showScreen(this.currentScreen.getScene());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}