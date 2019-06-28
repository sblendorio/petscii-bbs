package droid64.addons;
import droid64.d64.CbmException;
import droid64.d64.CbmFile;
import droid64.d64.DiskImage;
import droid64.db.DiskFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import static eu.sblendorio.bbs.core.PetsciiThread.*;

public class DiskUtilities {

    /* This main is for testing purposes only */
    public static void main(String[] args) throws Exception {
        String url = "ftp://arnold.c64.org/pub/games/f/Freds_Back.Markt_und_Technik.+2-SCS.zip";

        byte[] bytes = getPrgContent(url);
        if (bytes == null) {
            System.out.println("INVALID");
            System.exit(1);
        }
        Path path = Paths.get("/tmp/sample.prg");
        Files.write(path, bytes);
        System.out.println("DONE!");
    }

    public static byte[] getPrgContent(String urlString) throws Exception {
        byte[] result = null;

        DownloadData file = download(new URL(urlString));
        if (isValidZip(file.getContent()))
            file = singleFileInZip(file.getContent());
        if (file != null && isValidFilename(file.getFilename())) {
            if (isPRG(file.getFilename())) {
                result = file.getContent();
            } else if (isT64(file.getFilename())) {
                file = singleFileInArchive(file, true);
                result = file != null ? file.getContent() : null;
            } else { // is a disk
                file = singleFileInArchive(file, false);
                result = file != null ? file.getContent() : null;
            }
        } else {
            result = null;
        }
        return result;
    }

    private static boolean isValidZip(byte[] content) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            return zis.getNextEntry() != null;
        } catch (ZipException e) {
            return false;
        }
    }

    private static List<String> getZipEntries(byte[] content) throws IOException {
        List<String> result = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) result.add(entry.getName());
        return result;
    }

    private static boolean isValidFilename(String filename) {
        return filename.matches("(?is)^.*\\.(prg|d64|d71|d81|d82|t64|d64\\.gz|d71\\.gz|d81\\.gz|d82\\.gz|t64\\.gz)$");
    }

    private static boolean isPRG(String filename) {
        return filename.matches("(?is)^.*\\.prg$");
    }

    private static boolean isT64(String filename) {
        return filename.matches("(?is)^.*\\.(t64|t64\\.gz)$");
    }

    private static DownloadData singleFileInArchive(DownloadData file, boolean isT64) throws IOException {
        DiskImage diskImage;
        int count = 0;
        int num = 0;
        String filename = "";
        try {
            diskImage = DiskImage.getDiskImage(file.getFilename(), file.getContent());
            diskImage.readDirectory();
            for (DiskFile f: diskImage.getDisk().getFileList()) {
                if (isT64 || f.getFileType() == CbmFile.TYPE_PRG) {
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

    private static DownloadData singleFileInZip(byte[] content) throws IOException {
        List<String> fileList = getZipEntries(content);
        int count = 0;
        String filename = null;
        for (String file: fileList) {
            if (file.matches("(?is)^.*\\.(prg|d64|d71|d81|d82|t64|d64\\.gz|d71\\.gz|d81\\.gz|d82\\.gz|t64\\.gz)$") &&
                !file.matches("^.*/\\.[^/]+?$")) {
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
