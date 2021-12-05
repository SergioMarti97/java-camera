package camera.algorithm;

import camera.Frame;

public class Motion implements ImageProcessingAlgorithm {

    private Frame last;

    public Motion(Frame last) {
        this.last = last;
    }

    @Override
    public void process(Frame in, Frame out) {
        for (int y = 0; y < in.getH(); y++) {
            for (int x = 0; x < in.getW(); x++) {
                out.setValue(x, y, Math.abs(in.getValue(x, y) - last.getValue(x, y)));
            }
        }
    }

    public Frame getLast() {
        return last;
    }

    public void setLast(Frame last) {
        this.last = last;
    }

}
