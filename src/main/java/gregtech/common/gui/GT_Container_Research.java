package gregtech.common.gui;

import gregtech.api.gui.GT_Slot_Holo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GT_Container_Research extends Container {

    public GT_Container_Research(InventoryPlayer aPlayerInventory, GT_SlotDefinition[] aSlots){
        for(int i = 1; i < aSlots.length+1; i++) {
            GT_SlotDefinition def = aSlots[i-1];
            addSlotToContainer(new GT_Slot_Holo(aPlayerInventory, i, def.x, def.y, false, false, 0, def.stack));
        }
    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    public static class GT_SlotDefinition{
        public int x,y;
        public ItemStack stack;

        public GT_SlotDefinition(ItemStack aStack, int aX, int aY){
            stack = aStack;
            x = aX;
            y = aY;
        }
    }
}
