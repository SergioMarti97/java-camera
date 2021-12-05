package camera.games;

import camera.ColorChannels;
import camera.Frame;
import camera.ImageProcessingAlgorithms;
import camera.algorithm.*;
import com.github.sarxos.webcam.Webcam;
import olcPGEApproach.AbstractGame;
import olcPGEApproach.GameContainer;
import olcPGEApproach.gfx.HexColors;
import olcPGEApproach.gfx.Renderer;
import olcPGEApproach.gfx.images.Image;

import java.util.ArrayList;

public class CameraGameIteration2 implements AbstractGame {

    private Webcam webcam;

    private Frame img;

    private Frame imgLast;

    private Frame out;

    private ImageProcessingAlgorithms algo;

    private ColorChannels colChannel;

    private boolean drawColorImgInput = false;

    private ArrayList<ImageProcessingAlgorithm> algorithms;

    @Override
    public void initialize(GameContainer gc) {
        webcam = Webcam.getDefault();
        webcam.open();

        int imgWidth = webcam.getImage().getWidth();
        int imgHeight = webcam.getImage().getHeight();

        img = new Frame(imgWidth, imgHeight);
        imgLast = new Frame(imgWidth, imgHeight);
        out = new Frame(imgWidth, imgHeight);

        algorithms = new ArrayList<>();
        algorithms.add(new Threshold());
        algorithms.add(new Motion(imgLast));
        algorithms.add(new LowPass());
        algorithms.add(new Sobel());
        algorithms.add(new MorphologicalOperation(imgWidth, imgHeight));
        algorithms.add(new Adaptive());
        algorithms.add(new Median());
        algorithms.add(new Convolution());

        algo = ImageProcessingAlgorithms.THRESHOLD;
        colChannel = ColorChannels.RED;
    }

    @Override
    public void update(GameContainer gc, float v) {
        img.setP(webcam.getImage().getRGB(0, 0, img.getW(), img.getH(), null, 0, img.getW()));
        img.update(colChannel);

        algorithms.get(6).process(img, out);
        img.copy(out);
        algorithms.get(3).process(img, out);

        imgLast.copy(img);
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
        if (drawColorImgInput) {
            drawScaledImage(gc.getRenderer(), marginWidth, y, img, pixelImgSize);
        } else {
            if (algorithms.get(4) instanceof MorphologicalOperation) {
                MorphologicalOperation algoMorpho = (MorphologicalOperation) algorithms.get(4);
                drawFrame(gc.getRenderer(), marginWidth, y, algo == ImageProcessingAlgorithms.MORPHO ? algoMorpho.getThresholdFrame() : img, pixelImgSize);
            }
        }
        drawFrame(gc.getRenderer(), marginWidth + marginMiddle + outW, y, out, pixelImgSize);
        y += img.getH() * pixelImgSize;
        y += 2 * marginY;

        int fontHeight = 40;
        gc.getRenderer().drawText("Selected channel: " + colChannel.toString().toLowerCase() + " (press R, G, B, M keys to change)",
                marginWidth, y, HexColors.WHITE);
        y += fontHeight;
        gc.getRenderer().drawText("Algorithms", marginWidth, y, HexColors.WHITE);
        y += fontHeight;
        int numCols = 2;
        int numRowsByCol = ImageProcessingAlgorithms.values().length / numCols;
        int marginColsWidth = marginWidth;
        int previousY = 0;
        int count = 0;
        for (int i = 0; i < numCols; i++) {
            int textSize = 0;
            previousY = y;
            for (int j = 0; j < numRowsByCol; j++) {
                String text = (count + 1) + ") " + ImageProcessingAlgorithms.values()[count].toString().toLowerCase();
                if (textSize < text.length() * 20) {
                    textSize = text.length() * 20;
                }
                if (ImageProcessingAlgorithms.values()[count].equals(algo)) {
                    text = text.concat(" <-");
                }
                gc.getRenderer().drawText(text, marginColsWidth, previousY, HexColors.WHITE);
                previousY += fontHeight;
                count++;
            }
            marginColsWidth += textSize;
        }
        y = previousY;

    }
}
