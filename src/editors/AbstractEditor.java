package editors;

import camera.Frame;
import com.github.sarxos.webcam.Webcam;
import olcPGEApproach.AbstractGame;
import olcPGEApproach.GameContainer;
import olcPGEApproach.gfx.Renderer;
import olcPGEApproach.gfx.images.Image;

public abstract class AbstractEditor implements AbstractGame {

    protected Webcam webcam;

    protected Frame img;

    protected Frame out;

    @Override
    public void initialize(GameContainer gc) {
        webcam = Webcam.getDefault();
        webcam.open();

        int imgWidth = webcam.getImage().getWidth();
        int imgHeight = webcam.getImage().getHeight();

        img = new Frame(imgWidth, imgHeight);
        out = new Frame(img);
    }

    protected void updateImg() {
        img.setP(webcam.getImage().getRGB(0, 0, img.getW(), img.getH(), null, 0, img.getW()));
        img.update();
    }

    public void drawScaledImage(Renderer r, int offX, int offY, Image img, int pixelSize) {
        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                r.drawFillRectangle(
                        x * pixelSize + offX,
                        y * pixelSize + offY,
                        pixelSize,
                        pixelSize,
                        img.getPixel(x, y));
            }
        }
    }

    public void drawFrame(Renderer r, int offX, int offY, Frame img, int pixelSize) {
        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                int p = (int)Math.min(Math.max(0.0f, img.getValue(x, y) * 255.0f), 255.0f);
                int c = 0xFF << 24 | p << 16 | p << 8 | p;
                r.drawFillRectangle(
                        x * pixelSize + offX,
                        y * pixelSize + offY,
                        pixelSize,
                        pixelSize,
                        c);
            }
        }
    }

}
