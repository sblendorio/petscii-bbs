package droid64.addons;

import droid64.d64.BasicParser;
import droid64.d64.DiskImage;
import droid64.db.DiskFile;

public class DiskUtilities {
    public static void main(String[] args) throws Exception {
        DiskImage diskImage = DiskImage.getDiskImage("/path/imagefile.d64");
        diskImage.readDirectory();
        for (DiskFile f: diskImage.getDisk().getFileList()) {
            System.out.println(f.getFileNum() + "/" + f.getName() + "/" + f.getFileTypeString());
        }
        byte[] content = diskImage.getFileData(0);
        System.out.println("OK! \n" + BasicParser.parseCbmBasicPrg(content));
    }
}
