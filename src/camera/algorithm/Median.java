package camera.algorithm;

import camera.Frame;

import java.util.ArrayList;

public class Median implements ImageProcessingAlgorithm {

    private int sizeMedian = 5;

    private final ArrayList<Float> surroundings = new ArrayList<>();

    public Median() {

    }

    public Median(int sizeMedian) {
        this.sizeMedian = sizeMedian;
    }

    @Override
    public void process(Frame in, Frame out) {
        int bottom = sizeMedian / 2;
        int top = bottom + 1;
        int middle = sizeMedian * sizeMedian / 2;
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                surroundings.clear();
                for (int n = -bottom; n < top; n++) {
                    for (int m = -bottom; m < top; m++) {
                        surroundings.add(in.getValue(x + n, y + m));
                    }
                }
                surroundings.sort(Float::compare);
                out.setValue(x, y, surroundings.get(middle));
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

}
