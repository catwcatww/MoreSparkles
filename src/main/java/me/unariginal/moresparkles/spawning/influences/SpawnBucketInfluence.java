package me.unariginal.moresparkles.spawning.influences;

import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import me.unariginal.moresparkles.data.BoostType;
import me.unariginal.moresparkles.managers.SpawnsManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static me.unariginal.moresparkles.managers.BoostManager.getGenericMultiplierTotal;

public class SpawnBucketInfluence implements SpawningInfluence {
    private final ServerPlayerEntity player;

    public SpawnBucketInfluence(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void affectBucketWeights(@NotNull Map<String, Float> bucketWeights) {
        float multiplier = getGenericMultiplierTotal(player, BoostType.SPAWN_BUCKET);
        if (multiplier <= 0F || multiplier == 1F) return;

        Set<String> exempt = SpawnsManager.getPokestopBuckets();
        Predicate<String> flatten = b -> !b.equals("boss") && !exempt.contains(b);

        float originalTotal = (float) bucketWeights.entrySet().stream()
                .filter(e -> flatten.test(e.getKey()))
                .mapToDouble(Map.Entry::getValue).sum();

        float exponent = 1.0F / multiplier;
        bucketWeights.replaceAll((bucket, weight) ->
                flatten.test(bucket) ? (float) Math.pow(weight, exponent) : weight);

        if (originalTotal > 0F) {
            float newTotal = (float) bucketWeights.entrySet().stream()
                    .filter(e -> flatten.test(e.getKey()))
                    .mapToDouble(Map.Entry::getValue).sum();
            float ratio = newTotal / originalTotal;
            bucketWeights.computeIfPresent("boss", (b, w) -> w * ratio);
            for (String b : exempt) bucketWeights.computeIfPresent(b, (k, w) -> w * ratio);
        }
    }
}