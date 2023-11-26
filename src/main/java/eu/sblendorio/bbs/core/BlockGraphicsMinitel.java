package eu.sblendorio.bbs.core;

import java.util.ArrayList;
import java.util.List;

public class BlockGraphicsMinitel {

    private static int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }

    public static int[] getRenderedMidres(int htab, String[]matrix) throws Exception {
        return getRenderedMidres(htab, matrix, true, false);
    }

    public static int[] getRenderedMidres(int htab, String[]matrix, boolean finalCR, boolean transparent) throws Exception {
        int[][] output = new int[roundUp(matrix.length, 3)][roundUp(matrix[0].length(), 2)];
        for (int y=0; y<matrix.length; ++y)
            for (int x=0; x<matrix[y].length(); ++x)
                if (matrix[y].charAt(x) == '*' || matrix[y].charAt(x) == '1') plotMidres(output, x, y);
        return renderMidres(htab, output, finalCR, transparent);
    }

    private static void plotMidres(int[][] output, int x, int y) {
        int[] pow = {1, 2, 4, 8, 16, 32};
        int macrox = x / 2;
        int macroy = y / 3;
        int microx = 1 - (x % 2);
        int microy = 2 - (y % 3);
        output[macroy][macrox] |= pow[microx + 2*microy];
    }

    private static int[] renderMidres(int htab, int[][] output, boolean finalCR, boolean transparent) {
        int empty = graphic(0);
        List<Integer> result = new ArrayList<>();
        for (int y=0; y<output.length; ++y) {
            for (int h = 0; h < htab; ++h) result.add(9);
            for (int x=0; x<output[y].length; ++x) {
                int chr = graphic(output[y][x]);
                if (chr == empty && transparent) chr = 9;
                result.add(chr);
            }
            if (y < output.length-1 || finalCR) {
                result.add(10);
                result.add(13);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    private static int[] pow = new int[] {1,2,4,8,16,32,64,128};
    private static int bitRead(int x, int n) {
        return (x & pow[n]) == 0 ? 0 : 1;
    }

    private static int graphic(int b) {
        b = 0x20
                + bitRead(b,5)
                + bitRead(b,4) * 2
                + bitRead(b,3) * 4
                + bitRead(b,2) * 8
                + bitRead(b,1) * 16
                + bitRead(b,0) * 64;
        if (b == 0x7F) {  // 0b1111111
            b= 0x5F;
        }
        return b;
    }

}
