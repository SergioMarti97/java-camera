package camera.algorithm;

import camera.Frame;

/**
 * This interface is to group all algorithms in one generic
 */
public interface ImageProcessingAlgorithm {

    /**
     * This method is what the algorithm do
     * @param in the input frame
     * @param out the output frame
     */
    void process(Frame in, Frame out);

}
