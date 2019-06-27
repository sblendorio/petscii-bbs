package droid64.addons;
import droid64.d64.BasicParser;
import droid64.d64.CbmException;
import droid64.d64.DiskImage;
import droid64.db.DiskFile;

import java.net.URL;
import java.util.Arrays;

import static eu.sblendorio.bbs.core.PetsciiThread.*;

public class DiskUtilities {
    public static void main(String[] args) throws Exception {
        //DiskImage diskImage = DiskImage.getDiskImage("/Users/francesco.sblendorio/Dropbox/emul/C64/best1.d64");
        //DownloadData file = download(new URL("https://csdb.dk/release/download.php?id=214492"));
        DownloadData file = download(new URL("http://arnold.c64.org/pub/games/f/Freds_Back.Markt_und_Technik.+2-SCS.zip"));
        System.out.println("NAME = "+file.getFilename());
        DiskImage diskImage;
        try {
            diskImage = DiskImage.getDiskImage("/Users/francesco.sblendorio/Downloads/FEARSOFD.T64");
            //diskImage = DiskImage.getDiskImage("/Users/francesco.sblendorio/Dropbox/emul/C64/best1.d64");
        } catch (CbmException e) {
            System.out.println("NOT A DISK IMAGE");
            throw e;
        }
        //System.out.println("1) i="+diskImage.getDisk().getImageType());
        diskImage.readDirectory();
        System.out.println("TYPE DISK="+ diskImage.getImageTypeNames()[diskImage.checkImageFormat()]);
        for (DiskFile f: diskImage.getDisk().getFileList()) {
            System.out.println(f.getFileNum() + "/" + f.getName() + "/" + f.getFileTypeString());
        }
        byte[] content = diskImage.getFileData(0);
        //System.out.println("OK! \n" + BasicParser.parseCbmBasicPrg(content));
    }





}
