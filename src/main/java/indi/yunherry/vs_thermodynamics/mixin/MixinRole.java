package indi.yunherry.vs_thermodynamics.mixin;

//import net.minecraft.server.level.ServerLevel;
//import net.spaceeye.vmod.vEntityManaging.types.constraints.RopeConstraint;
//import net.spaceeye.vmod.vEntityManaging.util.TwoShipsMConstraint;
//import net.spaceeye.vmod.vEntityManaging.util.VEAutoSerializable;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(RopeConstraint.class)
//public abstract class MixinRole  {
//    @Inject(method = "iOnMakeVEntity",at=@At("HEAD"),remap = false)
//    public void iOnMakeVEntity(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("iOnMakeVEntity: " + getShipId1() + "  : " + getShipId2());
//    }
//
//    @Shadow public abstract long getShipId1();
//
//    @Shadow public abstract long getShipId2();
//}
