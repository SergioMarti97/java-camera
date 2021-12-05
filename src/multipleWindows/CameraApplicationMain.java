package multipleWindows;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import olcPGEApproach.windowManager.ScreensManager;

public class CameraApplicationMain extends Application {

    public static String mainScreenID = "main";
    public static String mainScreenLayout = "/multipleWindows/CameraApplicationLayout.fxml";
    public static String thresholdScreenID = "threshold";
    public static String thresholdScreenLayout = "/editors/threshold/ThresholdLayout.fxml";

    @Override
    public void start(Stage primaryStage) {
        ScreensManager manager = new ScreensManager();
        manager.loadScreen(CameraApplicationMain.mainScreenID, CameraApplicationMain.mainScreenLayout);
        manager.loadScreen(CameraApplicationMain.thresholdScreenID, CameraApplicationMain.thresholdScreenLayout);

        manager.setScreen(mainScreenID);

        Group root = new Group();
        root.getChildren().addAll(manager);
        Scene scene = new Scene(root, 1320, 980);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
