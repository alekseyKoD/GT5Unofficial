package gregtech.common.gui;

import gregtech.api.gui.GT_ContainerMetaTile_Machine;
import gregtech.api.gui.GT_Slot_Output;
import gregtech.api.gui.GT_Slot_Render;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class GT_Container_PrimitiveResearchStation extends GT_ContainerMetaTile_Machine {

    public GT_Container_PrimitiveResearchStation(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    public void addSlots(InventoryPlayer aInventoryPlayer) {
     //   addSlotToContainer(new GT_Slot_Holo(mTileEntity, 0, 8, 63, false, true, 1));
     //   addSlotToContainer(new GT_Slot_Holo(mTileEntity, 0, 26, 63, false, true, 1));
      //  addSlotToContainer(new GT_Slot_Render(mTileEntity, 2, 107, 63));



        // addSlotToContainer(new Slot(mTileEntity, 1, 80, 63));
        addSlotToContainer(new Slot(mTileEntity, 3, 125, 63));
        int tStartIndex = 4;
        addSlotToContainer(new GT_Slot_Output(mTileEntity, 1, 143, 44));//output
        addSlotToContainer(new GT_Slot_Render(mTileEntity, 2, 43, 43));//fluid stack

        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 7, 33));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 25, 33));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 7, 51));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 25, 51));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 66, 25));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 84, 25));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 102, 25));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 66, 43));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 84, 43));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 102, 43));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 66, 61));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 84, 61));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 102, 61));
        addSlotToContainer(new Slot(mTileEntity, tStartIndex++, 146, 14));

        //addSlotToContainer(new GT_Slot_Render(mTileEntity, tStartIndex++, 53, 63));
      //  addSlotToContainer(new GT_Slot_Output(mTileEntity, tStartIndex++, 107, 25));

    }

    public int getSlotCount() {
        return 19;
    }

    public int getShiftClickSlotCount() {
        return 0;
    }

}
