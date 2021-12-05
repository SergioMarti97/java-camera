package multipleWindows;

import editors.threshold.ThresholdEditorController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import olcPGEApproach.AbstractGame;
import olcPGEApproach.GameContainer;
import olcPGEApproach.windowManager.ScreenController;

import java.net.URL;
import java.util.ResourceBundle;

public class CameraApplicationController extends ScreenController {

    @FXML
    private ImageView imgView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AbstractGame game = new CameraApplication();
        GameContainer gc = new GameContainer(game, (int) imgView.getFitWidth(), (int) imgView.getFitHeight(), imgView);
        imgView.setImage(gc.getImg());
        gc.getTimer().start();

        imgView.setOnKeyPressed(event -> {
            if (screenParent.getScreen(CameraApplicationMain.thresholdScreenID).getValue() instanceof ThresholdEditorController) {
                ThresholdEditorController thresholdEditorController = (ThresholdEditorController) screenParent.getScreen(CameraApplicationMain.thresholdScreenID).getValue();
                //thresholdEditorController.setAlgo(); // <- y aquí debería de pasarsele la instancia del algoritmo de procesamiento de imagenes
            }
            screenParent.setScreen(CameraApplicationMain.thresholdScreenID);
        });
    }

}
