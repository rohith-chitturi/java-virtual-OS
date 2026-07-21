package com.rohith.javavirtualos.kernel.vfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Path {
    private final String pathStr;
    private final boolean isAbsolute;
    private final List<String> segments;

    public static Path of(String path) {
        return new Path(path);
    }

    private Path(String path) {
        this.isAbsolute = path.startsWith("/");
        this.segments = normalize(path);
        
        if (segments.isEmpty() && isAbsolute) {
            this.pathStr = "/";
        } else {
            this.pathStr = (isAbsolute ? "/" : "") + String.join("/", segments);
        }
    }

    private List<String> normalize(String path) {
        List<String> rawSegments = Arrays.stream(path.split("/"))
                                         .filter(s -> !s.isEmpty() && !s.equals("."))
                                         .collect(Collectors.toList());
        List<String> normalized = new ArrayList<>();
        
        for (String segment : rawSegments) {
            if (segment.equals("..")) {
                if (!normalized.isEmpty() && !normalized.get(normalized.size() - 1).equals("..")) {
                    normalized.remove(normalized.size() - 1);
                } else if (!isAbsolute) {
                    normalized.add("..");
                }
            } else {
                normalized.add(segment);
            }
        }
        return normalized;
    }

    public boolean isAbsolute() { return isAbsolute; }
    public List<String> getSegments() { return segments; }
    
    public Path resolve(Path other) {
        if (other.isAbsolute()) {
            return other;
        }
        if (this.pathStr.equals("/")) {
            return Path.of("/" + other.pathStr);
        }
        return Path.of(this.pathStr + "/" + other.pathStr);
    }

    public Path getParent() {
        if (segments.isEmpty()) return null;
        if (segments.size() == 1 && isAbsolute) return Path.of("/");
        List<String> parentSegments = segments.subList(0, segments.size() - 1);
        return Path.of((isAbsolute ? "/" : "") + String.join("/", parentSegments));
    }

    public String getName() {
        if (segments.isEmpty()) return isAbsolute ? "/" : "";
        return segments.get(segments.size() - 1);
    }

    @Override
    public String toString() { return pathStr; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return pathStr.equals(path.pathStr);
    }

    @Override
    public int hashCode() {
        return pathStr.hashCode();
    }
}
