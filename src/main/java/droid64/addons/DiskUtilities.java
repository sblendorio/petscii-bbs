package droid64.addons;
import droid64.d64.BasicParser;
import droid64.d64.CbmException;
import droid64.d64.DiskImage;
import droid64.db.DiskFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static eu.sblendorio.bbs.core.PetsciiThread.*;

public class DiskUtilities {

    public static void main(String[] args) throws Exception {
        String url = "ftp://arnold.c64.org/pub/games/f/Freds_Back.Markt_und_Technik.+2-SCS.zip";

        byte[] bytes = getPrgContent(url);
        if (bytes == null) {
            System.out.println("INVALID");
            System.exit(1);
        }
        Path path = Paths.get("/tmp/zfile1");
        Files.write(path, bytes);

    }
    public static byte[] getPrgContent(String urlString) throws Exception {
        String url = "ftp://arnold.c64.org/pub/games/f/Freds_Back.Markt_und_Technik.+2-SCS.zip";
        byte[] result = null;

        DownloadData file = download(new URL(urlString));
        if (isValidZip(file.getContent())) file = singleFileInZip(file.getContent());
        if (file != null && isValidFilename(file.getFilename())) {
            if (isPRG(file.getFilename())) {
                result = file.getContent();
            } else if (isT64(file.getFilename())) {
                result = singleFileInArchive(file, true).getContent();
            } else { // is a disk
                result = singleFileInArchive(file, false).getContent();
            }
        } else {
            result = null;
        }

        return result;
    }

    public static boolean isValidZip(byte[] content) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            return zis.getNextEntry() != null;
        } catch (ZipException e) {
            return false;
        }
    }

    public static List<String> getZipEntries(byte[] content) throws IOException {
        List<String> result = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) result.add(entry.getName());
        return result;
    }

    public static boolean isValidFilename(String filename) {
        return filename.matches("(?is)^.*\\.(prg|d64|d71|d81|d82|t64)$");
    }

    public static boolean isPRG(String filename) {
        return filename.matches("(?is)^.*\\.prg$");
    }

    public static boolean isT64(String filename) {
        return filename.matches("(?is)^.*\\.t64$");
    }

    public static DownloadData singleFileInArchive(DownloadData file, boolean isT64) throws IOException {
        DiskImage diskImage;
        int count = 0;
        int num = 0;
        String filename = "";
        try {
            diskImage = DiskImage.getDiskImage(file.getFilename(), file.getContent());
            for (DiskFile f: diskImage.getDisk().getFileList()) {
                if (isT64 || isPRG(f.getName())) {
                    ++count;
                    num = f.getFileNum();
                    filename = f.getName();
                }
            }
            return count==1 ? new DownloadData(filename, diskImage.getFileData(num)) : null;

        } catch (CbmException e) {
            return null;
        }
    }

    public static DownloadData singleFileInZip(byte[] content) throws IOException {
        List<String> fileList = getZipEntries(content);
        int count = 0;
        String filename = null;
        for (String file: fileList) {
            if (file.matches("(?is)^.*\\.(prg|d64|d71|d81|d82|t64)$")) {
                ++count;
                filename = file;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        if (count == 1) {
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content));
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (entry.getName().equals(filename)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) baos.write(buffer, 0, len);
                    baos.close();
                    break;
                }
                entry = zis.getNextEntry();
            }
        }

        return count==1 ? new DownloadData(filename, baos.toByteArray()) : null;
    }

}
