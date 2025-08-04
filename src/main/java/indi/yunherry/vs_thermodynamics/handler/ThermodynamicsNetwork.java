package indi.yunherry.vs_thermodynamics.handler;

import indi.yunherry.vs_thermodynamics.VSThermodynamics;
import indi.yunherry.vs_thermodynamics.content.WindDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ThermodynamicsNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(VSThermodynamics.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                WindDataPacket.class,
                WindDataPacket::encode,
                WindDataPacket::decode,
                WindDataPacket::handle
        );
        // 可以在这里继续注册更多 packet
    }
}
