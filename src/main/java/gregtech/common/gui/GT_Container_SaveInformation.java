package gregtech.common.gui;

import gregtech.api.enums.ItemList;
import gregtech.api.gui.GT_Slot_Holo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.net.GT_Packet_SaveInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static gregtech.api.enums.GT_Values.NW;

public class GT_Container_SaveInformation extends Container {

    IGregTechTileEntity terminal;
    int mID;

    public GT_Container_SaveInformation(InventoryPlayer aPlayerInventory, IInventory aCopyFrom, boolean small, IGregTechTileEntity aTerminal, int aRecipeID){
        //addSlotToContainer(new GT_Slot_Holo(aPlayerInventory,1,slotX,sloY,false,false,0, aDisplayeStack));
        bindPlayerInventory(aPlayerInventory);
        terminal = aTerminal;
        mID = aRecipeID;
        if(small){
            addSlotToContainer(new GT_Slot_Holo(aPlayerInventory, 36, 71, 26,false,false,0, aCopyFrom.getStackInSlot(0)));
            addSlotToContainer(new GT_Slot_Holo(aPlayerInventory, 37, 89, 26,false,false,0, aCopyFrom.getStackInSlot(1)));
            addSlotToContainer(new GT_Slot_Holo(aPlayerInventory, 38, 71, 44,false,false,0, aCopyFrom.getStackInSlot(2)));
            addSlotToContainer(new GT_Slot_Holo(aPlayerInventory, 39, 89, 44,false,false,0, aCopyFrom.getStackInSlot(3)));
        }

    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        if(aSlotIndex<0)
            return super.slotClick(aShifthold,aMouseclick,aShifthold,aPlayer);
        ItemStack aStack = getSlot(aSlotIndex).getStack();
        if(checkIfCanAdd(aStack)){
            NW.sendToServer(new GT_Packet_SaveInformation(terminal.getWorld().provider.dimensionId, terminal.getXCoord(), terminal.getYCoord(), terminal.getZCoord(),mID,aSlotIndex));
            Minecraft.getMinecraft().thePlayer.closeScreenNoPacket();
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    public boolean checkIfCanAdd(ItemStack aStack){
        int freeSpace = 0;
        if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)|| ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)|| ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
            return false;
        NBTTagCompound tTag = aStack.getTagCompound();
        if(tTag == null)
            return false;
        if(tTag.getBoolean("isLocked"))
            return false;
        int size = tTag.getInteger("capacitySize");
        int usedCapacity = tTag.getInteger("usedCapacity");
        freeSpace+=(size-usedCapacity);
        if(freeSpace<=0)
            return false;
        return true;
    }

    protected void bindPlayerInventory(InventoryPlayer aInventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new GT_Slot_Holo(aInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18,false,false,0,aInventoryPlayer.getStackInSlot(j + i * 9 + 9)));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new GT_Slot_Holo(aInventoryPlayer, i, 8 + i * 18, 142,false,false,0,aInventoryPlayer.getStackInSlot(i)));
        }
    }
}
