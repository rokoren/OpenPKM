package com.github.felipeucelli.nodejswrapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;

public class Downloader {

    private static final OkHttpClient client = new OkHttpClient();

    public static Path downloadIfNeeded() throws IOException {
        String platform = detectPlatform();
        Path cacheDir = Paths.get(System.getProperty("user.home"), ".nodejswrapper", platform);
        String bin = "node";
        String suffix = "";
        if (platform.equals("win_x64")){
            suffix = ".exe";
        }
        Path nodePath = cacheDir.resolve(bin + suffix);

        if (Files.exists(nodePath)) return nodePath;

        Files.createDirectories(cacheDir);
        String url = String.format(
                "https://github.com/felipeucelli/nodejs_wrapper/releases/download/binaries/node_%s%s.tar.gz",
                 platform, suffix
        );

        Path tarGz = cacheDir.resolve(bin + "_" + platform + suffix + ".tar.gz");
        Request request = new Request.Builder().url(url).build();
        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) throw new IOException("Download failed: " + resp);
            assert resp.body() != null;
            Files.copy(resp.body().byteStream(), tarGz, StandardCopyOption.REPLACE_EXISTING);
        }

        try (InputStream fi = Files.newInputStream(tarGz);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(fi);
             TarArchiveInputStream tar = new TarArchiveInputStream(gzi)) {

            TarArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    Files.copy(tar, nodePath, StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
            }
        }

        try {
            Files.setPosixFilePermissions(nodePath,
                    PosixFilePermissions.fromString("rwxr-xr-x"));
        } catch (UnsupportedOperationException ignored) {

        }

        return nodePath;
    }

    private static String detectPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        if (os.contains("win")) return "win_x64";
        if (os.contains("mac"))
            return arch.contains("aarch64") || arch.contains("arm") ? "darwin_arm64" : "darwin_x64";
        if (os.contains("nux"))
            return arch.contains("aarch64") || arch.contains("arm") ? "linux_arm64" : "linux_x64";
        throw new RuntimeException("Unsupported platform: " + os + " " + arch);
    }
}
