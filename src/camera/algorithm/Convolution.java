package camera.algorithm;

import camera.Frame;

import java.util.HashMap;

public class Convolution implements ImageProcessingAlgorithm {

    enum Kernels {
        BLUR,
        SHARPEN
    }

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

    private final HashMap<Integer, Float[]> kernels = new HashMap<Integer, Float[]>() {{
        put(0, kernelBlur);
        put(1, kernelSharpen);
    }};

    private Kernels k = Kernels.BLUR;

    public Convolution() {

    }

    public Convolution(Kernels k) {
        this.k = k;
    }

    @Override
    public void process(Frame in, Frame out) {
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                float sum = 0.0f;
                for (int n = -1; n < 2; n++) {
                    for (int m = -1; m < 2; m++) {
                        sum += in.getValue(x + n, y + m) * kernels.get(k.ordinal())[(m + 1) * 3 + (n + 1)];
                    }
                }
                out.setValue(x, y, sum);
            }
        }
    }

    public Kernels getK() {
        return k;
    }

    public void setK(Kernels k) {
        this.k = k;
    }

}
