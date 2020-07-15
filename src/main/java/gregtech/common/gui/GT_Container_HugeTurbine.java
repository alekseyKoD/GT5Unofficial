package gregtech.common.gui;

import gregtech.api.gui.GT_Container_MultiMachine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GT_Container_HugeTurbine  extends GT_Container_MultiMachine {
    public GT_Container_HugeTurbine(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }


    @Override
    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new Slot(mTileEntity, 5, 133, 5));
        addSlotToContainer(new Slot(mTileEntity, 1, 152, 5));
        addSlotToContainer(new Slot(mTileEntity, 2, 152, 25));
        addSlotToContainer(new Slot(mTileEntity, 3, 152, 45));
        addSlotToContainer(new Slot(mTileEntity, 4, 152, 65));

    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public int getShiftClickSlotCount() {
        return 5;
    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        System.out.println(""+aSlotIndex);
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }
}
