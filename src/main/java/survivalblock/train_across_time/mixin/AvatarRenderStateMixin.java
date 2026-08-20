package survivalblock.train_across_time.mixin;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import survivalblock.train_across_time.util.AvatarRenderStateExtension;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateExtension {
    @Unique
    private boolean train_across_time$psycho = false;

    @Override
    public boolean train_across_time$isPsycho() {
        return train_across_time$psycho;
    }

    @Override
    public void train_across_time$setPsycho(boolean psycho) {
        train_across_time$psycho = psycho;
    }
}
