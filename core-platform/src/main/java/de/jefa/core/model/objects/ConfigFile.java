package de.jefa.core.model.objects;

public final class ConfigFile {

    private final String fileName;
    private final String prefix;

    public ConfigFile(String fileName, String prefix) {
        this.fileName = fileName;
        this.prefix = prefix;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPrefix() {
        return prefix;
    }
}
