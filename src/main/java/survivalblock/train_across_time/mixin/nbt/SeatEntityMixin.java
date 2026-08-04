package survivalblock.train_across_time.mixin.nbt;

import dev.doctor4t.wathe.block.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("NameDoesntMatchTargetClass")
@Mixin(SeatEntity.class)
public abstract class SeatEntityMixin extends Entity {
    @Shadow
    public abstract BlockPos getSeatPos();

    @Shadow
    public abstract void setSeatPos(BlockPos par1);

    public SeatEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveData(ValueOutput output, CallbackInfo ci) {
        output.storeNullable("seatPos", BlockPos.CODEC, this.getSeatPos());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readData(ValueInput input, CallbackInfo ci) {
        input.read("seatPos", BlockPos.CODEC).ifPresent(this::setSeatPos);
    }
}
