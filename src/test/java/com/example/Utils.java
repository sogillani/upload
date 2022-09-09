package com.example;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static final String CONTRACTS_FILENAME_FILTER = ".*\\.(doc|docx|jpg|pdf|png|txt)";

    public static Path getProjectRootPath() {
        Path rootPath = Path.of(System.getProperty("user.dir"));
        if (!rootPath.getFileName().toString().equals("demo")) {
            rootPath = rootPath.getParent();
        }
        return rootPath;
    }

    public static Path getProjectTargetPath() {
        Path rootPath = getProjectRootPath();
        Path targetPath = Paths.get(rootPath.toString(), "target");
        if (Files.notExists(targetPath)) {
            new File(targetPath.toUri()).mkdirs();
        }
        return targetPath;
    }

    public static List<File> getContracts() {
        Path targetPath = getProjectTargetPath();
        Path contractsPath = Paths.get(targetPath.toString(), "test-data");
        File dir = new File(contractsPath.toUri());
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(CONTRACTS_FILENAME_FILTER);
            }
        });
        return Arrays.asList(files);
    }

    public static List<File> getContracts(int limit) {
        return getContracts().stream().limit(limit).collect(Collectors.toList());
    }
}