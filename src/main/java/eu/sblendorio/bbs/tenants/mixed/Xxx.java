package eu.sblendorio.bbs.tenants.mixed;

public class Xxx {


    public final static int NUM_COLUMNS = 2;
    public final static int SEPARATING_SPACE_LENGTH =4;

    public static void main(String[] args) {
        list(new String[] {
                "Provacicciomessereunoduetre",
                "Provacorta",
                "To",
                "SEPARATING_SPACE_LENGTHSEPARATING_SPACE_LENGTH",
                "Cortona"
        });
    }


    private static void list(String[] files) {
        int[] maxLength = new int[NUM_COLUMNS];

        for (int i = 0; i < files.length; i++) {
            int fileLength  = files[i].length();
            int columnIndex = i % NUM_COLUMNS;

            if (maxLength[columnIndex] < fileLength) {
                maxLength[columnIndex] = fileLength;
            }
        }


        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            System.out.print(fileName);
            for (int j = 0; j < maxLength[i % NUM_COLUMNS] - fileName.length() + SEPARATING_SPACE_LENGTH; j++) {
                System.out.print(" ");
            }

            if ((i + 1) % NUM_COLUMNS == 0) {
                System.out.println();
            }
        }
    }
}
