package ch.pingu;

import ch.pingu.ui.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        MainLayout mainLayout = new MainLayout();
        
        Scene scene = new Scene(mainLayout.getRoot(), 1024, 768);
        
        primaryStage.setTitle("Pingu Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
