package eu.sblendorio.bbs.core;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

import java.util.ArrayList;
import java.util.List;

public class BlockGraphicsPetscii {

    private static int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }

    public static int[] getRenderedMidres(int htab, String[]matrix) throws Exception {
        return getRenderedMidres(htab, matrix, true, false);
    }

    public static int[] getRenderedMidres(int htab, String[]matrix, boolean finalCR, boolean transparent) throws Exception {
        int[][] output = new int[roundUp(matrix.length, 2)][roundUp(matrix[0].length(), 2)];
        for (int y=0; y<matrix.length; ++y)
            for (int x=0; x<matrix[y].length(); ++x)
                if (matrix[y].charAt(x) == '*' || matrix[y].charAt(x) == '1') plotMidres(output, x, y);
        return renderMidres(htab, output, finalCR, transparent);
    }

    private static void plotMidres(int[][] output, int x, int y) {
        int[] pow = {1, 2, 4, 8};
        int macrox = x / 2;
        int macroy = y / 2;
        int microx = 1 - (x % 2);
        int microy = 1 - (y % 2);
        output[macroy][macrox] |= pow[microx + 2*microy];
    }

    private static int[] renderMidres(int htab, int[][] output, boolean finalCR, boolean transparent) {
        int[] decode = {
                transparent ? 29 : 32, 172, 187, 162,
                                    188, -161, -191, -190,
                                    190, 191, 161, -188,
                                    -162, -187, -172, -32
        };
        List<Integer> result = new ArrayList<>();
        boolean reverse = false;
        for (int y=0; y<output.length; ++y) {
            for (int h = 0; h < htab; ++h) result.add(29);
            for (int x=0; x<output[y].length; ++x) {
                int chr = decode[output[y][x]];
                if (chr < 0) {
                    if (!reverse) result.add(18);
                    reverse = true;
                    result.add(-chr);
                } else {
                    if (reverse) result.add(146);
                    reverse = false;
                    result.add(chr);
                }
            }
            if (y < output.length-1 || finalCR) result.add(13);
            reverse = false;
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    public static String[] stringToQr(String string) throws WriterException {
        ByteMatrix matrix = Encoder.encode(string, ErrorCorrectionLevel.H).getMatrix();
        String[] strMatrix = new String[matrix.getHeight()];
        for (int y=0; y < matrix.getHeight(); ++y) {
            strMatrix[y] = "";
            for (int x = 0; x < matrix.getWidth(); ++x) {
                strMatrix[y] += (matrix.get(x, y) == 1 ? "*" : ".");
            }
        }
        return strMatrix;
    }


}
