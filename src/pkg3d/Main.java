package pkg3d;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static Stage primalStage = null;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        primalStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MainWindow.fxml"));
        Parent root = loader.load();
        PerspectiveCamera camera = (PerspectiveCamera) loader.getNamespace().get("sceneCamera");
        Scene scene = new Scene(root, 420, 510);
        scene.setCamera(camera);
        Stage stage = primaryStage;
        stage.setTitle("Floating Calc");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
