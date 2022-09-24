package nl.jellejurre.reversingutils;

import static nl.jellejurre.reversingutils.ReversingUtils.getLocationsFromLootSeeds;
import static org.junit.jupiter.api.Assertions.assertTrue;


import com.seedfinding.latticg.reversal.DynamicProgram;
import com.seedfinding.latticg.reversal.calltype.java.JavaCalls;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.ChestContent;
import com.seedfinding.mcfeature.loot.LootContext;
import com.seedfinding.mcfeature.loot.LootTable;
import com.seedfinding.mcfeature.loot.MCLootTables;
import com.seedfinding.mcfeature.loot.item.Items;
import com.seedfinding.mcfeature.structure.RuinedPortal;
import com.seedfinding.mcfeature.structure.generator.structure.RuinedPortalGenerator;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReversingUtilsTest {

    // Summary of minecraft's loot generation system:
    // for each loot pool in the table:
    //   rolls = lootpool.minRolls + random.nextInt(lootpool.maxRolls-lootpool.minRolls + 1)
    //   ItemRoll[] itemRolls = lootpool.generateWeightedArray()
    //   for (int i=0; i<rolls; i++):
    //     ItemRoll item = itemRolls[random.nextInt(itemRolls.length)]
    //     int count = item.minCount + random.nextInt(itemcount.maxCount-item.minCount + 1)
    //     item.ApplyDamageAndEnchantmentFunctions(random)
    @Test
    public void PortalTest() {
        DynamicProgram dynamicProgram = DynamicProgram.create(LCG.JAVA);
        // We are rolling on the Ruined Portal Loot Table, which has 4-8 rolls.
        // So the first random call will be the amount of rolls. Since there are five values it can take, minecraft does nextInt(5) since nextInt isn't inclusive.
        dynamicProgram.add(JavaCalls.nextInt(5).equalTo(4));
        // We check the value of the first four rolls. Any more and it will be too rare.
        for (int i = 0; i < 4; i++) {
            // For every roll, we want to know if the roll is gold blocks, which has its values equal to 397.
            // The easiest way to check this is to look at LootPool.precomputedWeights during generation.
            dynamicProgram.add(JavaCalls.nextInt(398).equalTo(397));
            // Gold blocks rolls in batches of 1 or 2. Since there are two values it can take, minecraft does nextInt(2) since nextInt isn't inclusive.
            dynamicProgram.add(JavaCalls.nextInt(2).equalTo(1));
            // No calls done due to enchantment or damage functions, so these are all the calls of this item.
        }
        List<Long> lootSeeds = dynamicProgram.reverse().boxed().parallel().toList();
        System.out.println("Loot seeds found: " + lootSeeds.size());

        int salt = new RuinedPortal(Dimension.OVERWORLD, MCVersion.v1_16).getDecorationSalt();

        List<Pair<Long, CPos>> locations =
            ReversingUtils.getLocationsFromLootSeeds(lootSeeds, salt, -10, -10, 10, 10, MCVersion.v1_16);

        assertTrue(locations.size() > 0);
        System.out.println("Structure seed, CPos pairs found: " + locations.size());

        // Validate using manual work
        for (Pair<Long, CPos> location : locations) {
            ChunkRand cr = new ChunkRand();
            cr.setDecoratorSeed(location.getFirst(), location.getSecond().getX() << 4, location.getSecond().getZ() << 4, salt, MCVersion.v1_16);
            long lootseed = cr.nextLong();
            LootTable portalTable = MCLootTables.RUINED_PORTAL_CHEST;
            LootContext context = new LootContext(lootseed);
            portalTable.apply(MCVersion.v1_16);
            assertTrue(portalTable.generate(context).stream().anyMatch(item -> item.getItem().equals(Items.GOLD_BLOCK) && item.getCount() >= 8));
        }

        // Validate using libraries
        for (Pair<Long, CPos> location : locations) {
            RuinedPortalGenerator generator = new RuinedPortalGenerator(MCVersion.v1_16);
            RuinedPortal portal = new RuinedPortal(Dimension.OVERWORLD, MCVersion.v1_16);
            OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16, location.getFirst());
            OverworldTerrainGenerator terrainGenerator = new OverworldTerrainGenerator(biomeSource);
            if(generator.generate(terrainGenerator, location.getSecond())){
                List<ChestContent> loot = portal.getLoot(location.getFirst(), generator, false);
                assertTrue(loot.get(0).containsAtLeast(Items.GOLD_BLOCK, 8));
            }
        }
    }
}
