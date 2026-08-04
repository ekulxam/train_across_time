package survivalblock.train_across_time.provided.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;

@SuppressWarnings("unused")
public class ZItemEntityRenderState extends EntityRenderState {
    // need bounding box height, age, hashCode, crosshairPickEntity (from dispatcher)

    // can use id and level in extract
    public final ItemStackRenderState itemStackRenderState = new TrackingItemStackRenderState();
    public int entityHashCode = 0;
}
