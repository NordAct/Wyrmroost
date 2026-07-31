## Changes for 1.3 version
- Port from latest released version (1.2.11 for 1.16 released 19th January 2021) to Neoforge 1.21.1
- Added 3 new damage types to accompany vanilla changes
- Fixed possible client crashes related to some dragons having invalid variant. Specifically:
  - Alpine Dragon
  - Canari Wyvern
  - Coin Dragon
  - Dragon Fruit Drake
  - Overworld Drake
  - Royal Red Dragon
  - Silver Glider

- Dragon armor is now equipped to body slot added in newer versions of the game
  - Some dragon armor attribute values differ from original since they're now tied to armor material, and some of them are tied to vanilla materials

- Spawns are no longer hardcoded, instead they use Neoforge's Biome Modifiers, which are data driven (tl;dr you can edit spawns via datapack now)
  - Alpine Dragon now uses `#wyrmroost:alpine_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_mountain/peak` and `minecraft:meadow`
  - Butterfly Leviathan now uses `#wyrmroost:butterflu_leviathan_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_ocean`
  - Canari Wyvern now uses `#wyrmroost:canari_wyvern_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_swamp` 
  - Dragon Fruit Drake now uses `#wyrmroost:dragon_fruit_drake_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_jungle`
  - Lesser Desert Wyrm now uses `#wyrmroost:lesser_desert_wyrm_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_desert`
  - Overworld Drake now uses `#wyrmroost:overworld_drake_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_plains` and `#c:is_savanna` 
  - Roost Stalker now uses `#wyrmroost:roost_stalker_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_plains`, `#c:is_forest` and `#c:is_mountain/slope` 
  - Royal Red Dragon now uses `#wyrmroost:royal_red_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_mountain/peak`
  - Silver Glider now uses `#wyrmroost:silver_glider_can_spawn` tag for valid spawn biomes by default with default entries `#c:is_ocean` and `#c:is_beach`

- Ore features spawns are no longer hardcoded as well and configurable via datapacks
  - They still spawn in similar manner to original mod, despite 1.18 worldgen changes, meaning Blue Geode spawns between y=0 and y=16, while Platinum spawns between y=0 and y=64
  - No Overworld ore currently got deepslate variant
  - Platinum Ore still drops as block instead of dropping its raw counterpart because there's no item for it

- Un-hardcoded bunch of text and added localization keys for it
  - As bilingual myself, I really want to hit with a pan anyone who does that. *Really, really hard*
  - I also updated Russian translation because it was bad

- Replaced hardcoded food items entries (which for most dragons are also items usable for taming) with item tag entries:
  - Alpine Dragon uses `#wyrmroost:alpine_food`, with default entries being Honeycomb and Honey Bottle
  - Butterfly Leviathan uses `#wyrmroost:butterfly_leviathan_food`, with default entries being meat tags
  - Canari Wyvern uses `#wyrmroost:canari_wyvern_food`, with default entries being Sweet Berries
  - Dragon Fruit Drake uses `#wyrmroost:dragon_fruit_drake_food`, with default entries being Apple
  - Overworld Drake uses `#wyrmroost:overworld_drake_food`, with default entries being wheat tag
  - Roost Stalker uses `#wyrmroost:roost_stalker_food`, with default entries being meat tags
  - Royal Red Dragon uses `#wyrmroost:royal_red_food`, with default entries being meat tags
  - Silver Glider uses `#wyrmroost:silver_glider_food`, with default entries being fish tags

- Replaced hardcoded (or better say added) items for breeding with tags:
  - Alpine Dragon uses `#wyrmroost:alpine_breeding_items`, with default entries being dragon's food tag
  - Butterfly Leviathan uses `#wyrmroost:butterfly_leviathan_breeding_items`, with default entries being Kelp to match wiki description
  - Canari Wyvern uses `#wyrmroost:canari_wyvern_breeding_items`, with default entries being dragon's food tag
  - Dragon Fruit Drake uses `#wyrmroost:dragon_fruit_drake_breeding_items`, with default entries being dragon's food tag
  - Overworld Drake uses `#wyrmroost:overworld_drake_breeding_items`, with default entries being dragon's food tag
  - Roost Stalker uses `#wyrmroost:roost_stalker_breeding_items`, with default entries being golden nuggets tag
  - Royal Red Dragon uses `#wyrmroost:royal_red_breeding_items`, with default entries being dragon's food tag
  - Silver Glider uses `#wyrmroost:silver_glider_breeding_items`, with default entries being dragon's food tag

- Added `#wyrmroost:activates_dragon_fruit_drake_crops_growth` tag that accepts items that can activate Dragon Fruit Drake's increased crops growth speed ability. By default contains only Glistering Melon Slice
- Added `#wyrmroost:roost_stalker_taming_items` tag, which contains items that Roost Stalker may eat in case if it happens to get in its mouth. By default contains eggs tag
- Dragon Fruit Drake now can be sheared by any shears instead just vanilla ones (via checking against `#c:tools/shear` tag)
- Silver Glider now can spawn on any sand from `#c:sands` rather than just checking for vanilla sand block
- Made bug that caused flying dragons to be infinitely stuck in a loop between trying to land and take off if they're too close to the ground less annoying (proper fix requires rewriting entire thing)
- Fixed missing inventory title in case if dragon did not have custom name
- Removed home position highlight because it was buggy and I couldn't find way to fix it
- Replaced old staff selection glow with vanilla one as it was buggy too. And also now it's for sure shader friendly
- Downscaled Royal Red and Butterfly Leviathan hitboxes to have 2.95 block height and width to mitigate some performance issues
- Lesser Desert Wyrm now burrows in blocks from `#wymroost:lesser_desert_wyrm_can_burrow_in` that by default contains `#c:sands` tag
- All of stereo sounds have been converted to mono, so distance effects now apply properly
  - As consequence of this change, some sounds may appear quieter than before. ~~Chair~~ Butterfly Leviathan sound included
- Fixed missing translation of keybinds
- Added `#wyrmroost:geode_tipped_arrows` entity type tag that contains all geode arrows
- Added `#wyrmroost:geode_tipped_arrows` item tag that contains all geode arrows
- Different geode tipped arrow types have been split to have their own entity type instead of unified one
- Alpine Dragon can now be tamed with any bee and not just vanilla one
- Added `#wyrmroost:dragons` tag that contains all dragons from the mod
- Soul Crystal now accepts entities from `#wyrmroost:soul_crystal_can_fit` tag. By default, tag contains `#wyrmroost:dragons` and a few mobs from Ice and Fire Community Edition, Dragon Mounts Remastered and Useless Reptile
- Fixed Platinum Ore being breakable with your bare hands

- Other possible changes in mod behavior that came up as result of porting process. Mostly unintentional, because I had to remap entire mob from MCP mappings to Mojmaps by hand (shoutout to shedaniel for making mappings translator tool, it wouldn't be possible to make this port otherwise) or because of differences between game versions

## Changes for 1.4 version
- Updated textures for all dragons (thanks Therzis for all of her hard work of stitching updated textures on older models)
- Slightly edited animation of Canari Wyvern to hide one membrane that just looks bad
- Alpine Dragons got genders now. We respect "traditional values" here (/j)
  - This is also represented visually. Males got markings on their wings
  - As consequence, any resource pack that edited Alpine textures besides Christmas one no longer works
  - All existing Alpine Dragons in the world will be turned into females when updating the world from previous game version
- Added `#wyrmroost:home_defender_attackable` tag for filtering entities that can be attacked
  - Tag by default attempts to contain only hostile mobs (minus creepers) to exclude possibility of accidentally massacring lots of passive mobs
- Fixed Butterfly Leviathan Conduit attack targeting entities considered allies
- Fixed various issues with ore spawns
- Dragon Fruit Drake now spawns as baby with 60% chance
- Alpine Dragons now will have 30 seconds delay between attempting to roar (starting from moment of roar start) to prevent causing infinite roar loops. And also to have some mercy on MC sound engine
- Fixed dragons trying to set home position mid air while leashed and desperately trying to return to it after
- Fixed various issues related to Butterfly Leviathans and conduits
- Added `#wyrmroost:butterfly_leviathan_conduit_targets` tag that contains entities that can be damaged by Butterfly Leviathans with Conduit if they're not actively targeted and get too close
  - By default, tag contains `#wyrmroost:home_defender_attackable` and `#minecraft:aquatic`, so don't leave your BFLs near your aquariums
- Updated Butterfly Leviathan navigator to make it do donuts in water less often
- Dragons that can ride player now render attached to player model and no longer dismount themselves when player uses elytra
- Fixed issue when dragons that ride player either dismount player or vanish from world entirely when player logs off
- Fixed issue when dragons could start buttsliding when they happen to be under rain
  - This also fixes BFL being unable to stand still while in sitting pose under water