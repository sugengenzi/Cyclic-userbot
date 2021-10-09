/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclic.enchant;

import java.lang.reflect.Method;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.base.EnchantBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class EnchantQuickdraw extends EnchantBase {

  public EnchantQuickdraw(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
    super(rarityIn, typeIn, slots);
    MinecraftForge.EVENT_BUS.register(this);
  }

  public static BooleanValue CFG;
  public static final String ID = "quickshot";

  @Override
  public boolean isEnabled() {
    return CFG.get();
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    return stack.getItem() instanceof BowItem;
  }

  @Override
  public int getMaxLevel() {
    return 3;
  }

  @SubscribeEvent
  public void onPlayerUpdate(LivingUpdateEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      if (player.isUsingItem() == false) {
        return;
      }
      InteractionHand hand = player.getUsedItemHand();
      if (hand == null) {
        return;
      }
      ItemStack heldItem = player.getItemInHand(hand);
      if (heldItem.getItem() instanceof BowItem == false) {
        return;
      }
      int level = getCurrentLevelTool(heldItem);
      if (level <= 0) {
        return;
      }
      // extra tick per level
      for (int i = 0; i < level; i++) {
        this.tickHeldBow(player);
      }
    }
  }

  private void tickHeldBow(Player player) {
    try {
      Method m = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "updatingUsingItem");
      //      Method m = PlayerEntity.class.getDeclaredMethod("updateActiveHand");
      m.setAccessible(true);
      m.invoke(player);
    }
    catch (Exception e) {
      ModCyclic.LOGGER.error("Player quickdraw error", e);
    }
  }
}
