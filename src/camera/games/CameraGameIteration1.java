package camera.games;

import camera.ColorChannels;
import camera.Frame;
import camera.ImageProcessingAlgorithms;
import camera.MorphologicalOperations;
import com.github.sarxos.webcam.Webcam;
import javafx.scene.input.KeyCode;
import olcPGEApproach.AbstractGame;
import olcPGEApproach.GameContainer;
import olcPGEApproach.gfx.HexColors;
import olcPGEApproach.gfx.Renderer;
import olcPGEApproach.gfx.images.Image;

import java.util.ArrayList;
import java.util.HashMap;

public class CameraGameIteration1 implements AbstractGame {

    private Webcam webcam;

    private Frame img;

    private Frame imgLast;

    private Frame out;

    private Frame act;

    private Frame threshold;

    private boolean drawColorImgInput = false;

    private float thresholdValue = 0.5f;

    private float lowPassRC = 0.5f;

    private int numKernel = 0;

    private float morphCount = 1.0f;

    private int sizeMedian = 5;

    private float adaptiveBias = 1.0f;

    private final Float[] kernelBlur = new Float[] {
            0.0f,   0.125f,  0.0f,
            0.125f, 0.5f,   0.125f,
            0.0f,   0.125f, 0.0f
    };

    private final Float[] kernelSharpen = new Float[] {
            0.0f,  -1.0f,  0.0f,
            -1.0f,  5.0f, -1.0f,
            0.0f,  -1.0f,  0.0f
    };

    private final Float[] kernelSobelV = new Float[] {
            -1.0f, 0.0f, +1.0f,
            -2.0f, 0.0f, +2.0f,
            -1.0f, 0.0f, +1.0f,
    };

    private final Float[] kernelSobelH = new Float[] {
            -1.0f, -2.0f, -1.0f,
            0.0f, 0.0f, 0.0f,
            +1.0f, +2.0f, +1.0f,
    };

    private final HashMap<Integer, Float[]> kernels = new HashMap<Integer, Float[]>() {{
        put(0, kernelBlur);
        put(1, kernelSharpen);
    }};

    private final HashMap<Integer, Float[]> kernelsSobel = new HashMap<Integer, Float[]>() {{
        put(0, kernelSobelV);
        put(1, kernelSobelH);
    }};

    private final ArrayList<Float> surroundings = new ArrayList<>();

    private ImageProcessingAlgorithms algo;

    private MorphologicalOperations morph;

    private ColorChannels colChannel;

    @Override
    public void initialize(GameContainer gc) {
        webcam = Webcam.getDefault();
        webcam.open();

        int imgWidth = webcam.getImage().getWidth();
        int imgHeight = webcam.getImage().getHeight();

        img = new Frame(imgWidth, imgHeight);
        imgLast = new Frame(imgWidth, imgHeight);
        out = new Frame(imgWidth, imgHeight);
        act = new Frame(imgWidth, imgHeight);
        threshold = new Frame(imgWidth, imgHeight);

        algo = ImageProcessingAlgorithms.THRESHOLD;
        morph = MorphologicalOperations.EROSION;
        colChannel = ColorChannels.RED;
    }

    private void threshold(GameContainer gc, float elapsedTime) {
        if (gc.getInput().isKeyHeld(KeyCode.Z)) {
            thresholdValue += 0.1f * elapsedTime;
        }
        if (gc.getInput().isKeyHeld(KeyCode.X)) {
            thresholdValue -= 0.1f * elapsedTime;
        }
        if (thresholdValue < 0.0f) {
            thresholdValue = 0.0f;
        }
        if (thresholdValue > 1.0f) {
            thresholdValue = 1.0f;
        }

        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                out.setValue(x, y, img.getValue(x, y) > thresholdValue ? 1.0f : 0.0f);
            }
        }
    }

    public void motion() {
        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                out.setValue(x, y, Math.abs(img.getValue(x, y) - imgLast.getValue(x, y)));
            }
        }
    }

    private void lowPass(GameContainer gc, float elapsedTime) {
        if (gc.getInput().isKeyHeld(KeyCode.Z)) {
            lowPassRC += 0.1f * elapsedTime;
        }
        if (gc.getInput().isKeyHeld(KeyCode.X)) {
            lowPassRC -= 0.1f * elapsedTime;
        }
        if (lowPassRC < 0.0f) {
            lowPassRC = 0.0f;
        }
        if (lowPassRC > 1.0f) {
            lowPassRC = 1.0f;
        }

        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                float pixel = img.getValue(x, y) - out.getValue(x, y);
                pixel *= lowPassRC;
                out.setValue(x, y, pixel + out.getValue(x, y));
            }
        }
    }

    private void convolution(GameContainer gc) {
        if (gc.getInput().isKeyDown(KeyCode.DIGIT1)) {
            numKernel = 0;
        }
        if (gc.getInput().isKeyDown(KeyCode.DIGIT2)) {
            numKernel = 1;
        }

        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                float sum = 0.0f;
                for (int n = -1; n < 2; n++) {
                    for (int m = -1; m < 2; m++) {
                        sum += img.getValue(x + n, y + m) * kernels.get(numKernel)[(m + 1) * 3 + (n + 1)];
                    }
                }
                out.setValue(x, y, sum);
            }
        }
    }

    private void sobel() {
        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                float kernelSumV = 0.0f;
                float kernelSumH = 0.0f;
                for (int n = -1; n < 2; n++) {
                    for (int m = -1; m < 2; m++) {
                        kernelSumV += img.getValue(x + n, y + m) * kernelsSobel.get(0)[(m + 1) * 3 + (n + 1)];
                        kernelSumH += img.getValue(x + n, y + m) * kernelsSobel.get(1)[(m + 1) * 3 + (n + 1)];
                    }
                }
                out.setValue(x, y, Math.abs(kernelSumV + kernelSumH) / 2.0f);
            }
        }
    }

    private void morpho(GameContainer gc, float elapsedTime) {
        if (gc.getInput().isKeyDown(KeyCode.DIGIT1)) {
            morph = MorphologicalOperations.EROSION;
        }
        if (gc.getInput().isKeyDown(KeyCode.DIGIT2)) {
            morph = MorphologicalOperations.DILATATION;
        }
        if (gc.getInput().isKeyDown(KeyCode.DIGIT3)) {
            morph = MorphologicalOperations.EDGE;
        }

        if (gc.getInput().isKeyHeld(KeyCode.Z)) {
            thresholdValue += 0.1f * elapsedTime;
        }
        if (gc.getInput().isKeyHeld(KeyCode.X)) {
            thresholdValue -= 0.1f * elapsedTime;
        }
        if (thresholdValue < 0.0f) {
            thresholdValue = 0.0f;
        }
        if (thresholdValue > 1.0f) {
            thresholdValue = 1.0f;
        }

        if (gc.getInput().isKeyHeld(KeyCode.A)) {
            morphCount++;
        }
        if (gc.getInput().isKeyHeld(KeyCode.S)) {
            morphCount--;
        }
        if (morphCount < 1.0f) {
            morphCount = 1.0f;
        }
        if (morphCount > 10.0f) {
            morphCount = 10.0f;
        }

        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                act.setValue(x, y, img.getValue(x, y) > thresholdValue ? 1.0f : 0.0f);
            }
        }

        threshold.copy(act);

        switch (morph) {
            case DILATATION:
                for (int n = 0; n < morphCount; n++) {
                    out.copy(act);
                    for (int y = 0; y < img.getH(); y++) {
                        for (int x = 0; x < img.getW(); x++) {
                            if (act.getValue(x, y) == 1.0f) {
                                out.setValue(x, y, 1.0f);
                                out.setValue(x - 1, y, 1.0f);
                                out.setValue(x + 1, y, 1.0f);
                                out.setValue(x, y - 1, 1.0f);
                                out.setValue(x, y + 1, 1.0f);
                                out.setValue(x - 1, y - 1, 1.0f);
                                out.setValue(x + 1, y + 1, 1.0f);
                                out.setValue(x + 1, y - 1, 1.0f);
                                out.setValue(x - 1, y + 1, 1.0f);
                            }
                        }
                    }
                    act.copy(out);
                }
                out.copy(act);
                break;
            case EROSION:
                for (int n = 0; n < morphCount; n++) {
                    out.copy(act);
                    for (int y = 0; y < img.getH(); y++) {
                        for (int x = 0; x < img.getW(); x++) {
                            float sum = act.getValue(x - 1, y) + act.getValue(x + 1, y) + act.getValue(x, y - 1) + act.getValue(x, y + 1) +
                                    act.getValue(x - 1, y - 1) + act.getValue(x + 1, y + 1) + act.getValue(x + 1, y - 1) + act.getValue(x - 1, y + 1);
                            if (act.getValue(x, y) == 1.0f && sum < 8.0f) {
                                out.setValue(x, y, 0.0f);
                            }
                        }
                    }
                    act.copy(out);
                }
                out.copy(act);
                break;
            case EDGE:
                out.copy(act);
                for (int y = 0; y < img.getH(); y++) {
                    for (int x = 0; x < img.getW(); x++) {
                        float sum = act.getValue(x - 1, y) + act.getValue(x + 1, y) + act.getValue(x, y - 1) + act.getValue(x, y + 1) +
                                act.getValue(x - 1, y - 1) + act.getValue(x + 1, y + 1) + act.getValue(x + 1, y - 1) + act.getValue(x - 1, y + 1);
                        if (act.getValue(x, y) == 1.0f && sum == 8.0f) {
                            out.setValue(x, y, 0.0f);
                        }
                    }
                }
                act.copy(out);
                break;
        }
    }

    private void median(GameContainer gc) {
        if (gc.getInput().isKeyDown(KeyCode.Z)) {
            sizeMedian += 2;
        }
        if (gc.getInput().isKeyDown(KeyCode.X)) {
            sizeMedian -= 2;
        }
        if (sizeMedian < 3) {
            sizeMedian = 3;
        }
        if (sizeMedian > 11) {
            sizeMedian = 11;
        }

        int bottom = sizeMedian / 2;
        int top = bottom + 1;
        int middle = sizeMedian * sizeMedian / 2;
        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                surroundings.clear();
                for (int n = -bottom; n < top; n++) {
                    for (int m = -bottom; m < top; m++) {
                        surroundings.add(img.getValue(x + n, y + m));
                    }
                }
                surroundings.sort(Float::compare);
                out.setValue(x, y, surroundings.get(middle));
            }
        }
    }

    private void adaptive(GameContainer gc, float elapsedTime) {
        if (gc.getInput().isKeyDown(KeyCode.A)) {
            sizeMedian += 2;
        }
        if (gc.getInput().isKeyDown(KeyCode.S)) {
            sizeMedian -= 2;
        }
        if (sizeMedian < 3) {
            sizeMedian = 3;
        }
        if (sizeMedian > 11) {
            sizeMedian = 11;
        }

        if (gc.getInput().isKeyHeld(KeyCode.Z)) {
            adaptiveBias += 0.1f * elapsedTime;
        }
        if (gc.getInput().isKeyHeld(KeyCode.X)) {
            adaptiveBias -= 0.1f * elapsedTime;
        }
        if (adaptiveBias < 0.5f) {
            adaptiveBias = 0.5f;
        }
        if (adaptiveBias > 1.5f) {
            adaptiveBias = 1.5f;
        }

        int bottom = sizeMedian / 2;
        int top = bottom + 1;
        int total = sizeMedian * sizeMedian;
        for (int y = 0; y < img.getH(); y++) {
            for (int x = 0; x < img.getW(); x++) {
                float regionSum = 0.0f;
                for (int n = -bottom; n < top; n++) {
                    for (int m = -bottom; m < top; m++) {
                        regionSum += img.getValue(x + n, y + m);
                    }
                }
                regionSum /= total;
                out.setValue(x, y, img.getValue(x, y) > (regionSum * adaptiveBias) ? 1.0f : 0.0f);
            }
        }
    }

    @Override
    public void update(GameContainer gc, float elapsedTime) {
        img.setP(webcam.getImage().getRGB(0, 0, img.getW(), img.getH(), null, 0, img.getW()));
        img.update(colChannel);

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            drawColorImgInput = !drawColorImgInput;
        }

        if (gc.getInput().isKeyDown(KeyCode.R)) {
            colChannel = ColorChannels.RED;
        }
        if (gc.getInput().isKeyDown(KeyCode.G)) {
            colChannel = ColorChannels.GREEN;
        }
        if (gc.getInput().isKeyDown(KeyCode.B)) {
            colChannel = ColorChannels.BLUE;
        }
        if (gc.getInput().isKeyDown(KeyCode.M)) {
            colChannel = ColorChannels.MEDIAN;
        }

        if (gc.getInput().isKeyDown(KeyCode.NUMPAD1)) {
            algo = ImageProcessingAlgorithms.THRESHOLD;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD2)) {
            algo = ImageProcessingAlgorithms.MOTION;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD3)) {
            algo = ImageProcessingAlgorithms.LOWPASS;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD4)) {
            algo = ImageProcessingAlgorithms.CONVOLUTION;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD5)) {
            algo = ImageProcessingAlgorithms.SOBEL;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD6)) {
            algo = ImageProcessingAlgorithms.MORPHO;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD7)) {
            algo = ImageProcessingAlgorithms.MEDIAN;
        }
        if (gc.getInput().isKeyDown(KeyCode.NUMPAD8)) {
            algo = ImageProcessingAlgorithms.ADAPTIVE;
        }

        switch (algo) {
            case THRESHOLD: default:
                threshold(gc, elapsedTime);
                break;
            case MOTION:
                motion();
                break;
            case LOWPASS:
                lowPass(gc, elapsedTime);
                break;
            case CONVOLUTION:
                convolution(gc);
                break;
            case SOBEL:
                sobel();
                break;
            case MORPHO:
                morpho(gc, elapsedTime);
                break;
            case MEDIAN:
                median(gc);
                break;
            case ADAPTIVE:
                adaptive(gc, elapsedTime);
                break;
        }

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
            drawFrame(gc.getRenderer(), marginWidth, y, algo == ImageProcessingAlgorithms.MORPHO ? threshold : img, pixelImgSize);
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

        switch (algo) {
            case THRESHOLD:
                gc.getRenderer().drawText(String.format("Threshold: %.5f", thresholdValue), marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText("Hold Z key to increase threshold or X to decrease", marginWidth, y, HexColors.WHITE);
                break;
            case MOTION:
                break;
            case LOWPASS:
                gc.getRenderer().drawText(String.format("Low pass RC: %.5f", lowPassRC), marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText("Hold Z key to increase low pass RC or X to decrease", marginWidth, y, HexColors.WHITE);
                break;
            case CONVOLUTION:
                gc.getRenderer().drawText("Using kernel: " + (numKernel == 0 ? "blur" : "sharpen"), marginWidth, y, HexColors.WHITE);
                y+= fontHeight;
                gc.getRenderer().drawText("Press number 1 to blur image", marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText("Press number 2 to sharpen image", marginWidth, y, HexColors.WHITE);
                break;
            case MORPHO:
                gc.getRenderer().drawText("Morphological operation: " + morph.toString().toLowerCase(), marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                String morphoText = "(";
                for (int i = 0; i < MorphologicalOperations.values().length; i++) {
                    morphoText = morphoText.concat("press " + (i + 1) + " to " + MorphologicalOperations.values()[i].toString().toLowerCase()
                            + (i == (MorphologicalOperations.values().length - 1) ? "" : " "));
                }
                morphoText = morphoText.concat(")");
                gc.getRenderer().drawText(morphoText, marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText(String.format("Threshold: %.5f Morpho count: %.5f", thresholdValue, morphCount), marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText("Hold Z key to increase threshold or X to decrease", marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText("Hold A key to increase morpho count or S to decrease", marginWidth, y, HexColors.WHITE);
                break;
            case MEDIAN:
                gc.getRenderer().drawText("Area to make the median: " + sizeMedian + "x"+ sizeMedian, marginWidth, y, HexColors.WHITE);
                break;
            case ADAPTIVE:
                gc.getRenderer().drawText(String.format("Adaptive bias: %.5f", adaptiveBias), marginWidth, y, HexColors.WHITE);
                y += fontHeight;
                gc.getRenderer().drawText("Hold Z key to increase adaptive bias value or X to decrease", marginWidth, y, HexColors.WHITE);
                break;
        }
    }

}
