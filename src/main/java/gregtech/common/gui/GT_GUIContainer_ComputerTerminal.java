package gregtech.common.gui;

import cpw.mods.fml.client.FMLClientHandler;
import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation.GT_ResearchDescription;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;


public class GT_GUIContainer_ComputerTerminal extends GuiScreen {

    public int mItemOffset = 4, mLineOffset = 12;
    public ResourceLocation mGUIbackground;
    public int mWidth = 256, mHeight = 190;
    public int mMapX = 0, mMapY=0;
    public int xMapSize = 240, yMapSize = 180;
    public int mTop,mLeft,mRight,mDown;
    public ArrayList<GT_ResearchDescription> mResearchesOnPage = new ArrayList<>();
    public ResourceLocation mSlot,mGui,mBorders;
    public int mLastMouseX = 0,mLastMouseY = 0;
    public ArrayList<GT_Recipe_ResearchStation>[] pages = new ArrayList[3];
    public HashSet<Integer> mCompletedResearches,mProcessingResearches;
    public HashMap<Integer,Integer> mResearchProgress ;
    HashSet<GT_ResearchDescription> mUnlockedRecipes = new HashSet<>();
    public HashSet<Integer> mUnsaved;
    int currentPage = 0;
    public ArrayList<GT_Recipe_ResearchStation> mRecipesOnCurrentPage = new ArrayList<>();
    ResourceLocation[] pageIcons = new ResourceLocation[3];
    private int r = 0;

    public EntityPlayer mPlayer;
    public IGregTechTileEntity mTerminal;
    public IInventory mCopyFrom;

    private GT_ResearchDescription mMouseWasDown = null;

    public GT_GUIContainer_ComputerTerminal(EntityPlayer aPlayer, IGregTechTileEntity terminal, HashSet<Integer> aCompleted, HashSet<Integer> aProcessing, HashMap<Integer,Integer> aProgress, HashSet<Integer> aUnsaved, IInventory aCopyFrom){
        mGUIbackground = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/ComputerTerminal.png");
        mSlot = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/ComputerTerminalSlot.png");
        mGui = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/gui_research.png");
        pageIcons[0] = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/researches/page1.png");
        pageIcons[1] = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/researches/page2.png");
        pageIcons[2] = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/researches/page3.png");
        mBorders = new ResourceLocation(GT_Values.RES_PATH_GUI+"basicmachines/researches/terminalBorders.png");
        mPlayer = aPlayer;
        mTerminal = terminal;
        mCompletedResearches = aCompleted;
        mProcessingResearches = aProcessing;
        mUnsaved = aUnsaved;
        mCopyFrom = aCopyFrom;
        mResearchProgress = aProgress;

    }


    protected void keyTyped(char par1, int par2) {
       // if (par2 != this.mc.gameSettings.keyBindInventory.getKeyCode() && par2 != 1) {
            super.keyTyped(par1, par2);
       /* } else {
            //this.mc.displayGuiScreen(new GuiResearchBrowser(this.guiMapX, this.guiMapY));
        }*/

    }

    @Override
    public void drawScreen(int aX, int aY, float p_73863_3_) { ;
        mUnlockedRecipes.clear();
        zLevel = 0;
        GL11.glDisable(GL11.GL_BLEND);
        drawDefaultBackground();
       // zLevel= 20;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(518);
        //GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(3553);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glPushMatrix();


        drawResearchBackground(aX,aY);

        GL11.glScalef(0.5F, 0.5F, 1.0F);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);

        zLevel = 15;

        drawResearches();
        drawPages();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        float transX = ((float)this.width - (float)mWidth * 1.3F) / 2.0F;
        float transY = ((float)this.height - (float)mHeight * 1.3F) / 2.0F;
        mTop = (int)transY;
        mLeft = (int)transX;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(mBorders);
        GL11.glPushMatrix();
        GL11.glTranslatef(transX, transY, 1.0F);
        GL11.glEnable(3042);
        GL11.glScalef(1.3F, 1.3F, 1.0F);
        zLevel = 100;
        this.drawTexturedModalRect(0, 0, 0, 0, mWidth, mHeight);
        GL11.glPopMatrix();

        if(aX>=mLeft&&aX<=(mLeft+mWidth*1.3)&&aY>=mTop&&aY<=(mTop+mHeight*1.3))
        for(GT_ResearchDescription aResearch : mResearchesOnPage){
            if(isMouseOverQuad(aX,aY,aResearch.mapX, aResearch.mapY, 25)){

                zLevel = 4;
               // GL11.glEnable(GL11.GL_DEPTH_TEST);
                drawGradientRect(getX(aResearch.mapX)+1,getY(aResearch.mapY)+1,getX(aResearch.mapX)+24,getY(aResearch.mapY)+24,0,200+(100<<8)+(0<<16)+(255<<24));
                zLevel = 180;
                if(mUnlockedRecipes.contains(aResearch))
                    drawToolTip(aX, aY, aResearch.mDescription);
                else {
                    String[] aDesc = new String[aResearch.mDependencies.size()+1];
                    aDesc[0] = EnumChatFormatting.RED + "not unlocked"+ EnumChatFormatting.RESET;
                    for(int i = 1; i < aDesc.length; i++){
                        aDesc[i] = "Research required: " +aResearch.mDependencies.get(i-1).mDescription[0];
                    }
                    drawToolTip(aX, aY, aDesc);
                }
                if(Mouse.isButtonDown(0)) {
                    mMouseWasDown = aResearch;
                }
                else if( mMouseWasDown == aResearch&&mUnlockedRecipes.contains(aResearch)){
                    Minecraft.getMinecraft().displayGuiScreen(new GT_GUIContainer_Research(GT_Recipe_ResearchStation.mIDtoRecipeMap.get(aResearch.mResearchID),mPlayer,mTerminal,mCompletedResearches,mProcessingResearches, mUnsaved,mResearchProgress,mCopyFrom));
                }
                else{
                    mMouseWasDown = null;
                }
            }
        }

        for(int i = 0; i < pageIcons.length; i++){
            if(Mouse.isButtonDown(0)&&isMouseOverQuad(aX-mMapX,aY-mMapY,-25,25*i,25)){
                currentPage = i;
            }
        }


        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        zLevel=16;






        this.zLevel = 0.0F;
        GL11.glDepthFunc(515);

    }

    public void drawPages(){
        for(int i = 0; i < pages.length; i++){
            if(currentPage == i){
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPushMatrix();
                GL11.glTranslatef(mLeft-25, mTop+25*i, 1.0F);
                GL11.glEnable(3042);
                GL11.glScalef(0.1F,.1F,1.f);
                Minecraft.getMinecraft().renderEngine.bindTexture(pageIcons[i]);
                float a = zLevel;
                zLevel = 999;

                drawTexturedModalRect(0,0,0,0,256,256);

                GL11.glPopMatrix();
                GL11.glPushMatrix();
                zLevel = 1000;

                drawGradientRect(mLeft-25, mTop+25*i,mLeft,mTop+25*i+25,255+(255<<8)+(255<<16)+(120<<24),255+(255<<8)+(255<<16)+(120<<24));
                GL11.glColor4f(1.0F, 1.0F, 1.0F, .1F);
                zLevel = a;
                GL11.glPopMatrix();
                //draw selected
            }
            else{
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPushMatrix();
                GL11.glTranslatef(mLeft-25, mTop+25*i, 1.0F);
                GL11.glEnable(3042);
                GL11.glScalef(0.1F,.1F,1.f);
                Minecraft.getMinecraft().renderEngine.bindTexture(pageIcons[i]);
                float a = zLevel;
                zLevel = 1000;

                drawTexturedModalRect(0,0,0,0,256,256);

                zLevel = a;
                GL11.glPopMatrix();
                //draw simple
            }
        }
    }

    @Override
    public void updateScreen() {

        if(Mouse.isButtonDown(0)) {
            if (mLastMouseY == 0 || mLastMouseX == 0) {
                mLastMouseY = Mouse.getY();
                mLastMouseX = Mouse.getX();
            }

            int x = Mouse.getX() - mLastMouseX;
            int y = Mouse.getY() - mLastMouseY;
            x/=2;
            y/=2;
            mLastMouseY = Mouse.getY();
            mLastMouseX = Mouse.getX();
            mMapX -= x;
            mMapY += y;
        }





        super.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        super.actionPerformed(p_146284_1_);
    }

    @Override
    protected void mouseClicked(int x, int y, int p_73864_3_) {

        super.mouseClicked(x, y, p_73864_3_);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {

        super.mouseClickMove(mouseX, mouseY, button,time);
    }

    @Override
    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_) {
        mLastMouseX =0; mLastMouseY = 0;
        super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    @Override
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        super.drawTexturedModalRect(x, y, u, v, width, height);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void drawResearches(){
        mResearchesOnPage.clear();
        for(GT_ResearchDescription aRecipe : GT_Recipe_ResearchStation.mPageNoDependanciesRecipes[currentPage])
            drawPage(aRecipe);
    }

    protected void drawResearchBackground(int aX, int aY){
        float transX = ((float)this.width - (float)mWidth * 1.3F) / 2.0F;
        float transY = ((float)this.height - (float)mHeight * 1.3F) / 2.0F;
        mTop = (int)transY;
        mLeft = (int)transX;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(mGUIbackground);
        GL11.glPushMatrix();
        GL11.glTranslatef(transX, transY, 1.0F);
        GL11.glEnable(3042);
        GL11.glScalef(1.3F, 1.3F, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, mWidth, mHeight);
        GL11.glPopMatrix();
    }

    public int getX(int aGlobalX){
        return mLeft+aGlobalX-mMapX;
    }

    public int getY(int aGlobalY){
        return mTop+aGlobalY-mMapY;
    }

    public boolean isMouseOverQuad(int mx, int my, int aMapX, int aMapY, int offset){
        int mX = mx-mLeft+mMapX;
        int mY = my-mTop+mMapY;
        if(mX>=aMapX&&mX<=aMapX+offset && mY>=aMapY&&mY<=aMapY+offset)
            return true;
        return false;
    }

    public void drawSlot(float x, float y){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(mSlot);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, .0F);
        GL11.glEnable(3042);
        GL11.glScalef(.1F, .1F, 1F);
        zLevel = 3;
        this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        GL11.glPopMatrix();
    }

    private void drawLine(int x, int y, int x2, int y2, float r, float g, float b, int mode) {//mode : 0 - closed, 1 - avaible, 2 - processing
        float asd = zLevel;
        zLevel =4;
        int time = FMLClientHandler.instance().getClient().thePlayer.ticksExisted;
        if(mode == 4)
            time = 12345;
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569F);
        GL11.glDisable(3553);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        double d3 = (double)(x - x2);
        double d4 = (double)(y - y2);
        float dist = MathHelper.sqrt_double(d3 * d3 + d4 * d4);
        int inc = (int)(dist / 2.0F);
        float dx = (float)(d3 / (double)inc);
        float dy = (float)(d4 / (double)inc);
        if (Math.abs(d3) > Math.abs(d4)) {
            dx *= 2.0F;
        } else {
            dy *= 2.0F;
        }

        GL11.glLineWidth(3.0F);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        tessellator.startDrawing(3);

        int[] c = new int[50];
        int size = 0;
        for(int i = 0 ; i <= inc; i++){
            if(Math.sin((double)((double)time/2+i))>0.7f){
                c[size] = i;
                size++;
                i+=5;
            }
        }
        size = 0;
        int find = -1;
        for(int a = 0; a <= inc; ++a) {
            float r2 = r;
            float g2 = g;
            float b2 = b;
            int gg = 0;
            int rr = 0;
            int bb = 0;
            float mx = 0.0F;
            float my = 0.0F;
            float op = 0.6F;

            double off = 0;
            if(mode == 2||mode == 4){
                if(a==c[size]){
                    size++;
                    find = 3;
                }

            }
            if(find>0){
                bb=200;
                gg = 100;
                find--;
            }

            if(mode == 1){
                if(Math.sin((double)time/3)>=0) {
                    bb = 200;
                    gg = 100;
                }
            }
            if(mode == 3){
                bb=200;
                gg = 100;
            }

            if(mode == 0){
                rr = 15;
                gg = 15;
                bb = 15;
            }
            tessellator.setColorRGBA(rr,gg,bb,255);
            tessellator.addVertex((double)((float)x - dx * (float)a + mx), (double)((float)y - dy * (float)a + my), zLevel);
            if (Math.abs(d3) > Math.abs(d4)) {
                dx *= 1.0F - 1.0F / ((float)inc * 3.0F / 2.0F);
            } else {
                dy *= 1.0F - 1.0F / ((float)inc * 3.0F / 2.0F);
            }
        }

        tessellator.draw();
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2848);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(32826);
        GL11.glEnable(3553);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glPopMatrix();
        zLevel = asd;
    }

    public void drawPage(GT_ResearchDescription aMainRecipe){
        r=3;
        drawRecipe(aMainRecipe);
    }

    public void drawRecipe(GT_ResearchDescription aRecipe){
        boolean tUnlocked = checkDependancies(aRecipe);
        boolean tCompleted = mCompletedResearches.contains(aRecipe.mResearchID);
        boolean tUnsaved = mUnsaved.contains(aRecipe.mResearchID);
        if(tUnlocked)
            mUnlockedRecipes.add(aRecipe);
        mResearchesOnPage.add(aRecipe);
     //   GL11.glEnable(GL11.GL_DEPTH_TEST);
       // GL11.glEnable(GL11.GL_BLEND);
        zLevel = 8;
        drawSlot(getX(aRecipe.mapX), getY((aRecipe.mapY)));
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
   //     GL11.glPushMatrix();
      //  GL11.glEnable(GL11.GL_BLEND);
       // GL11.glBlendFunc(770, 771);
       /* RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);*/


        RenderItem mRenderer = new RenderItem();
        mRenderer.zLevel = 12;

        if(tUnlocked)
            mRenderer.renderWithColor = true;
        else
            GL11.glColor4f(.1f,.1f,.1f,1f);
        //if recipe unlocked use colors
        mRenderer.renderWithColor = mUnlockedRecipes.contains(aRecipe)||mCompletedResearches.contains(aRecipe.mResearchID);
        /*if(mCompletedResearches.contains(aRecipe.mID))
            mRenderer.renderWithColor = true;*/
        mRenderer.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,aRecipe.mShownItem, getX(aRecipe.mapX)+mItemOffset, getY(aRecipe.mapY)+mItemOffset);

      //  GL11.glDisable(GL11.GL_LIGHTING);

  //      GL11.glEnable(GL11.GL_DEPTH_TEST);
     //   GL11.glDisable(GL11.GL_BLEND);
    //    GL11.glPopMatrix();
        if(tCompleted){
            drawGradientRect(getX(aRecipe.mapX)+1,getY(aRecipe.mapY)+1,getX(aRecipe.mapX)+24,getY(aRecipe.mapY)+24,30+(210<<8)+(10<<16)+(180<<24),30+(210<<8)+(10<<16)+(180<<24));

        }

        if(tUnsaved&&tUnlocked){
            drawGradientRect(getX(aRecipe.mapX)+1,getY(aRecipe.mapY)+1,getX(aRecipe.mapX)+24,getY(aRecipe.mapY)+24,(255<<16)+(99<<8)+0+(180<<24),(255<<16)+(99<<8)+0+(180<<24));
        }

        for(GT_ResearchDescription tRecipe : aRecipe.mDependedRecipes){
            drawConnection(tRecipe,aRecipe, tUnlocked);
            drawRecipe(tRecipe);
        }
    }

    public void drawConnection(GT_ResearchDescription aRecipe /*dependent recipe*/, GT_ResearchDescription tRecipe /*recipe*/, boolean aUnlocked){
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        if(mCompletedResearches.contains(tRecipe.mResearchID)) {// 3 - completed, 2 - researching, 1 - available, 0 - unavailable
            if (mCompletedResearches.contains(aRecipe.mResearchID))
                r = 3;
            else if (mProcessingResearches.contains(aRecipe.mResearchID)) {
                int aProgress = mResearchProgress.get(aRecipe.mResearchID)== null?0:mResearchProgress.get(aRecipe.mResearchID);
                if((aProgress&128)>0)
                    r = 2;
                else
                    r = 4;
            }
            else if (aUnlocked)
                r = 1;
        }
        else
            r = 0;

/*
        if(aUnlocked){
            r = 1;
            mUnlockedRecipes.add(tRecipe);
        }
        else if(mCompletedResearches.contains(tRecipe.mID))
            if(mCompletedResearches.contains(aRecipe.mID))
                r = 3;
            else
                if(mProcessingResearches.contains(aRecipe.mID))
                    r = 2;

        else
            r = 0;*/
        drawLine(getX(aRecipe.mapX)+mLineOffset,getY(aRecipe.mapY)+mLineOffset,getX(tRecipe.mapX)+mLineOffset,getY(tRecipe.mapY)+mLineOffset,0.9F,.9F,1.0F,r);
        r = 0;
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void drawToolTip(int aGlobalX, int aGlobalY, String[] aDescription){
        if(aDescription == null)
            aDescription = new String[]{"Default desc"};

        FontRenderer fr  = Minecraft.getMinecraft().fontRenderer;
        drawHoveringText(Arrays.asList(aDescription),aGlobalX,aGlobalY,fr);

    }

    public boolean checkDependancies(GT_ResearchDescription aRecipe){
        for(GT_ResearchDescription tRecipe:aRecipe.mDependencies){
            if(!mCompletedResearches.contains(tRecipe.mResearchID))
                return false;
        }
        return true;
    }

}
