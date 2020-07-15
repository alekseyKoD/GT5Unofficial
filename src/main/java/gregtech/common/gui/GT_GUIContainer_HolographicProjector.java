package gregtech.common.gui;

import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_HolographicProjector;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_HolographicProjector extends GT_GUIContainerMetaTile_Machine {
    public GT_GUIContainer_HolographicProjector(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(new GT_Container_HolographicProjector(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "Teleporter.png");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString("Holographic Projector", 46, 8, 16448255);
        String[] mParamNames = ((GT_MetaTileEntity_HolographicProjector)this.mContainer.mTileEntity.getMetaTileEntity()).getParamNames();
        if (this.mContainer != null) {
            this.fontRendererObj.drawString(""+mParamNames[0] + GT_Utility.parseNumberToString(((GT_Container_HolographicProjector) this.mContainer).param1), 46, 16, 16448255);
            this.fontRendererObj.drawString(""+mParamNames[1] + GT_Utility.parseNumberToString(((GT_Container_HolographicProjector) this.mContainer).param2), 46, 24, 16448255);
            this.fontRendererObj.drawString(""+mParamNames[2] + GT_Utility.parseNumberToString(((GT_Container_HolographicProjector) this.mContainer).param3), 46, 32, 16448255);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
