package camera;

import olcPGEApproach.gfx.images.Image;

public class Frame extends Image {

    private final float[] pixels;

    private ColorChannels channel;

    public Frame(int w, int h) {
        super(w, h);
        pixels = new float[w * h];
        channel = ColorChannels.RED;
    }

    public Frame(Frame frame) {
        super(frame.w, frame.h);
        pixels = new float[frame.w * frame.h];
        System.arraycopy(frame.pixels, 0, pixels, 0, frame.pixels.length);
        this.channel = frame.getChannel();
    }

    public void update(ColorChannels channel) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                switch (channel) {
                    case RED:
                        setValue(x, y, (p[x + w * y] >> 16 & 0xFF) / 255.0f);
                        break;
                    case GREEN:
                        setValue(x, y, (p[x + w * y] >> 8 & 0xFF) / 255.0f);
                        break;
                    case BLUE:
                        setValue(x, y, (p[x + w * y] & 0xFF) / 255.0f);
                        break;
                    case MEDIAN:
                        int r = (p[x + w * y] >> 16 & 0xFF);
                        int g = (p[x + w * y] >> 8 & 0xFF);
                        int b = (p[x + w * y] & 0xFF);
                        setValue(x, y, (r + g + b) / (3.0f * 255.0f));
                        break;
                }
            }
        }
    }

    public void update() {
        update(channel);
    }

    public void copy(Frame frame) {
        this.w = frame.getW();
        this.h = frame.getH();
        System.arraycopy(frame.p, 0, p, 0, frame.p.length);
        this.alpha = frame.alpha;
        System.arraycopy(frame.pixels, 0, pixels, 0, frame.pixels.length);
    }

    public float getValue(int x, int y) {
        if (x >= 0 && x < w && y >= 0 && y < h) {
            return pixels[x + w * y];
        } else {
            return 0.0f;
        }
    }

    public void setValue(int x, int y, float p) {
        if (x >= 0 && x < w && y >= 0 && y < h) {
            pixels[x + w * y] = p;
        }
    }

    public float[] getPixels() {
        return pixels;
    }

    public ColorChannels getChannel() {
        return channel;
    }

    public void setChannel(ColorChannels channel) {
        this.channel = channel;
    }

}
