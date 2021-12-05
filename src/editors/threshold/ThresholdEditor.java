package editors.threshold;

import camera.algorithm.Threshold;
import editors.AbstractEditor;
import javafx.scene.input.KeyCode;
import olcPGEApproach.GameContainer;
import olcPGEApproach.gfx.HexColors;

public class ThresholdEditor extends AbstractEditor {

    private Threshold algo;

    public ThresholdEditor(Threshold algo) {
        this.algo = algo;
    }

    @Override
    public void initialize(GameContainer gc) {
        super.initialize(gc);
        algo = new Threshold();
    }

    @Override
    public void update(GameContainer gc, float elapsedTime) {
        updateImg();

        final float delta = 0.1f;

        if (gc.getInput().isKeyHeld(KeyCode.Z)) {
            algo.increaseThreshold(delta * elapsedTime);
        }
        if (gc.getInput().isKeyHeld(KeyCode.X)) {
            algo.increaseThreshold(-delta * elapsedTime);
        }
        if (algo.getThresholdValue() < 0.0f) {
            algo.setThresholdValue(0.0f);
        }
        if (algo.getThresholdValue() > 1.0f) {
            algo.setThresholdValue(1.0f);
        }

        algo.process(img, out);
    }

    @Override
    public void render(GameContainer gc) {
        gc.getRenderer().clear(HexColors.BLUE);

        int y;
        int marginY = 10;
        int pixelImgSize = 4;
        int imgW = img.getW() * pixelImgSize;
        int outW = img.getW() * pixelImgSize;
        int marginMiddle = 5;
        int totalWidth = imgW + outW + marginMiddle;
        int restWidth = gc.getRenderer().getW() - totalWidth;
        int marginWidth = restWidth / 2;

        y = marginY;
        drawFrame(gc.getRenderer(), marginWidth, y, img, pixelImgSize);
        drawFrame(gc.getRenderer(), marginWidth + marginMiddle + outW, y, out, pixelImgSize);
        //y += img.getH() * pixelImgSize;
        //y += 2 * marginY;
    }

}
