package survivalblock.train_across_time.mixin;

import dev.doctor4t.wathe.client.gui.screen.ingame.NoteScreen;
import dev.doctor4t.wathe.item.NoteItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(NoteItem.class)
public abstract class NoteItemMixin extends Item {
    public NoteItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD")
    )
    private void use(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide() && user.isShiftKeyDown()) {
            Minecraft.getInstance().setScreen(new NoteScreen());
        }
    }
}
