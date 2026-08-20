package survivalblock.train_across_time.mixin;

import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.train_across_time.util.AvatarRenderStateExtension;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(
            method = "shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shouldRenderLayers(AvatarRenderState state, CallbackInfoReturnable<Boolean> cir) {
        if (((AvatarRenderStateExtension) state).train_across_time$isPsycho()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    private void extractRenderState(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        ((AvatarRenderStateExtension) state).train_across_time$setPsycho(PlayerPsychoComponent.KEY.get(entity).getPsychoTicks() > 0);
    }
}
