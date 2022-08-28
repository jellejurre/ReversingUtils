# ReversingUtils
This is a slightly friendlier interface to the loot reversing part of Neil's repositories.

## Install Instructions
Since it uses Neil's repositories, you will need them to be able to work with this code.  
These repositories can be found at: https://github.com/SeedFinding.  
Add the following to your build.gradle repositories block:
```
    maven {
        url "https://jitpack.io"
    }
    maven {
        url "https://maven.latticg.com/"
    }
    maven {
        url "https://maven.seedfinding.com/"
    }
    maven {
        url "https://maven-snapshots.seedfinding.com/"
    }
```

and the following to your build.gradle dependencies block:
```
    implementation('com.seedfinding:mc_math:d73ac7cc644c67628ade0effd7136e11eb00bb76') { transitive = false }
    implementation('com.seedfinding:mc_seed:5518e3ba3ee567fb0b51c15958967f70a6a19e02') { transitive = false }
    implementation('com.seedfinding:mc_core:706e4f1b7aa6b42b3627f682a311d06280d80b5c') { transitive = false }
    implementation('com.seedfinding:mc_noise:a6ab8e6c688491829f8d2adf845392da22ef8e9c') { transitive = false }
    implementation('com.seedfinding:mc_biome:b2271807a047bb43ac60c8c20ad47e315f19b9a6') { transitive = false }
    implementation('com.seedfinding:mc_terrain:9e937ddb838e28e79423c287fa18b1ce66f061d7') { transitive = false }
    implementation('com.seedfinding:mc_feature:c29fd1fcd746e14c1bcdb127da3113ba273db1fd') { transitive = false }
    implementation('com.seedfinding:mc_reversal:2.0.0') { transitive = false } 
    implementation('com.seedfinding:latticg:1.06')
    implementation('com.github.jellejurre:reversingutils:main-SNAPSHOT'){transitive=false}
```

## Credits 
Made by: jellejurre (discord: @jellejurre#8585)