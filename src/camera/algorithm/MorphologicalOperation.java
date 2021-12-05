package camera.algorithm;

import camera.Frame;
import camera.MorphologicalOperations;

public class MorphologicalOperation implements ImageProcessingAlgorithm {

    private final Frame act;

    /**
     * This value is the limit to binarize the image
     */
    private float thresholdValue = 0.5f;

    private float morphCount = 1.0f;

    private final Frame threshold;

    private MorphologicalOperations morpho = MorphologicalOperations.DILATATION;

    public MorphologicalOperation(int w, int h) {
        this.act = new Frame(w, h);
        this.threshold = new Frame(w, h);
    }

    private void filterThreshold(Frame in) {
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                act.setValue(x, y, in.getValue(x, y) > thresholdValue ? 1.0f : 0.0f);
            }
        }
    }

    public void dilatation(Frame in, Frame out) {
        for (int n = 0; n < morphCount; n++) {
            out.copy(act);
            for (int y = 0; y < in.getH(); y++) {
                for (int x = 0; x < in.getW(); x++) {
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
    }

    public void erosion(Frame in, Frame out) {
        for (int n = 0; n < morphCount; n++) {
            out.copy(act);
            for (int y = 0; y < in.getH(); y++) {
                for (int x = 0; x < in.getW(); x++) {
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
    }

    public void edge(Frame in, Frame out) {
        out.copy(act);
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                float sum = act.getValue(x - 1, y) + act.getValue(x + 1, y) + act.getValue(x, y - 1) + act.getValue(x, y + 1) +
                        act.getValue(x - 1, y - 1) + act.getValue(x + 1, y + 1) + act.getValue(x + 1, y - 1) + act.getValue(x - 1, y + 1);
                if (act.getValue(x, y) == 1.0f && sum == 8.0f) {
                    out.setValue(x, y, 0.0f);
                }
            }
        }
        act.copy(out);
    }

    @Override
    public void process(Frame in, Frame out) {
        filterThreshold(in);
        threshold.copy(act);
        switch (morpho) {
            case DILATATION:
                dilatation(in, out);
                break;
            case EROSION:
                erosion(in, out);
                break;
            case EDGE:
                edge(in, out);
                break;
        }
    }

    public Frame getThresholdFrame() {
        return threshold;
    }

    public void increaseThreshold(float val) {
        thresholdValue += val;
    }

    public float getThreshold() {
        return thresholdValue;
    }

    public void setThreshold(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public float getMorphCount() {
        return morphCount;
    }

    public void setMorphCount(float morphCount) {
        this.morphCount = morphCount;
    }

    public MorphologicalOperations getMorpho() {
        return morpho;
    }

    public void setMorpho(MorphologicalOperations morpho) {
        this.morpho = morpho;
    }

}
