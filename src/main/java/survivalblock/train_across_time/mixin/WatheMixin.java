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
package survivalblock.train_across_time.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.train_across_time.util.TrainAcrossTimeMixinHelper;

@Mixin(targets = "dev.doctor4t.wathe.Wathe")
public class WatheMixin {
    @Inject(method = "onInitialize", at = @At("RETURN"))
    private void initShopEntriesAfterRegistration(CallbackInfo ci) {
        TrainAcrossTimeMixinHelper.SHOP_ENTRIES_INITIALIZER.runLast();
    }
}
