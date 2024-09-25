package org.foden.utils;

import org.foden.pages.BasePage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Objects;

public class FileDirectoryUtils {

    public static void cleanDirectory(String path) {
        Path dirPath = Paths.get(path);
        if (Files.exists(dirPath)) {
            try {
                Files.walk(dirPath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(file -> {
                            try {
                                Files.delete(file);
                            } catch (IOException e) {
                                Log4jUtils.error(e.getMessage());
                            }
                        });
            } catch (IOException e) {
                Log4jUtils.error(e.getMessage());
            }
        }
    }

    public static void copyDirectory(String srcPath, String destPath) {
        Path src = Paths.get(srcPath);
        Path dest = Paths.get(destPath);

        try {
            Files.walk(src)
                    .forEach(source -> {
                        Path destination = dest.resolve(src.relativize(source));
                        try {
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            Log4jUtils.error(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            Log4jUtils.error(e.getMessage());
        }
    }

    public static String getPathDownloadDirectory() {
        String path = "";
        String machine_name = System.getProperty("user.home");
        path = machine_name + File.separator + "Downloads";
        return path;
    }

    public static int countFilesInDownloadDirectory() {
        String pathFolderDownload = getPathDownloadDirectory();
        File file = new File(pathFolderDownload);
        int i = 0;
        for (File listOfFiles : Objects.requireNonNull(file.listFiles())) {
            if (listOfFiles.isFile()) {
                i++;
            }
        }
        return i;
    }

    public static boolean verifyFileContainsInDownloadDirectory(String fileName) {
        boolean flag = false;
        try {
            String pathFolderDownload = getPathDownloadDirectory();
            File dir = new File(pathFolderDownload);
            File[] files = dir.listFiles();
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                if (files[i].getName().contains(fileName)) {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return flag;
        }
    }

    public static boolean verifyFileEqualsInDownloadDirectory(String fileName) {
        boolean flag = false;
        try {
            String pathFolderDownload = getPathDownloadDirectory();
            File dir = new File(pathFolderDownload);
            File[] files = dir.listFiles();
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                if (files[i].getName().equals(fileName)) {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            System.out.println(e.getMessage());;
            return flag;
        }
    }

    public static boolean verifyDownloadFileContainsName(String fileName, int timeoutSeconds) {
        boolean check = false;
        int i = 0;
        while (i < timeoutSeconds) {
            boolean exist = verifyFileContainsInDownloadDirectory(fileName);
            if (exist) {
                i = timeoutSeconds;
                return check = true;
            }
            BasePage.sleep(1);
            i++;
        }
        return check;
    }

    public static boolean verifyDownloadFileEqualsName(String fileName, int timeoutSeconds) {
        boolean check = false;
        int i = 0;
        while (i < timeoutSeconds) {
            boolean exist = verifyFileEqualsInDownloadDirectory(fileName);
            if (exist == true) {
                i = timeoutSeconds;
                return check = true;
            }
            BasePage.sleep(1);
            i++;
        }
        return check;
    }

    public static void deleteAllFileInDownloadDirectory() {
        try {
            String pathFolderDownload = getPathDownloadDirectory();
            File file = new File(pathFolderDownload);
            File[] listOfFiles = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
                if (listOfFiles[i].isFile()) {
                    new File(listOfFiles[i].toString()).delete();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteAllFileInDirectory(String pathDirectory) {
        try {
            File file = new File(pathDirectory);
            File[] listOfFiles = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
                if (listOfFiles[i].isFile()) {
                    new File(listOfFiles[i].toString()).delete();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getDownloadPath(){
        String osName = System.getProperty("os.name");
        String downloadPath= System.getProperty("user.dir")+File.separator+"downloaded"+File.separator;
        if(osName.equalsIgnoreCase("linux"))
            downloadPath=File.separator+"home"+File.separator+"ubuntu"+File.separator+"downloads"+File.separator+"seluser"+File.separator+"Downloads"+File.separator;
        return downloadPath;
    }

}
