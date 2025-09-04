package de.jefa.core.api;

import java.util.List;
import java.util.Set;

public interface ConfigService extends AutoCloseable{

    String getString(String path, String def);
    int getInt(String path, int def);
    double getDouble(String path, double def);
    boolean getBoolean(String path, boolean def);
    List<String> getStringList(String path);

    boolean contains(String path);
    Set<String> getKeys(String prefix, boolean deep);

    void reload();

    @Override
    void close();
}
