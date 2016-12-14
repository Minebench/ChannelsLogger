package de.minebench.channelslogger;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/*
 * Copyright 2016 Max Lee (https://github.com/Phoenix616/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 *
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */

public class FileConfiguration {
    protected final static ConfigurationProvider yml = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Plugin plugin;

    private Configuration defaults;
    private Configuration config;
    private File configFile;

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param path The path to the yml file with the plugin's datafolder as the parent
     * @throws IOException
     */
    public FileConfiguration(Plugin plugin, String path) throws IOException {
        this(plugin, new File(plugin.getDataFolder(), path));
    }

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param configFile The yml file
     * @throws IOException
     */
    public FileConfiguration(Plugin plugin, File configFile) throws IOException {
        this.plugin = plugin;
        loadConfig(configFile);
    }

    /**
     * Load a file into this config
     * @param configFile The yml file
     * @return <tt>true</tt> if it was successfully loaded, <tt>false</tt> if not
     */
    public boolean loadConfig(File configFile) throws IOException {
        this.configFile = configFile;
        defaults = yml.load(new InputStreamReader(plugin.getResourceAsStream(configFile.getName())));

        if(configFile.exists()) {
            config = yml.load(configFile);
            return true;
        } else if(configFile.getParentFile().exists() || configFile.getParentFile().mkdirs()) {
            return createDefaultConfig();
        }
        return false;
    }

    /**
     * Saves the config into the yml file on the disc
     * @return <tt>true</tt> if it was saved; <tt>false</tt> if an error occurred
     */
    public boolean saveConfig() {
        try {
            yml.save(config, configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save configuration to " + configFile.getAbsolutePath());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Copy the default config from the plugin jar into its path
     * @return <tt>true</tt> if it was successfully created, <tt>false</tt> if it already existed
     */
    public boolean createDefaultConfig() throws IOException {
        if(configFile.createNewFile()) {
            config = defaults;
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Delete the file of this config from the disc
     * @return <tt>true</tt> if it was successfully deleted; <tt>false</tt> otherwise
     */
    public boolean removeConfig() {
        return configFile.delete();
    }

    public Configuration getConfiguration() {
        return config;
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public Configuration getSection(String path) {
        return config.getSection(path);
    }

    public Configuration getDefaults() {
        return defaults;
    }

    public boolean isSet(String path) {
        return config.get(path) != null;
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }
}