package eu.sblendorio.bbs.core;

public class PrestelControls {
    public static byte CLS = 12;
    public static byte BACKSPACE_KEY = 8;
    public static byte CURSOR_LEFT = 8;
    public static byte CURSOR_RIGHT = 9;
    public static byte CURSOR_DOWN = 10;
    public static byte CURSOR_UP = 11;
    public static byte CLEAR_SCREEN = 12;
    public static byte CR = 13;
    public static byte LF = 10;
    public static byte[] CRLF = new byte[] {13, 10};
    public static byte CURSOR_ON = 17;
    public static byte CURSOR_OFF = 20;
    public static byte HOME = 30;
    public static byte BEEP = 7;
    public static byte ESC = 27;
    public static byte[] CONCEAL = new byte[] {0x1b, 0x58};

    public static final byte[] CHAR_RED = new byte[] {0x1b, 0x41};
    public static final byte[] CHAR_GREEN = new byte[] {0x1b, 0x42};
    public static final byte[] CHAR_YELLOW = new byte[] {0x1b, 0x43};
    public static final byte[] CHAR_BLUE = new byte[] {0x1b, 0x44};
    public static final byte[] CHAR_MAGENTA = new byte[] {0x1b, 0x45};
    public static final byte[] CHAR_CYAN = new byte[] {0x1b, 0x46};
    public static final byte[] CHAR_WHITE = new byte[] {0x1b, 0x47};

    public static final byte[] BACKGROUND_BLACK = new byte[] {92};
    public static final byte[] BACKGROUND_RED = new byte[] {0x1b, 0x41, 92};
    public static final byte[] BACKGROUND_GREEN = new byte[] {0x1b, 0x42, 92};
    public static final byte[] BACKGROUND_YELLOW = new byte[] {0x1b, 0x43, 92};
    public static final byte[] BACKGROUND_BLUE = new byte[] {0x1b, 0x44, 92};
    public static final byte[] BACKGROUND_MAGENTA = new byte[] {0x1b, 0x45, 92};
    public static final byte[] BACKGROUND_CYAN = new byte[] {0x1b, 0x46, 92};
    public static final byte[] BACKGROUND_WHITE = new byte[] {0x1b, 0x47, 92};

}
