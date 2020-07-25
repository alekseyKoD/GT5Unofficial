package gregtech.common.gui;

import gregtech.api.gui.GT_ContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class GT_Container_ComputerTerminal extends GT_ContainerMetaTile_Machine {

    public GT_Container_ComputerTerminal(InventoryPlayer i, IGregTechTileEntity g){
        super(i,g,false);
    }
}
