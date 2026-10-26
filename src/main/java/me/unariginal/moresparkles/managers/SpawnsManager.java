package me.unariginal.moresparkles.managers;

import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.api.spawning.detail.*;
import com.cobblemon.mod.common.pokemon.aspects.PokemonAspectsKt;
import me.unariginal.moresparkles.MoreSparkles;

import java.util.HashSet;
import java.util.Set;

public class SpawnsManager {
    private static volatile Set<String> alphaBuckets = Set.of();
    private static boolean subscribed = false;
    private static volatile Set<String> pokestopBuckets = Set.of();

    public static boolean isPokestop(SpawnDetail detail) {
        return detail instanceof NPCSpawnDetail
                && detail.getId().equalsIgnoreCase("pokestop");
    }

    public static Set<String> getPokestopBuckets() { return pokestopBuckets; }

    public static void register() {
        SpawnPool pool = CobblemonSpawnPools.INSTANCE.getWORLD_SPAWN_POOL();
        refreshAlphaBuckets(pool);
        if (subscribed) return;
        pool.getObservable().subscribe(SpawnsManager::refreshAlphaBuckets);
        subscribed = true;
    }

    private static void refreshAlphaBuckets(SpawnPool pool) {
        Set<String> buckets = new HashSet<>();
        Set<String> stopBuckets = new HashSet<>();
        for (SpawnDetail detail : pool) {
            if (detailHasAlpha(detail)) buckets.add(detail.getBucket());
            if (isPokestop(detail)) stopBuckets.add(detail.getBucket());
        }
        alphaBuckets = Set.copyOf(buckets);
        pokestopBuckets = Set.copyOf(stopBuckets);
        MoreSparkles.logInfo("Pokestop buckets: " + stopBuckets + ", alpha buckets: " + buckets);
    }

    public static boolean detailHasAlpha(SpawnDetail detail) {
        if (detail instanceof PokemonHerdSpawnDetail herdDetail) {
            return herdDetail.getHerdablePokemon().stream()
                    .anyMatch(herdable -> !PokemonAspectsKt.getALPHA_ASPECT().provide(herdable.getPokemon()).isEmpty());
        }

        if (detail instanceof PokemonSpawnDetail pokemonDetail) {
            return !PokemonAspectsKt.getALPHA_ASPECT().provide(pokemonDetail.getPokemon()).isEmpty();
        }

        return false;
    }

    public static Set<String> getAlphaBuckets() {
        return alphaBuckets;
    }
}
