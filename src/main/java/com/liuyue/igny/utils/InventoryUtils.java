package com.liuyue.igny.utils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;

//#if MC >= 12005
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.core.component.DataComponents;
//#else
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.nbt.ListTag;
//$$ import net.minecraft.nbt.Tag;
//#endif

public class InventoryUtils {
    public static boolean isShulkerBoxItem(ItemStack shulkerBox) {
        if (shulkerBox.is(Items.SHULKER_BOX)) {
            return true;
        }
        if (shulkerBox.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }

    public static boolean isEmptyShulkerBox(ItemStack shulkerBox) {
        if (shulkerBox.getCount() != 1) {
            return true;
        }

        //#if MC >= 12005
        ItemContainerContents component = shulkerBox.get(DataComponents.CONTAINER);
        if (component == null || component == ItemContainerContents.EMPTY) {
            return true;
        }
        return !component.nonEmptyItems().iterator().hasNext();
        //#else
        //$$ CompoundTag tag = shulkerBox.getTag();
        //$$ if (tag == null || !tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
        //$$     return true;
        //$$ }
        //$$ CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
        //$$ if (!blockEntityTag.contains("Items", Tag.TAG_LIST)) {
        //$$     return true;
        //$$ }
        //$$ ListTag itemsList = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);
        //$$ return itemsList.isEmpty();
        //#endif
    }
}
