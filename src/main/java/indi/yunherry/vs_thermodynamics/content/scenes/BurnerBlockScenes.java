package indi.yunherry.vs_thermodynamics.content.scenes;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import indi.yunherry.vs_thermodynamics.Blocks;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BurnerBlockScenes {
    public static void register(PonderRegistrationHelper helper) {
        helper.forComponents(Blocks.BURNER).addStoryBoard("burner_ponder", BurnerBlockScenes::burner);
        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_SOURCES)
                .add(Blocks.BURNER);
    }

    public static void burner(SceneBuilder scene, SceneBuildingUtil util) {

        scene.title("burner_usage", "How to use The burner");

        scene.configureBasePlate(0, 0, 10);
        scene.scaleSceneView(.5f);
        scene.showBasePlate();
        scene.rotateCameraY(90);

        // 设置摄像机
//        scene.scaleSceneView(0.75f);
//        scene.rotateScene(20, 0);

//        // 模拟放置一个方块
        scene.idle(20);
        Selection tank = util.select.position(4, 1, 3);
        scene.world.showSection(tank, Direction.DOWN);
        scene.idle(10);
        Selection pump = util.select.position(4, 2, 3);
        scene.world.showSection(pump, Direction.DOWN);
        scene.idle(10);
        Selection controller = util.select.position(4, 3, 3);
        scene.world.showSection(controller, Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(60) // 持续时间（tick）
                .text("") // 显示的文本
                .attachKeyFrame()
                .pointAt(util.vector.topOf(4, 3, 3)) // 指向的方块（相对 center 坐标）
                .colored(PonderPalette.BLUE); // 颜色
        scene.idle(70);
        scene.overlay.showText(60) // 持续时间（tick）
                .text("") // 显示的文本
                .attachKeyFrame()
                .pointAt(util.vector.topOf(4, 3, 2)) // 指向的方块（相对 center 坐标）
                .colored(PonderPalette.BLUE); // 颜色
        Selection handCrank = util.select.position(4, 3, 2);
        scene.world.showSection(handCrank, Direction.DOWN);
        scene.world.modifyBlockEntity(util.grid.at(4, 3, 3), BurnerBlockEntity.class, be -> {
            be.valveState = 0;
        });
        FluidStack content = new FluidStack(Fluids.LAVA.getSource(), 8000);
        scene.world.modifyBlockEntity(util.grid.at(4, 1, 3), FluidTankBlockEntity.class, be -> be.getTankInventory()
                .fill(content, IFluidHandler.FluidAction.EXECUTE));
//        scene.world.interpolateTankLevel(new BlockPos(0, 1, 0), Fluids.LAVA, 4000, 40);
        scene.overlay.showOutline(PonderPalette.BLUE, "highlight_key",
                util.select.position(4, 3, 2), 30);
        scene.world.modifyBlockEntity(util.grid.at(4, 3, 2), HandCrankBlockEntity.class, crank -> {
            crank.setSpeed(30);
        });
        scene.world.modifyBlockEntity(util.grid.at(4, 3, 3), BurnerBlockEntity.class, be -> {
            be.valveState = 1;
        });
        scene.idle(60);
        scene.world.showSection(util.select.everywhere(), Direction.UP);

        scene.overlay.showText(60) // 持续时间（tick）
                .text("") // 显示的文本
                .attachKeyFrame()
                .pointAt(util.vector.topOf(4, 3, 2)) // 指向的方块（相对 center 坐标）
                .colored(PonderPalette.BLUE); // 颜色
        scene.world.modifyBlockEntity(util.grid.at(4, 3, 2), HandCrankBlockEntity.class, crank -> {
            crank.setSpeed(0);
        });
        scene.rotateCameraY(-45);//3, 4, 1
        scene.world.setBlock(new BlockPos(5, 2, 0), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), false);
        int x1 = 5, y1 = 5, z1 = 1;
        int x2 = 3, y2 = 4, z2 = 1;

        // 计算最小/最大值，确保支持反向坐标
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = util.grid.at(x, y, z);
                    scene.world.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), false);
                }
            }
        }
        Selection area = util.select.fromTo(util.grid.at(3, 4, 2), util.grid.at(5, 5, 4));

        scene.overlay.showOutline(PonderPalette.BLUE, "highlight_key",
                area, 60);
        scene.idle(60);
        scene.overlay.showText(60) // 持续时间（tick）
                .text("") // 显示的文本
                .attachKeyFrame()
                .pointAt(util.vector.topOf(4, 3, 2)) // 指向的方块（相对 center 坐标）
                .colored(PonderPalette.BLUE); // 颜色
        scene.idle(60);
    }
}
