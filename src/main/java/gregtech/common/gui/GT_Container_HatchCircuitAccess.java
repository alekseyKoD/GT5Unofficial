package gregtech.common.gui;

import gregtech.api.gui.GT_ContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_OreDictUnificator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GT_Container_HatchCircuitAccess
        extends GT_ContainerMetaTile_Machine {

    public GT_Container_HatchCircuitAccess(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }



    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new Slot(this.mTileEntity, 0, 13, 10));
        addSlotToContainer(new Slot(this.mTileEntity, 1, 42, 53));
        addSlotToContainer(new Slot(this.mTileEntity, 2, 80, 35));
        addSlotToContainer(new Slot(this.mTileEntity, 3, 113, 56));
        addSlotToContainer(new Slot(this.mTileEntity, 4, 124, 13));
        /*
        addSlotToContainer(new Slot(this.mTileEntity, 0, 98, 5));
        addSlotToContainer(new Slot(this.mTileEntity, 1, 116, 5));
        addSlotToContainer(new Slot(this.mTileEntity, 2, 134, 5));
        addSlotToContainer(new Slot(this.mTileEntity, 3, 98, 23));
        addSlotToContainer(new Slot(this.mTileEntity, 4, 116, 23));
        addSlotToContainer(new Slot(this.mTileEntity, 5, 134, 23));
        addSlotToContainer(new Slot(this.mTileEntity, 6, 98, 41));
        addSlotToContainer(new Slot(this.mTileEntity, 7, 116, 41));
        addSlotToContainer(new Slot(this.mTileEntity, 8, 134, 41));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 9, 18, 6, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 10, 35, 6, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 11, 52, 6, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 12, 18, 23, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 13, 35, 23, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 14, 52, 23, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 15, 18, 40, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 16, 35, 40, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 17, 52, 40, false, true, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 18, 8, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 18, 26, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 18, 44, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 18, 62, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 18, 80, 63, false, true, 1));
        */
    }

//(GT_ModHandler.useSolderingIron(aStack, aPlayer))




    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        if(aSlotIndex>4||aSlotIndex<0)
            return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
        ItemStack tStack = aPlayer.inventory.getItemStack();
        if (tStack==null|| GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitBasic")|| GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitGood")
                || GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitAdvanced")|| GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitData")
                || GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitElite")|| GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitMaster")
                || GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitUltimate")|| GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitSuperconductor")||
                GT_OreDictUnificator.isItemStackInstanceOf(tStack,"circuitInfinite")) {
            return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
        }
        return null;
    }

    public int getSlotCount() {
        return 5;
    }

    public int getShiftClickSlotCount() {
        return 0;
    }
}

