package com.primogemstudio.primogemcraft.items.instances.materials.vayuda;

import com.primogemstudio.primogemcraft.items.PrimogemCraftItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VayudaTurquoiseGemstoneDiamondSwordItem extends SwordItem {
    public VayudaTurquoiseGemstoneDiamondSwordItem() {
        super(new Tier() {
            public int getUses() {
                return 1920;
            }

            public float getSpeed() {
                return 8f;
            }

            public float getAttackDamageBonus() {
                return 4.5f;
            }

            public int getLevel() {
                return 3;
            }

            public int getEnchantmentValue() {
                return 14;
            }

            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(PrimogemCraftItems.VAYUDA_TURQUOISE_GEMSTONE_SLIVER_ITEM), new ItemStack(PrimogemCraftItems.PRIMOGEM_ITEM));
            }
        }, 3, -2.2f, new Item.Properties().fireResistant());
    }

    @Override
    public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        OnAttack.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
        return super.hurtEnemy(itemstack, entity, sourceentity);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("tooltip.primogemcraft.vayuda_turquoise_gemstone_diamond_sword.line1"));
        list.add(Component.translatable("tooltip.primogemcraft.vayuda_turquoise_gemstone_diamond_sword.line2"));
        list.add(Component.translatable("tooltip.primogemcraft.vayuda_turquoise_gemstone_diamond_sword.line3"));
    }
}