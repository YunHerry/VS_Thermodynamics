package indi.yunherry.vs_thermodynamics.content.item;

import indi.yunherry.vs_thermodynamics.utils.ShipUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class DebugStickItem extends Item {
    public DebugStickItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!world.isClientSide && player != null) {
            player.sendSystemMessage(Component.literal("当前质量为: " +  ShipUtils.getShipByPos((ServerLevel) world, pos).getInertiaData().getMass()));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
