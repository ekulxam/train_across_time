package survivalblock.train_across_time.mixin;

import dev.doctor4t.wathe.cca.TrainWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyArg(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V"
            ),
            index = 7
    )
    private boolean renderLevel(boolean renderSky) {
        return renderSky && (!WatheClient.isTrainMoving() || WatheClient.trainComponent.getTimeOfDay() == TrainWorldComponent.TimeOfDay.SUNDOWN);
    }
}
