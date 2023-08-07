package eu.sblendorio.bbs.core;

public class PrestelControls {
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
    public static byte[] CONCEAL = new byte[] {0x1b, 0x58};
}
