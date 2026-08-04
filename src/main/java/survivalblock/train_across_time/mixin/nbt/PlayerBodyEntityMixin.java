package survivalblock.train_across_time.mixin.nbt;

import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@SuppressWarnings("NameDoesntMatchTargetClass")
@Mixin(PlayerBodyEntity.class)
public abstract class PlayerBodyEntityMixin extends LivingEntity {
    @Shadow
    public abstract UUID getPlayerUuid();

    @Shadow
    public abstract void setPlayerUuid(UUID playerUuid);

    protected PlayerBodyEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveData(ValueOutput output, CallbackInfo ci) {
        super.addAdditionalSaveData(output);
        output.storeNullable("Player", UUIDUtil.CODEC, this.getPlayerUuid());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readData(ValueInput input, CallbackInfo ci) {
        super.readAdditionalSaveData(input);

        UUID uuid = input.read("Player", UUIDUtil.CODEC).orElseGet(() -> {
            if (this.level() instanceof ServerLevel serverLevel) {
                return OldUsersConverter.convertMobOwnerIfNecessary(
                        serverLevel.getServer(),
                        input.getStringOr("Player", "")
                );
            }
            return null;
        });

        if (uuid != null) {
            this.setPlayerUuid(uuid);
        }
    }
}
