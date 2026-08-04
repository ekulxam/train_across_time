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
package survivalblock.train_across_time.agent.remap;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;

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
            case "net/minecraft/world/level/block/state/properties/DirectionProperty" -> "net/minecraft/world/level/block/state/properties/EnumProperty";
            case "net/minecraft/Util" -> "net/minecraft/util/Util";
            case "net/minecraft/world/level/block/entity/BlockEntityType$Builder" -> "net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder";
            case "net/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier" -> "net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder$Factory";
            case "net/minecraft/world/entity/projectile/ThrowableItemProjectile" -> "net/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile";

            case "org/ladysnake/cca/api/v3/world/WorldComponentInitializer" -> "org/ladysnake/cca/api/v3/level/LevelComponentInitializer";

            case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents" -> "net/fabricmc/fabric/api/creativetab/v1/CreativeModeTabEvents";
            case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents$ModifyEntries" -> "net/fabricmc/fabric/api/creativetab/v1/CreativeModeTabEvents$ModifyOutput";
            case "net/fabricmc/fabric/api/itemgroup/v1/FabricItemGroupEntries" -> "net/fabricmc/fabric/api/creativetab/v1/FabricCreativeModeTabOutput";
            case "net/fabricmc/fabric/api/itemgroup/v1/FabricItemGroup" -> "net/fabricmc/fabric/api/creativetab/v1/FabricCreativeModeTab";

            case "net/minecraft/world/ItemInteractionResult" -> "net/minecraft/world/InteractionResult";

            default -> internalName;
        };

        internalName = switch (info.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (internalName) {
                case "net/minecraft/world/item/Tiers" -> "net/minecraft/world/item/ToolMaterial";

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

            default -> name;
        };

        if (!original.equals(name)) {
            info.markChanged();
        }

        return name;
    }
}
