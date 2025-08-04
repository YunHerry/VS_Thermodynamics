package indi.yunherry.vs_thermodynamics.content;

import indi.yunherry.vs_thermodynamics.WorldContext;
import indi.yunherry.vs_thermodynamics.handler.WindDirectionType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;

import java.util.function.Supplier;

public class WindDataPacket {
    //对流层方向
    public static WindDirectionType windDirection = WindDirectionType.NONE;
    //对流层低空大气
    public static Vector3d windVec = new Vector3d();
    //平流层高空稳定大气
    public static Vector3d windVec1 = new Vector3d();

    public WindDataPacket(WindDirectionType windDirection,Vector3d windVec,Vector3d windVec1) {
        this.windDirection = windDirection;
        this.windVec = windVec;
        this.windVec1 = windVec1;
    }

    public static void encode(WindDataPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.windDirection);
        buf.writeDouble(msg.windVec.x);
        buf.writeDouble(msg.windVec.y);
        buf.writeDouble(msg.windVec.z);
        buf.writeDouble(msg.windVec1.x);
        buf.writeDouble(msg.windVec1.y);
        buf.writeDouble(msg.windVec1.z);
    }

    public static WindDataPacket decode(FriendlyByteBuf buf) {
        WindDirectionType direction = buf.readEnum(WindDirectionType.class);
        Vector3d vec = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vector3d vec1 = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new WindDataPacket(direction, vec, vec1);
    }

    public static void handle(WindDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 客户端收到后处理逻辑（设置到缓存类）
            WorldContext.windDirection = msg.windDirection;
            WorldContext.windVec = msg.windVec;
            WorldContext.windVec1 = msg.windVec1;
        });
        ctx.get().setPacketHandled(true);
    }
}
