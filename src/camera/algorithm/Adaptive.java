package camera.algorithm;

import camera.Frame;

public class Adaptive implements ImageProcessingAlgorithm {

    private int sizeMedian = 5;

    private float adaptiveBias = 1.0f;

    public Adaptive() {

    }

    public Adaptive(int sizeMedian, float adaptiveBias) {
        this.sizeMedian = sizeMedian;
        this.adaptiveBias = adaptiveBias;
    }

    @Override
    public void process(Frame in, Frame out) {
        int bottom = sizeMedian / 2;
        int top = bottom + 1;
        int total = sizeMedian * sizeMedian;
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                float regionSum = 0.0f;
                for (int n = -bottom; n < top; n++) {
                    for (int m = -bottom; m < top; m++) {
                        regionSum += in.getValue(x + n, y + m);
                    }
                }
                regionSum /= total;
                out.setValue(x, y, in.getValue(x, y) > (regionSum * adaptiveBias) ? 1.0f : 0.0f);
            }
        }
    }

    public void increaseSizeMedian(int val) {
        sizeMedian += val;
    }

    public int getSizeMedian() {
        return sizeMedian;
    }

    public void setSizeMedian(int sizeMedian) {
        this.sizeMedian = sizeMedian;
    }

    public void increaseAdaptiveBias(float val) {
        adaptiveBias += val;
    }

    public float getAdaptiveBias() {
        return adaptiveBias;
    }

    public void setAdaptiveBias(float adaptiveBias) {
        this.adaptiveBias = adaptiveBias;
    }

}
