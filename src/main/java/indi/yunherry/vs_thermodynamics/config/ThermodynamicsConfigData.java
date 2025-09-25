package indi.yunherry.vs_thermodynamics.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import static indi.yunherry.vs_thermodynamics.VSThermodynamics.MODID;

@Config(name = MODID)
public class ThermodynamicsConfigData implements ConfigData {
    public Long gift = 10000L;
    public Integer maxAirBlock = 600;

    public ThermodynamicsConfigData() {
    }

    public ThermodynamicsConfigData(Long gift, Integer maxAirBlock) {
        this.gift = gift;
        this.maxAirBlock = maxAirBlock;
    }
}
