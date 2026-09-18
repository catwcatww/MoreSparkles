package me.unariginal.moresparkles.mixin;

import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction;
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause;
import me.unariginal.moresparkles.MoreSparkles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonSpawnAction.class)
public class PokemonSpawnActionMixin {

    @Inject(method = "createEntity", at = @At("HEAD"), remap = false)
    private void moresparkles$flagBaitSpawn(CallbackInfoReturnable<?> cir) {
        PokemonSpawnAction self = (PokemonSpawnAction)(Object) this;
        Object cause = self.getSpawnablePosition().getCause();

        boolean isFishing = cause instanceof FishingSpawnCause;
        boolean isPokeSnack = self.getSpawnablePosition().getSpawner().getName().startsWith("poke_snack_spawner_");

        MoreSparkles.currentSpawnIsBaitBased.set(isFishing || isPokeSnack);
    }
    @Inject(method = "createEntity", at = @At("RETURN"), remap = false)
    private void moresparkles$clearBaitSpawnFlag(CallbackInfoReturnable<?> cir) {
        MoreSparkles.currentSpawnIsBaitBased.set(false);
    }
}