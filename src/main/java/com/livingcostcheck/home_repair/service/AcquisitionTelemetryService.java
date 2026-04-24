package com.livingcostcheck.home_repair.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AcquisitionTelemetryService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Pattern UNSAFE_SURFACE_CODE_CHARS = Pattern.compile("[^a-z0-9_]+");
    private static final Pattern MULTI_UNDERSCORE = Pattern.compile("_+");

    private final Path telemetryPath;

    public AcquisitionTelemetryService(
            @Value("${app.acquisition.telemetry-path:data/acquisition-events.csv}") String telemetryPath) {
        this.telemetryPath = Paths.get(telemetryPath);
    }

    public void recordSurfaceView(String surfaceCode, String path) {
        appendEvent(normalizeSurfaceCode(surfaceCode), "SURFACE_VIEW", sanitizePath(path));
    }

    public void recordToolOpen(String surfaceCode, String path) {
        appendEvent(normalizeSurfaceCode(surfaceCode), "TOOL_OPEN", sanitizePath(path));
    }

    public List<TelemetryEvent> readSince(LocalDateTime cutoff) {
        if (!Files.exists(telemetryPath)) {
            return List.of();
        }

        try {
            List<String> lines = Files.readAllLines(telemetryPath);
            List<TelemetryEvent> events = new ArrayList<>();
            for (int index = 1; index < lines.size(); index++) {
                TelemetryEvent event = parseLine(lines.get(index));
                if (event != null && !event.createdAt().isBefore(cutoff)) {
                    events.add(event);
                }
            }
            return events;
        } catch (IOException e) {
            log.error("Failed to read acquisition telemetry", e);
            return List.of();
        }
    }

    private synchronized void appendEvent(String surfaceCode, String stage, String path) {
        try {
            Files.createDirectories(telemetryPath.getParent());
            boolean isNew = !Files.exists(telemetryPath);
            String line = String.join(",",
                    LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                    surfaceCode,
                    stage,
                    path);
            if (isNew) {
                Files.writeString(telemetryPath, "timestamp,surface,stage,path\n" + line + "\n");
            } else {
                Files.writeString(telemetryPath, line + "\n", java.nio.file.StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            log.error("Failed to append acquisition telemetry", e);
        }
    }

    private TelemetryEvent parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split(",", 4);
        if (parts.length < 4) {
            return null;
        }
        try {
            return new TelemetryEvent(
                    LocalDateTime.parse(parts[0], TIMESTAMP_FORMATTER),
                    normalizeSurfaceCode(parts[1]),
                    parts[2].trim().toUpperCase(Locale.ENGLISH),
                    sanitizePath(parts[3]));
        } catch (Exception e) {
            log.warn("Skipping malformed acquisition telemetry row: {}", line);
            return null;
        }
    }

    private String normalizeSurfaceCode(String rawSurfaceCode) {
        if (rawSurfaceCode == null || rawSurfaceCode.isBlank()) {
            return "direct";
        }
        String normalized = rawSurfaceCode.trim().toLowerCase(Locale.ENGLISH).replace('-', '_');
        normalized = UNSAFE_SURFACE_CODE_CHARS.matcher(normalized).replaceAll("_");
        normalized = MULTI_UNDERSCORE.matcher(normalized).replaceAll("_");
        normalized = normalized.replaceAll("^_+|_+$", "");
        if (normalized.isBlank()) {
            return "direct";
        }
        return normalized.length() > 80 ? normalized.substring(0, 80) : normalized;
    }

    private String sanitizePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "/";
        }
        String sanitized = rawPath.trim().replace(',', '_');
        return sanitized.length() > 160 ? sanitized.substring(0, 160) : sanitized;
    }

    public record TelemetryEvent(
            LocalDateTime createdAt,
            String surfaceCode,
            String stage,
            String path) {
    }
}
