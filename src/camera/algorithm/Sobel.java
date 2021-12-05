package camera.algorithm;

import camera.Frame;

public class Sobel implements ImageProcessingAlgorithm {

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

    public Sobel() {

    }

    @Override
    public void process(Frame in, Frame out) {
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                float kernelSumV = 0.0f;
                float kernelSumH = 0.0f;
                for (int n = -1; n < 2; n++) {
                    for (int m = -1; m < 2; m++) {
                        kernelSumV += in.getValue(x + n, y + m) * kernelSobelV[(m + 1) * 3 + (n + 1)];
                        kernelSumH += in.getValue(x + n, y + m) * kernelSobelH[(m + 1) * 3 + (n + 1)];
                    }
                }
                out.setValue(x, y, Math.abs(kernelSumV + kernelSumH) / 2.0f);
            }
        }
    }

}
