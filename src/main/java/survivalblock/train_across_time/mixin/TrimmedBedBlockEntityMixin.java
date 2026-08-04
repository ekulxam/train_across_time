package survivalblock.train_across_time.mixin;

import dev.doctor4t.wathe.api.event.CanSeePoison;
import dev.doctor4t.wathe.block_entity.TrimmedBedBlockEntity;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.index.WatheParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO
@Environment(EnvType.CLIENT)
@SuppressWarnings({"InvalidInjectorMethodSignature", "DataFlowIssue"})
@Mixin(TrimmedBedBlockEntity.class)
public class TrimmedBedBlockEntityMixin {
    @Inject(
            method = "clientTick",
            at = @At("HEAD")
    )
    private static void clientTick(Level level, BlockPos pos, BlockState state, BlockEntity t, CallbackInfo ci) {
        TrimmedBedBlockEntity entity = (TrimmedBedBlockEntity) (Object) t;

        if (!WatheClient.isKiller() && !CanSeePoison.EVENT.invoker().visible(Minecraft.getInstance().player)) {
            return;
        }

        if (!entity.hasScorpion()) {
            return;
        }

        if (level.getRandom().nextIntBetweenInclusive(0, 20) < 17) {
            return;
        }

        level.addParticle(
                WatheParticles.POISON,
                pos.getX() + 0.5f,
                pos.getY() + 0.5f,
                pos.getZ() + 0.5f,
                0f, 0.05f, 0f
        );
    }
}
