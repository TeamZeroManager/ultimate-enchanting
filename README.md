# Ultimate Enchanting — Minecraft 1.20.1 / Forge 47.x

Prototype source project for a custom enchanting table.

## Intended behavior
- Adds a separate Ultimate Enchanting Table block.
- Lists registered enchantments applicable to the inserted item (books show all registered enchantments).
- Selecting an entry applies that enchantment at its registered maximum level.
- Does not reject enchantments because they conflict with enchantments already on the item.
- Costs one lapis lazuli and one experience level per selection.
- The table is added to the Functional Blocks creative tab.

## Build requirements
- JDK 17
- Gradle / ForgeGradle dependencies downloaded from the Forge Maven repository
- Minecraft Forge 1.20.1, Forge 47.3.0

From this directory, run `gradle build`. The output should be under `build/libs/`.

## Important status note
This source archive has **not been compiled or tested in-game**. The current execution environment has no Gradle installation, no cached Forge dependencies, and cannot resolve the Forge Maven host, so a real installable JAR could not be built or verified here. Do not install this source ZIP as a mod; it is not a JAR.
