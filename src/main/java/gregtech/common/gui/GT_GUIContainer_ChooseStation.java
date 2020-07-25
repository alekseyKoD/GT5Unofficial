package gregtech.common.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

public class GT_GUIContainer_ChooseStation extends GuiContainer implements INEIGuiHandler {

    GT_Recipe_ResearchStation mRecipe;
    EntityPlayer mPlayer;
    IGregTechTileEntity terminal;
    ResourceLocation mBackgrpund;
    public int mWidth = 256, mHeight = 190, mTop, mLeft;
    String[] mDesription;
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    private int buttonX = 180, buttonY = 120;

    int researchState =0;

    public GT_GUIContainer_ChooseStation(GT_Recipe_ResearchStation aRecipe, EntityPlayer aPlayer, IGregTechTileEntity aTerminal){
        super(null);
        mRecipe = aRecipe;
        mPlayer = aPlayer;
        terminal = aTerminal;
        mBackgrpund =  new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/researches/ResearchPage.png");
        mDesription = aRecipe.mDescription.mRecipePageText;
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
        if(researchState!=1)
            return;
        int tDrawX = buttonX-78, tDrawY = buttonY-40;
        Minecraft.getMinecraft().renderEngine.bindTexture(mBackgrpund);
        GL11.glPushMatrix();
        //GL11.glTranslatef(buttonX, buttonY, 1.0F);
        GL11.glColor4f(1f,1f,1f,1f);
        drawTexturedModalRect(tDrawX,tDrawY,0,221,80,35);
        GL11.glPopMatrix();

        if(isMouseOverRect(mx,my,buttonX,buttonY,80,35)){
            drawGradientRect(tDrawX,tDrawY,tDrawX+80,tDrawY+35,(20<<16)+(50<<8)+(50)+(100<<24),(20<<16)+(50<<8)+(50)+(100<<24));
            //drawGradientRect
            //drawToolTip
            if(Mouse.isButtonDown(0)){
                //startResearch
            }
        }
    }

    public void drawTexts(){
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

        switch (researchState) {
            case 0 :
                y += 30;
                aCol = (200 << 16) + (20 << 8) + (50) + (255 << 24);
                fr.drawString("Research is unavailable", x, y, aCol);
                break;
            case 1 :
                y += 30;
                aCol = (20 << 16) + (200 << 8) + (50) + (255 << 24);
                fr.drawString("Research is available", x, y, aCol);
                break;
            case 2 :
                y += 30;
                aCol = (150 << 16) + (200 << 8) + (50) + (255 << 24);
                fr.drawString("Research is completed!", x, y, aCol);
                break;
        }
        if(researchState!=1)
            return;

        y+=40;
        x+=4;
        aCol =(20<<16)+(200<<8)+(250)+(255<<24);
        fr.drawString("Start Research",x,y,aCol);
    }

    public boolean isMouseOverRect(int mx, int my, int aX, int aY, int xOffset, int yOffset){
        int mX = mx-mLeft;
        int mY = my-mTop;
        if(mX>=aX&&mX<=aX+xOffset && mY>=aY&&mY<=aY+yOffset)
            return true;
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
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
        //Minecraft.getMinecraft().displayGuiScreen(new GT_GUIContainer_ComputerTerminal(mPlayer, terminal));
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
