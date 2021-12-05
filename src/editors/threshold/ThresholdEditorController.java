package editors.threshold;

import camera.algorithm.ImageProcessingAlgorithm;
import camera.algorithm.Threshold;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import multipleWindows.CameraApplicationMain;
import olcPGEApproach.AbstractGame;
import olcPGEApproach.GameContainer;
import olcPGEApproach.windowManager.ScreenController;

import java.net.URL;
import java.util.ResourceBundle;

public class ThresholdEditorController extends ScreenController {

    @FXML
    private Button btnSave;

    @FXML
    private ImageView imgView;

    private ImageProcessingAlgorithm algo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algo = new Threshold();
        AbstractGame game = new ThresholdEditor((Threshold) algo);
        GameContainer gc = new GameContainer(game, (int) imgView.getFitWidth(), (int) imgView.getFitHeight(), imgView);
        imgView.setImage(gc.getImg());
        gc.getTimer().start();

        btnSave.setOnAction((event)-> {
            screenParent.setScreen(CameraApplicationMain.mainScreenID);
        });
    }

    public ImageProcessingAlgorithm getAlgo() {
        return algo;
    }

    public void setAlgo(ImageProcessingAlgorithm algo) {
        this.algo = algo;
    }

}
