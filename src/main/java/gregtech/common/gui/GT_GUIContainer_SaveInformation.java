package gregtech.common.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_SaveInformation extends GuiContainer implements INEIGuiHandler {

    GT_Recipe_ResearchStation mRecipe;
    EntityPlayer mPlayer;
    IGregTechTileEntity terminal;
    ResourceLocation mBackgrpund;
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    IInventory mCopyFrom;



    public GT_GUIContainer_SaveInformation(GT_Recipe_ResearchStation aRecipe, EntityPlayer aPlayer, IGregTechTileEntity aTerminal, IInventory aCopyFrom){
        super(new GT_Container_SaveInformation(aPlayer.inventory,aCopyFrom,true,aTerminal, aRecipe.mID));
        mRecipe = aRecipe;
        mPlayer = aPlayer;
        terminal = aTerminal;
        mBackgrpund =  new ResourceLocation(RES_PATH_GUI + "2by2.png");
        mCopyFrom = aCopyFrom;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString("2 by 2", 8, 4, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(mBackgrpund);
        if (GregTech_API.sColoredGUI && mCopyFrom instanceof IGregTechTileEntity) {
            int tColor = ((IGregTechTileEntity)mCopyFrom).getColorization() & 15;
            if (tColor < ItemDye.field_150922_c.length) {
                tColor = ItemDye.field_150922_c[tColor];
                GL11.glColor4f(((tColor >> 16) & 255) / 255.0F, ((tColor >> 8) & 255) / 255.0F, (tColor & 255) / 255.0F, 1.0F);
            } else GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility)
    {
        currentVisibility.showNEI = false;
        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return Collections.emptyList();
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui)
    {
        return null;
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button)
    {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
    {
        return true;
    }
}
