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
    public static ThermodynamicsConfigData CONFIG = new ThermodynamicsConfigData();
}
