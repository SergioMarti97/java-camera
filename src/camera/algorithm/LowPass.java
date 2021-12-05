package camera.algorithm;

import camera.Frame;

public class LowPass implements ImageProcessingAlgorithm {

    private float lowPassRC = 0.5f;

    public LowPass() {

    }

    public LowPass(float lowPassRC) {
        this.lowPassRC = lowPassRC;
    }

    @Override
    public void process(Frame in, Frame out) {
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                float pixel = in.getValue(x, y) - out.getValue(x, y);
                pixel *= lowPassRC;
                out.setValue(x, y, pixel + out.getValue(x, y));
            }
        }
    }

    public void increaseLowPassRC(float val) {
        lowPassRC += val;
    }

    public float getLowPassRC() {
        return lowPassRC;
    }

    public void setLowPassRC(float lowPassRC) {
        this.lowPassRC = lowPassRC;
    }

}
