package com.github.wolfshotz.wyrmroost.items.base;

import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum ToolMaterials implements Tier {
    BLUE_GEODE(7.5f, 0.5f, 1732, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 20, Ingredient.of(WRItems.BLUE_GEODE.value())),
    RED_GEODE(8.5f, 0.5f, 2031, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 20, Ingredient.of(WRItems.RED_GEODE.value())),
    PURPLE_GEODE(10f, 0, 4214, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 25, Ingredient.of(WRItems.PURPLE_GEODE.value())),
    PLATINUM(6.5f, 0, 425, BlockTags.INCORRECT_FOR_IRON_TOOL, 18, Ingredient.of(WRItems.PLATINUM_INGOT.value()));

    private final float efficiency, attackDamage;
    private final int durability, enchantibility;
    private final TagKey<Block> incorrectBlocks;
    private final Ingredient repairMaterial;

    ToolMaterials(float efficiency, float attackDmgMod, int durability, TagKey<Block> incorrectBlocks, int enchantibility, Ingredient repairMaterial)
    {
        this.efficiency = efficiency;
        this.attackDamage = attackDmgMod;
        this.durability = durability;
        this.incorrectBlocks = incorrectBlocks;
        this.enchantibility = enchantibility;
        this.repairMaterial = repairMaterial;
    }

    @Override
    public int getUses() { return durability; }

    @Override
    public float getSpeed() { return efficiency; }

    /**
     * Literally used for decimal additions to attack damage. It's fucking stupid but it's what has to be done.
     */
    @Override
    public float getAttackDamageBonus() { return attackDamage; }

   @Override
   public TagKey<Block> getIncorrectBlocksForDrops() {
      return incorrectBlocks;
   }

   @Override
    public int getEnchantmentValue() { return enchantibility; }

    @Override
    public Ingredient getRepairIngredient() { return repairMaterial; }
}
