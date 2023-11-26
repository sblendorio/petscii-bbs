package eu.sblendorio.bbs.tenants.mixed;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.System.getProperty;

public class GenericClass {

    public static void main(String[] args) throws IOException {
        File img = new File(getProperty("user.home") + File.separator+"Dropbox"+File.separator+"bbs"+File.separator+"wi2.png");
        BufferedImage in = ImageIO.read(img);
        int[][] result = get2DPixelArraySlow(in);
        Set<Integer> m = new HashSet<>();
        for (int i=0; i<result.length; i++) {
            for (int j=0; j<result[i].length; j++) {
                m.add(result[i][j]);
                System.out.print(result[i][j]==-1?"1":"0");
            }
            System.out.println();
        }
        m.forEach(System.out::println);
    }
    public static int[][] get2DPixelArraySlow(BufferedImage sampleImage) {
        int width = sampleImage.getWidth();
        int height = sampleImage.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = sampleImage.getRGB(col, row);
            }
        }

        return result;
    }
}
