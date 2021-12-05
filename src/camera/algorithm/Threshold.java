package camera.algorithm;

import camera.Frame;

public class Threshold implements ImageProcessingAlgorithm {

    /**
     * This value is the limit to binarize the image
     */
    private float thresholdValue = 0.5f;

    /**
     * Void constructor
     */
    public Threshold() {

    }

    /**
     * Constructor
     * @param threshold the threshold value for this algorithm
     */
    public Threshold(float threshold) {
        this.thresholdValue = threshold;
    }

    @Override
    public void process(Frame in, Frame out) {
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                out.setValue(x, y, in.getValue(x, y) > thresholdValue ? 1.0f : 0.0f);
            }
        }
    }

    public void increaseThreshold(float val) {
        thresholdValue += val;
    }

    public float getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

}
