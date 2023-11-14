package eu.sblendorio.bbs.core;

public class MinitelControls {

    public static final byte ESC = 0x1b;
    public static final byte LF = 0x0a;

    public static final byte CHAR_BLACK = 0x40;
    public static final byte CHAR_RED = 0x41;
    public static final byte CHAR_GREEN = 0x42;
    public static final byte CHAR_YELLOW = 0x43;
    public static final byte CHAR_BLUE = 0x44;
    public static final byte CHAR_MAGENTA = 0x45;
    public static final byte CHAR_CYAN = 0x46;
    public static final byte CHAR_WHITE = 0x47;

    public static final byte BACKGROUND_BLACK = 0x50;
    public static final byte BACKGROUND_RED = 0x51;
    public static final byte BACKGROUND_GREEN = 0x52;
    public static final byte BACKGROUND_YELLOW = 0x53;
    public static final byte BACKGROUND_BLUE = 0x54;
    public static final byte BACKGROUND_MAGENTA = 0x55;
    public static final byte BACKGROUND_CYAN = 0x56;
    public static final byte BACKGROUND_WHITE = 0x57;

    public static final byte TEXTSIZE_NORMAL = 0x4c;
    public static final byte TEXTSIZE_DOUBLE_HEIGHT = 0x4d;
    public static final byte TEXTSIZE_DOUBLE_WIDTH = 0x4e;
    public static final byte TEXTSIZE_DOUBLE_ALL = 0x4f;

    public static final byte FLASH_ON = 0x48;
    public static final byte FLASH_OFF = 0x49;

    public static final byte MASK_ON = 0x58;
    public static final byte MASK_OFF = 0x5f;

    public static final byte LIGNAGE_ON = 0x59;
    public static final byte LIGNAGE_OFF = 0x5a;

    public static final byte REV_ON = 0x5c;
    public static final byte REV_OFF = 0x5d;

    public static final byte[] CSI = { 0x1b, 0x5b };

    public static final byte BACKSPACE_KEY = 0x08;
    public static final byte CURSOR_LEFT = 0x08;
    public static final byte CURSOR_RIGHT = 0x09;
    public static final byte CURSOR_UP = 0x0b;
    public static final byte CURSOR_DOWN = 0x0a;

    public static final byte CURSOR_ON = 0x11;
    public static final byte CURSOR_OFF = 0x14;

    public static final byte MOVEXY = 0x1f;
    public static final byte BEEP = 0x07;

    public static final byte[] SCROLL_ON = new byte[] { 0x1b, 0x3a, 0x69, 0x43 };
    public static final byte[] SCROLL_OFF = new byte[] { 0x1b, 0x3a, 0x6a, 0x43 };
    public static final byte[] CAPSLOCK_ON = new byte[] { 0x1b, 0x3a, 0x6a, 0x45 };
    public static final byte[] CAPSLOCK_OFF = new byte[] { 0x1b, 0x3a, 0x69, 0x45 };


    private MinitelControls() {
        throw new IllegalStateException("Utility class");
    }

}
