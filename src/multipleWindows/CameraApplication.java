package multipleWindows;

import editors.AbstractEditor;
import olcPGEApproach.GameContainer;
import olcPGEApproach.gfx.HexColors;

public class CameraApplication extends AbstractEditor {

    @Override
    public void initialize(GameContainer gc) {
        super.initialize(gc);
    }

    @Override
    public void update(GameContainer gc, float v) {

    }

    @Override
    public void render(GameContainer gc) {
        gc.getRenderer().clear(HexColors.RED);
        gc.getRenderer().drawText("Main screen", 10, 10, HexColors.WHITE);
        gc.getRenderer().drawText("Press anything to change screen", 10, 50, HexColors.WHITE);
    }

}
