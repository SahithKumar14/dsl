package com.apiGenerator.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtility {

    public static Path zipDirectory(Path sourceDir, String zipFileName) throws Exception {
        Path zipFile = Paths.get(zipFileName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            Files.walk(sourceDir).filter(Files::isRegularFile).forEach(file -> {
                ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(file).toString());
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return zipFile;
    }
}
