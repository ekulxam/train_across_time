package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @WrapMethod(
            method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
    )
    private void extractRenderState(ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks, ChatComponent.DisplayMode displayMode, Operation<Void> original) {
        if (!WatheClient.isPlayerAliveAndInSurvival()) {
            original.call(graphics, screenHeight, ticks, displayMode);
        }
    }
}
