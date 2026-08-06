/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.train_across_time.common.remap;

import org.objectweb.asm.commons.Remapper;
import survivalblock.train_across_time.common.util.ClassOutputInfo;

/**
 * @author Typho
 */
@SuppressWarnings({"SwitchStatementWithTooFewBranches"})
public class WatheRemapper extends Remapper {
    public final ClassOutputInfo info;

    public WatheRemapper(int api, ClassOutputInfo info) {
        super(api);
        this.info = info;
    }

    @Override
    public String map(String internalName) {
        var original = internalName;

        internalName = switch (internalName) {
            case "net/minecraft/resources/ResourceLocation" -> "net/minecraft/resources/Identifier";
            case "net/minecraft/world/InteractionResultHolder", "net/minecraft/world/ItemInteractionResult" -> "net/minecraft/world/InteractionResult";
            case "net/minecraft/world/level/block/state/properties/DirectionProperty" -> "net/minecraft/world/level/block/state/properties/EnumProperty";
            case "net/minecraft/Util" -> "net/minecraft/util/Util";
            case "net/minecraft/world/level/block/entity/BlockEntityType$Builder" -> "net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder";
            case "net/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier" -> "net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder$Factory";
            case "net/minecraft/world/entity/projectile/ThrowableItemProjectile" -> "net/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile";
            case "net/minecraft/client/particle/TextureSheetParticle" -> "net/minecraft/client/particle/SingleQuadParticle";
            case "net/minecraft/client/model/HierarchicalModel" -> "net/minecraft/client/model/EntityModel";

            case "org/ladysnake/cca/api/v3/world/WorldComponentInitializer" -> "org/ladysnake/cca/api/v3/level/LevelComponentInitializer";
            case "org/ladysnake/cca/api/v3/world/WorldComponentFactoryRegistry" -> "org/ladysnake/cca/api/v3/level/LevelComponentFactoryRegistry";

            case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents" -> "net/fabricmc/fabric/api/creativetab/v1/CreativeModeTabEvents";
            case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents$ModifyEntries" -> "net/fabricmc/fabric/api/creativetab/v1/CreativeModeTabEvents$ModifyOutput";
            case "net/fabricmc/fabric/api/itemgroup/v1/FabricItemGroupEntries" -> "net/fabricmc/fabric/api/creativetab/v1/FabricCreativeModeTabOutput";
            case "net/fabricmc/fabric/api/itemgroup/v1/FabricItemGroup" -> "net/fabricmc/fabric/api/creativetab/v1/FabricCreativeModeTab";
            case "net/fabricmc/fabric/api/client/event/lifecycle/v1/ClientTickEvents$StartWorldTick" -> "net/fabricmc/fabric/api/client/event/lifecycle/v1/ClientTickEvents$StartLevelTick";
            case "net/fabricmc/fabric/api/client/particle/v1/ParticleFactoryRegistry" -> "net/fabricmc/fabric/api/client/particle/v1/ParticleProviderRegistry";
            case "net/fabricmc/fabric/api/client/particle/v1/ParticleFactoryRegistry$PendingParticleFactory" -> "net/fabricmc/fabric/api/client/particle/v1/ParticleProviderRegistry$PendingParticleProvider";
            case "net/fabricmc/fabric/api/client/particle/v1/FabricSpriteProvider" -> "net/fabricmc/fabric/api/client/particle/v1/FabricSpriteSet";
            case "net/fabricmc/fabric/api/client/rendering/v1/EntityModelLayerRegistry" -> "net/fabricmc/fabric/api/client/rendering/v1/ModelLayerRegistry";
            case "net/fabricmc/fabric/api/client/rendering/v1/EntityModelLayerRegistry$TexturedModelDataProvider" -> "net/fabricmc/fabric/api/client/rendering/v1/ModelLayerRegistry$TexturedLayerDefinitionProvider";
            case "net/fabricmc/fabric/api/client/rendering/v1/WorldRenderEvents" -> "net/fabricmc/fabric/api/client/rendering/v1/level/LevelRenderEvents";
            case "net/fabricmc/fabric/api/client/rendering/v1/WorldRenderEvents$Last" -> "net/fabricmc/fabric/api/client/rendering/v1/level/LevelRenderEvents$EndMain";
            case "net/fabricmc/fabric/api/client/rendering/v1/WorldRenderContext" -> "net/fabricmc/fabric/api/client/rendering/v1/level/LevelRenderContext";

            default -> internalName;
        };

        internalName = switch (info.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (internalName) {
                case "net/minecraft/world/item/Tiers" -> "net/minecraft/world/item/ToolMaterial";

                default -> internalName;
            };
            case "dev/doctor4t/wathe/mixin/client/items/BipedEntityModelMixin" -> switch (internalName) {
                case "net/minecraft/world/entity/LivingEntity" -> "net/minecraft/client/renderer/entity/state/HumanoidRenderState";

                default -> internalName;
            };

            default -> internalName;
        };

        if (!original.equals(internalName)) {
            info.markChanged();
        }

        return internalName;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        var original = name;

        name = switch (owner) {
            case "net/fabricmc/fabric/api/networking/v1/PayloadTypeRegistry" -> switch (name) {
                case "configurationC2S" -> "serverboundConfiguration";
                case "configurationS2C" -> "clientboundConfiguration";
                case "playC2S" -> "serverboundPlay";
                case "playS2C" -> "clientboundPlay";

                default -> name;
            };
            case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents" -> switch (name) {
                case "modifyEntriesEvent" -> "modifyOutputEvent";

                default -> name;
            };
            case "net/minecraft/world/level/block/entity/BlockEntityType$Builder" -> switch (name) {
                case "of" -> "create";

                default -> name;
            };
            case "net/minecraft/world/level/block/state/BlockBehaviour$Properties" -> switch (name) {
                case "noCollission" -> "noCollision";

                default -> name;
            };

            default -> name;
        };

        // TODO
        name = switch (info.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (name) {
                case "attributes" -> "axe";

                default -> name;
            };
            case "dev/doctor4t/wathe/mixin/client/items/BipedEntityModelMixin" -> switch (name) {
                case "getMainHandItem" -> "getMainHandItemStack";

                default -> name;
            };

            default -> name;
        };

        if (!original.equals(name)) {
            info.markChanged();
        }

        return name;
    }

    @Override
    public String mapMethodDesc(String methodDescriptor) {
        var original = methodDescriptor;

        methodDescriptor = super.mapMethodDesc(methodDescriptor);

        // TODO
        methodDescriptor = switch (info.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (methodDescriptor) {
                case "(Lnet/minecraft/world/item/component/ItemAttributeModifiers;)Lnet/minecraft/world/item/Item$Properties;" -> "(Lnet/minecraft/world/item/ToolMaterial;FF)Lnet/minecraft/world/item/Item$Properties;";

                default -> methodDescriptor;
            };

            default -> methodDescriptor;
        };

        if (!original.equals(methodDescriptor)) {
            info.markChanged();
        }

        return methodDescriptor;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        var original = name;

        name = switch (owner) {
            case "net/minecraft/world/level/block/Blocks" -> switch (name) {
                case "CHAIN" -> "IRON_CHAIN";

                default -> name;
            };
            case "net/fabricmc/fabric/api/client/event/lifecycle/v1/ClientTickEvents" -> switch (name) {
                case "START_WORLD_TICK" -> "START_LEVEL_TICK";
                case "END_WORLD_TICK" -> "END_LEVEL_TICK";

                default -> name;
            };
            case "net/fabricmc/fabric/api/client/rendering/v1/WorldRenderEvents" -> switch (name) {
                case "LAST" -> "END_MAIN";

                default -> name;
            };

            default -> name;
        };

        if (!original.equals(name)) {
            info.markChanged();
        }

        return name;
    }

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        return super.mapSignature(signature == null ? null : signature.replace("Lnet/minecraft/world/InteractionResultHolder<Lnet/minecraft/world/item/ItemStack;>;", "Lnet/minecraft/world/InteractionResult;"), typeSignature);
    }
}
