package de.jefa.core.api;

import java.util.List;

public interface ConfigService {

    String getString(String path, String def);
    int getInt(String path, int def);
    double getDouble(String path, double def);
    boolean getBoolean(String path, boolean def);
    List<String> getStringList(String path);

    void reload();
}
