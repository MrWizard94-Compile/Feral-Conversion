package com.mrwizard.generatedferallanterns.menu;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class MigrationMenu extends AbstractContainerMenu {
    public MigrationMenu(int containerId, Inventory inventory) {
        super(GeneratedFeralLanterns.MIGRATION_MENU.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
