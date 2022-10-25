package rayTracer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class Workspace extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene;
        FXMLLoader loader = new FXMLLoader();
        WorkspaceController controller = new WorkspaceController();

        controller.setPrimaryStage(primaryStage);
        loader.setController(controller);

        scene = loader.load(new FileInputStream("data/workspace.fxml"));
        controller.initialize();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tiny Ray Tracer");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) { launch(Workspace.class, args); }
}
