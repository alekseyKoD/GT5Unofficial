package gregtech.common.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.net.GT_Packet_StartResearch;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_ComputerTerminal;
import gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_ComputerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static gregtech.api.enums.GT_Values.NW;

public class GT_GUIContainer_Research extends GuiContainer implements INEIGuiHandler {

    GT_Recipe_ResearchStation mRecipe;
    EntityPlayer mPlayer;
    IGregTechTileEntity terminal;
    ResourceLocation mBackgrpund;
    public int mWidth = 256, mHeight = 190, mTop, mLeft;
    String[] mDesription;
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    IInventory mCopyFrom;

    private int  buttonX = 100, buttonY = 70;
    private boolean alreadySent= false;

    public HashSet<Integer> mCompletedResearches,mProcessingResearches, mUnsavedResearches;


    int researchState =0, errorState = 0;
    HashMap<Integer, Integer> mResearchProgressMap;

    public  GT_GUIContainer_Research(GT_Recipe_ResearchStation aRecipe, EntityPlayer aPlayer, IGregTechTileEntity aTerminal, HashSet<Integer> aCompleted, HashSet<Integer> aProcessing, HashSet<Integer> aUnsaved, HashMap<Integer,Integer> aMap, IInventory aCopyFrom){
        super(new GT_Container_Research(aPlayer.inventory,setSlots(aRecipe)));
        mRecipe = aRecipe;
        mPlayer = aPlayer;
        terminal = aTerminal;
        mBackgrpund =  new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/researches/ResearchPage.png");
        mDesription = aRecipe.mDescription.mRecipePageText;
        mCopyFrom = aCopyFrom;
        mResearchProgressMap = aMap;
        mCompletedResearches = aCompleted;
        mProcessingResearches = aProcessing;
        mUnsavedResearches = aUnsaved;
    }

    protected static GT_Container_Research.GT_SlotDefinition[] setSlots(GT_Recipe_ResearchStation aRecipe){
        GT_Container_Research.GT_SlotDefinition[] aOut = new GT_Container_Research.GT_SlotDefinition[aRecipe.mTargetRecipes.length+1];
        aOut[0] = new GT_Container_Research.GT_SlotDefinition(aRecipe.mDataOrb,152,-20);
        for(int i = 1; i < aRecipe.mTargetRecipes.length+1; i++){
            ItemStack is = aRecipe.mTargetRecipes[i-1].mOutputs[0].copy();
            is.stackSize = 1;
            aOut[i] = new GT_Container_Research.GT_SlotDefinition(is, -60+18*(i-1),170);
        }
        return aOut;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      //  GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        super.drawScreen(p_73863_1_, p_73863_2_,p_73863_3_);




    }

    public void drawBackground(){
        float transX = ((float)this.width - (float)mWidth * 1.3F) / 2.0F;
        float transY = ((float)this.height - (float)mHeight * 1.3F) / 2.0F;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(mBackgrpund);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslatef(transX, transY, 1.0F);
        mTop = (int)transY; mLeft = (int)transX;
        GL11.glEnable(3042);
        GL11.glScalef(1.3F, 1.3F, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, mWidth, mHeight);
        GL11.glPopMatrix();
    }

    public void drawStartButton(int mx, int my){
        if(researchState == 1 || researchState == 3) {
            int bSizeX = 120, bSizeY = 45;
            int tDrawX = buttonX , tDrawY = buttonY;
            Minecraft.getMinecraft().renderEngine.bindTexture(mBackgrpund);
            GL11.glPushMatrix();
            GL11.glColor4f(1f, 1f, 1f, 1f);
            drawTexturedModalRect(buttonX, buttonY, 0, 211, bSizeX, bSizeY);
            GL11.glPopMatrix();

            if(Mouse.isButtonDown(0))
                System.out.println(mx+" "+my);

            if (isMouseOverRect(mx, my, buttonX, buttonY, bSizeX, bSizeY)) {
                drawGradientRect(tDrawX, tDrawY, tDrawX + bSizeX, tDrawY + bSizeY, (20 << 16) + (50 << 8) + (50) + (100 << 24), (20 << 16) + (50 << 8) + (50) + (100 << 24));
                if (Mouse.isButtonDown(0)) {
                    if (!alreadySent && researchState == 1) {
                        NW.sendToServer(new GT_Packet_StartResearch(terminal.getWorld().provider.dimensionId, terminal.getXCoord(), terminal.getYCoord(), terminal.getZCoord(), 1, mRecipe.mID));
                        alreadySent = true;
                    }
                    else if(researchState == 3 && mCopyFrom!=null){
                        Minecraft.getMinecraft().displayGuiScreen(new GT_GUIContainer_SaveInformation(mRecipe, mPlayer, terminal, mCopyFrom));
                    }
                }
            }
        }
        else if(researchState == 2){
            int tDrawX = buttonX, tDrawY = buttonY;
            int bSizeX = 120, bSizeY = 45;
            Minecraft.getMinecraft().renderEngine.bindTexture(mBackgrpund);
            GL11.glPushMatrix();
            GL11.glColor4f(1f, 1f, 1f, 1f);
            drawTexturedModalRect(buttonX, buttonY, 0, 211, bSizeX, bSizeY);
            GL11.glPopMatrix();
            int aValue = mResearchProgressMap.get(mRecipe.mID)==null?0:mResearchProgressMap.get(mRecipe.mID);
            int aCol = (aValue&128)>0?(5 << 16) + (180 << 8) + (50) + (100 << 24):(180 << 16) + (120 << 8) + (1) + (100 << 24);
            int xLength = (int)((float)(aValue&127)/100f*bSizeX);
            drawGradientRect(tDrawX, tDrawY, tDrawX + xLength, tDrawY + bSizeY, aCol, aCol);
        }

    }

    public void drawTexts(){
        errorState = ((GT_MetaTileEntity_ComputerTerminal)terminal.getMetaTileEntity()).mErrorState;
        GL11.glDisable(GL11.GL_LIGHTING);
        int y = -35;
        int x  =-70;
        for(int i =0 ; i < mDesription.length; i++) {
            int aCol = (20<<16)+(120<<8)+(200)+(255<<24);
            if(i ==0)
                aCol = (20<<16)+(50<<8)+(220)+(255<<24);
            fr.drawString(mDesription[i],x, y,aCol);
            y+= fr.FONT_HEIGHT+2;
        }

        x = 100;
        y= 0;
        int aCol = (20<<16)+(120<<8)+(200)+(255<<24);
        fr.drawString("research orb",x, y,aCol); y+= fr.FONT_HEIGHT+2;
        fr.drawString("'r' to show a research",x,y,aCol); y+= fr.FONT_HEIGHT+2;
        fr.drawString( "'u' to show a recipe",x,y,aCol);
        y += 30;
        switch (researchState) {
            case 0 :
                aCol = (200 << 16) + (20 << 8) + (50) + (255 << 24);
                fr.drawString("Research is unavailable", x, y, aCol);
                break;
            case 1 :
                aCol = (20 << 16) + (200 << 8) + (50) + (255 << 24);
                fr.drawString("Research is available", x, y, aCol);
                break;
            case 2 :
                int aValue = mResearchProgressMap.get(mRecipe.mID)==null?0:mResearchProgressMap.get(mRecipe.mID);
                 aCol = (aValue&128)>0?(20 << 16) + (200 << 8) + (50) + (255 << 24):(180 << 16) + (120 << 8) + (1) + (100 << 24);
                 if((aValue&128)>0)
                    fr.drawString("Research is processing", x, y, aCol);
                 else
                    fr.drawString("Research is paused", x, y, aCol);
                break;
            case 3:
                aCol = (150 << 16) + (200 << 8) + (50) + (255 << 24);
                fr.drawString("Research is completed!", x, y, aCol);
                break;
            case 4:
                aCol = (255 << 16) + (99 << 8) + (0) + (255 << 24);
                fr.drawString("Research is not saved!", x, y, aCol);
                y += 10;
                fr.drawString("Construction data may be lost", x, y, aCol);
                y+=10;
                fr.drawString("Add more data storage to", x, y, aCol);
                y+=10;
                fr.drawString("your computers", x, y, aCol);
                return;
        }
        y+=35;
        x+=6;
        if(researchState == 1&&errorState == 0)
            fr.drawString("Start Research",x,y,aCol);
        else if(errorState == 1){
            y-=4;
            aCol =(200<<16)+(20<<8)+(50)+(255<<24);
            fr.drawString("No Research Station",x,y,aCol);
            y+=10;
            fr.drawString("is available",x,y,aCol);
        }
        else if(errorState == 2){
            y-=4;
            aCol =(200<<16)+(20<<8)+(50)+(255<<24);
            fr.drawString("Data System",x,y,aCol);
            y+=10;
            fr.drawString("is not connected",x,y,aCol);
        }
        else if(researchState==3) {
            if(mCopyFrom == null){
                aCol =(200<<16)+(20<<8)+(50)+(255<<24);
                fr.drawString("No data hatch", x, y, aCol);
            }
            else {
                aCol =(20<<16)+(200<<8)+(250)+(255<<24);
                fr.drawString("Save information", x, y, aCol);
            }
        }
    }

    public boolean isMouseOverRect(int mx, int my, int aX, int aY, int xOffset, int yOffset){
        int mX = mx-guiLeft;
        int mY = my-guiTop;
        if(mX>=aX&&mX<=aX+xOffset && mY>=aY&&mY<=aY+yOffset)
            return true;
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        researchState = mUnsavedResearches.contains(mRecipe.mID)?4:mCompletedResearches.contains(mRecipe.mID)?3:mProcessingResearches.contains(mRecipe.mID)?2:1;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1f,1f,1f,1f);
        drawBackground();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
        zLevel = 10;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        zLevel = 11;

        drawStartButton(p_146979_1_, p_146979_2_);
        drawTexts();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        //super.onGuiClosed();
        //Minecraft.getMinecraft().displayGuiScreen(new GT_GUIContainer_ComputerTerminal(mPlayer, terminal,mCompletedResearches,mProcessingResearches,mResearchProgressMap,new ArrayList<Integer>(),mCopyFrom));
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
