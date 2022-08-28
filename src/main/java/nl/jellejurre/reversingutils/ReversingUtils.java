package nl.jellejurre.reversingutils;

import com.seedfinding.latticg.util.LCG;
import com.seedfinding.mccore.rand.seed.WorldSeed;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.math.NextLongReverser;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcmath.util.Mth;
import com.seedfinding.mcreversal.ChunkRandomReverser;
import java.util.ArrayList;
import java.util.List;

public class ReversingUtils {
    public static List<Pair<Long, CPos>> getLocationsFromLootSeeds(List<Long> lootSeeds, int index, int step, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, MCVersion version){
        List<Long> decoratorSeeds = getDecoratorSeedsFromLootSeeds(lootSeeds);
        List<Long> populationSeeds = getPopulationSeedsFromDecoratorSeeds(decoratorSeeds, index, step, version);
        return getLocationsFromPopulationSeeds(populationSeeds, minChunkX, minChunkZ, maxChunkX, maxChunkZ, version);
    }

    public static List<Pair<Long, CPos>> getLocationsFromLootSeeds(List<Long> lootSeeds, int salt, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, MCVersion version){
        return getLocationsFromLootSeeds(lootSeeds, salt%10, salt/10000, minChunkX, minChunkZ, maxChunkX, maxChunkZ, version);
    }
    public static List<Long> getDecoratorSeedsFromLootSeeds(List<Long> lootSeeds){
        List<Long> decoratorSeeds = new ArrayList<>();
        for (Long lootSeed : lootSeeds) {
            if(!WorldSeed.isRandom(lootSeed)){
                continue;
            }
            long validSeed = lootSeed ^ LCG.JAVA.multiplier & Mth.MASK_48;
            decoratorSeeds.addAll(NextLongReverser.getSeeds(validSeed));
        }
        return decoratorSeeds;
    }
    public static List<Long> getPopulationSeedsFromDecoratorSeeds(List<Long> decoratorSeeds, int salt, MCVersion version){
        return getPopulationSeedsFromDecoratorSeeds(decoratorSeeds, salt%10, salt/10000, version);
    }

    public static List<Long> getPopulationSeedsFromDecoratorSeeds(List<Long> decoratorSeeds, int index, int step, MCVersion version){
        List<Long> populationSeeds = new ArrayList<>();
        decoratorSeeds.forEach(decoratorSeed -> {
            long actualDecoratorSeed = decoratorSeed ^ LCG.JAVA.multiplier;
            long populationSeed = ChunkRandomReverser.reverseDecoratorSeed(actualDecoratorSeed, index, step, version);
            populationSeeds.add(populationSeed);
        });
        return populationSeeds;
    }

    public static List<Pair<Long, CPos>> getLocationsFromPopulationSeeds(
        List<Long> populationSeeds, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, MCVersion version) {
        List<Pair<Long, CPos>> locations = new ArrayList<>();
        for (Long populationSeed : populationSeeds) {
            for (int x = minChunkX; x < maxChunkX; x++) {
                for (int z = minChunkZ; z < maxChunkZ; z++) {
                    List<Long> structureSeeds = ChunkRandomReverser.reversePopulationSeed(populationSeed, x<<4, z<<4, version);
                    for (Long structureSeed : structureSeeds) {
                        if ((structureSeed & Mth.MASK_48) == 0) {
                            continue;
                        }
                        locations.add(new Pair<>(structureSeed, new CPos(x, z)));
                    }
                }
            }
        }
        return locations;
    }
}
