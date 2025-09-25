package indi.yunherry.vs_thermodynamics.content;

import indi.yunherry.vs_thermodynamics.config.ConfigManager;
import indi.yunherry.vs_thermodynamics.config.ThermodynamicsConfigData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ConfigSyncPacket {
    private static final Logger log = LoggerFactory.getLogger(ConfigSyncPacket.class);
    private final long gift;
    private final int maxAirBlock;

    // 构造函数 - 用于发送
    public ConfigSyncPacket(ThermodynamicsConfigData config) {
        this.gift = config.gift;
        this.maxAirBlock = config.maxAirBlock;
    }

    // 构造函数 - 用于接收
    public ConfigSyncPacket(long gift, int maxAirBlock) {
        this.gift = gift;
        this.maxAirBlock = maxAirBlock;
    }

    // 编码到网络
    public static void encode(ConfigSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.gift);
        buffer.writeInt(packet.maxAirBlock);
    }

    // 从网络解码
    public static ConfigSyncPacket decode(FriendlyByteBuf buffer) {
        long gift = buffer.readLong();
        int maxAirBlock = buffer.readInt();
        return new ConfigSyncPacket(gift, maxAirBlock);
    }

    // 处理数据包
    public static void handle(ConfigSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 确保只在客户端处理
            if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                handleOnClient(packet);
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOnClient(ConfigSyncPacket packet) {
        ConfigManager.CONFIG = new ThermodynamicsConfigData(packet.gift,packet.maxAirBlock);
        log.info("Received server config: gift={}, maxAirBlock={}",
                packet.gift, packet.maxAirBlock);
    }

    public long getGift() { return gift; }
    public int getMaxAirBlock() { return maxAirBlock; }
}
