package indi.yunherry.vs_thermodynamics.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static indi.yunherry.vs_thermodynamics.VSThermodynamics.MODID;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
    private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get();
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve(MODID + "-config.toml");
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    public static ThermodynamicsConfigData CONFIG = new ThermodynamicsConfigData();

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                String tomlContent = Files.readString(CONFIG_FILE, StandardCharsets.UTF_8);
                parseTomlToConfig(tomlContent);
                log.info("TOML config loaded successfully");
            } else {
                log.warn("TOML config file not found, creating default");
                saveConfig();
            }
        } catch (IOException e) {
            log.error("Error loading TOML config: {}", e.getMessage());
            e.printStackTrace();
            CONFIG = new ThermodynamicsConfigData();
            saveConfig();
        }
    }

    /**
     * 保存配置文件为 TOML 格式
     */
    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_DIR);
            String tomlContent = generateTomlContent();
            Files.writeString(CONFIG_FILE, tomlContent, StandardCharsets.UTF_8);
            log.info("TOML config saved successfully");
        } catch (IOException e) {
            log.error("Error saving TOML config: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解析 TOML 内容到配置对象
     */
    private static void parseTomlToConfig(String tomlContent) {
        try {
            // 简单的 TOML 解析器实现
            String[] lines = tomlContent.split("\n");
            String currentSection = "";

            for (String line : lines) {
                line = line.trim();

                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // 处理节 [section]
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    continue;
                }

                // 处理键值对
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    setConfigValue(currentSection, key, value);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing TOML config: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 设置配置值
     */
    private static void setConfigValue(String section, String key, String value) {
        try {
            // 移除引号
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            switch (section) {
                case "general":
                    setGeneralConfig(key, value);
                    break;
                case "":
                    // 根级别配置
                    setGeneralConfig(key, value);
                    break;
            }
        } catch (Exception e) {
            log.error("Error setting config value {}: {}", key, e.getMessage());
        }
    }

    private static void setGeneralConfig(String key, String value) {
        if (key.equals("gift")) {
            CONFIG.gift = Long.parseLong(value);
        }
    }


    /**
     * 生成 TOML 内容
     */
    private static String generateTomlContent() {
        StringBuilder toml = new StringBuilder();

        // 文件头注释
        toml.append("# Configuration file for ").append(MODID).append("\n");
        toml.append("# Generated automatically, but safe to edit manually\n\n");

        // [general] 部分
        toml.append("[general]\n");
        toml.append("# Maximum energy capacity (1000-100000)\n");
        toml.append("gift = ").append(CONFIG.gift).append("\n");

        return toml.toString();
    }

    /**
     * 重载配置文件
     */
    public static void reloadConfig() {
        loadConfig();
        onConfigChanged();
    }

    /**
     * 配置变更时的回调
     */
    private static void onConfigChanged() {
        System.out.println("[" + MODID + "] TOML config has been changed and reloaded");
    }

    /**
     * 验证配置值的有效性
     */
    public static void validateConfig() {
        boolean changed = false;

        // 如果配置被修正，保存文件
        if (changed) {
            System.out.println("[" + MODID + "] Some TOML config values were corrected");
            saveConfig();
        }
    }
}
