package survivalblock.train_across_time.mixin;

import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    private int selected;

    @Inject(
            method = "setSelectedSlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private void setSelectedSlot(int selected, CallbackInfo ci) {
        PlayerPsychoComponent component = PlayerPsychoComponent.KEY.get(player);

        if (component.getPsychoTicks() > 0 && player.getInventory().getItem(this.selected).is(WatheItems.BAT) && !player.getInventory().getItem(selected).is(WatheItems.BAT)) {
            ci.cancel();
        }
    }
}
