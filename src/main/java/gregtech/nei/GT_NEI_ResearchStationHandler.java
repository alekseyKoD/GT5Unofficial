package gregtech.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.gui.GT_GUIContainer_BasicMachine;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_WithAlt;
import gregtech.api.util.GT_Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import scala.actors.threadpool.Arrays;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class GT_NEI_ResearchStationHandler
        extends TemplateRecipeHandler {
    public static final int sOffsetX = 5;
    public static final int sOffsetY = 11;

    static {
        GuiContainerManager.addInputHandler(new GT_RectHandler());
        GuiContainerManager.addTooltipHandler(new GT_RectHandler());
    }

    protected final GT_Recipe.GT_Recipe_Map mRecipeMap;

    protected boolean mIsEcelctric;

    public GT_NEI_ResearchStationHandler(GT_Recipe.GT_Recipe_Map aRecipeMap, boolean aIsElectrcic) {//this is called when recipes should be shown
        this.mRecipeMap = aRecipeMap;
        this.mIsEcelctric = aIsElectrcic;
        this.transferRects.add(new RecipeTransferRect(new Rectangle(138, 18, 18, 18), getOverlayIdentifier(), new Object[0]));
        if (!NEI_GT_Config.sIsAdded) {
            FMLInterModComms.sendRuntimeMessage(GT_Values.GT, "NEIPlugins", "register-crafting-handler", "gregtech@" + getRecipeName() + "@" + getOverlayIdentifier());
            GuiCraftingRecipe.craftinghandlers.add(this);
            GuiUsageRecipe.usagehandlers.add(this);
        }
    }

    public static void drawText(int aX, int aY, String aString, int aColor) {
        Minecraft.getMinecraft().fontRenderer.drawString(aString, aX, aY, aColor);
    }

    public TemplateRecipeHandler newInstance() {
        if(this.mIsEcelctric) {
            NEI_GT_Config.RSH = new GT_NEI_ResearchStationHandler(this.mRecipeMap, this.mIsEcelctric);
            return NEI_GT_Config.RSH;
        }
        else {
            NEI_GT_Config.PRSH = new GT_NEI_ResearchStationHandler(this.mRecipeMap, this.mIsEcelctric);
            return NEI_GT_Config.PRSH;
        }
    }

    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (GT_Recipe tRecipe : this.mRecipeMap.mRecipeList) {
                if (!tRecipe.mHidden) {
                    this.arecipes.add(new CachedDefaultRecipe(tRecipe,this.mIsEcelctric));
                }else{
                    this.arecipes.remove(new CachedDefaultRecipe(tRecipe,this.mIsEcelctric));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(ItemStack aResult) {
        ItemData tPrefixMaterial = GT_OreDictUnificator.getAssociation(aResult);

        ArrayList<ItemStack> tResults = new ArrayList();
        tResults.add(aResult);
        tResults.add(GT_OreDictUnificator.get(true, aResult));
        if ((tPrefixMaterial != null) && (!tPrefixMaterial.mBlackListed) && (!tPrefixMaterial.mPrefix.mFamiliarPrefixes.isEmpty())) {
            for (OrePrefixes tPrefix : tPrefixMaterial.mPrefix.mFamiliarPrefixes) {
                tResults.add(GT_OreDictUnificator.get(tPrefix, tPrefixMaterial.mMaterial.mMaterial, 1L));
            }
        }
        if(GT_Utility.areStacksEqual(aResult, ItemList.Tool_DataCluster.get(1L),true)){
            if(!mIsEcelctric) return;
            for(GT_Recipe tRecipe : mRecipeMap.mRecipeList){
                if(checkIfDataItemContainsItems(aResult,tRecipe.mOutputs[0]))
                    arecipes.add(new CachedDefaultRecipe(tRecipe,this.mIsEcelctric));
            }
            return;
        }
       /* if(GT_Utility.areStacksEqual(aResult, ItemList.EngineersBook.get(1L),true)){
            if(mIsEcelctric) return;
            for(GT_Recipe tRecipe : mRecipeMap.mRecipeList){
                if(checkIfDataItemContainsItems(aResult,tRecipe.mOutputs[0]))
                    arecipes.add(new CachedDefaultRecipe(tRecipe,this.mIsEcelctric));
            }
            return;
        }*/
        FluidStack tFluid = GT_Utility.getFluidForFilledItem(aResult, true);
        if (tFluid != null) {
            tResults.add(GT_Utility.getFluidDisplayStack(tFluid, false));
            for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (tData.fluid.isFluidEqual(tFluid)) {
                    tResults.add(GT_Utility.copy(new Object[]{tData.filledContainer}));
                }
            }
        }
        for (GT_Recipe tRecipe : this.mRecipeMap.mRecipeList) {
            if (!tRecipe.mHidden) {
                CachedDefaultRecipe tNEIRecipe = new CachedDefaultRecipe(tRecipe,this.mIsEcelctric);
                for (ItemStack tStack : tResults) {
                    if (tNEIRecipe.contains(tNEIRecipe.mOutputs, tStack)) {
                        this.arecipes.add(tNEIRecipe);
                        break;
                    }
                }
            }else{
                CachedDefaultRecipe tNEIRecipe = new CachedDefaultRecipe(tRecipe,this.mIsEcelctric);
                for (ItemStack tStack : tResults) {
                    if (tNEIRecipe.contains(tNEIRecipe.mOutputs, tStack)) {
                        this.arecipes.remove(tNEIRecipe);
                        break;
                    }
                }
            }
        }
        CachedDefaultRecipe tNEIRecipe;
    }

    public void loadUsageRecipes(ItemStack aInput) {
        ItemData tPrefixMaterial = GT_OreDictUnificator.getAssociation(aInput);

        ArrayList<ItemStack> tInputs = new ArrayList();
        tInputs.add(aInput);
        tInputs.add(GT_OreDictUnificator.get(false, aInput));
        if ((tPrefixMaterial != null) && (!tPrefixMaterial.mPrefix.mFamiliarPrefixes.isEmpty())) {
            for (OrePrefixes tPrefix : tPrefixMaterial.mPrefix.mFamiliarPrefixes) {
                tInputs.add(GT_OreDictUnificator.get(tPrefix, tPrefixMaterial.mMaterial.mMaterial, 1L));
            }
        }
        FluidStack tFluid = GT_Utility.getFluidForFilledItem(aInput, true);
        if (tFluid != null) {
            tInputs.add(GT_Utility.getFluidDisplayStack(tFluid, false));
            for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (tData.fluid.isFluidEqual(tFluid)) {
                    tInputs.add(GT_Utility.copy(new Object[]{tData.filledContainer}));
                }
            }
        }
        for (GT_Recipe tRecipe : this.mRecipeMap.mRecipeList) {
            if (!tRecipe.mHidden) {
                CachedDefaultRecipe tNEIRecipe = new CachedDefaultRecipe(tRecipe,this.mIsEcelctric);
                for (ItemStack tStack : tInputs) {
                    if (tNEIRecipe.contains(tNEIRecipe.mInputs, tStack)) {
                        this.arecipes.add(tNEIRecipe);
                        break;
                    }
                }
            }else{
                CachedDefaultRecipe tNEIRecipe = new CachedDefaultRecipe(tRecipe,this.mIsEcelctric);
                for (ItemStack tStack : tInputs) {
                    if (tNEIRecipe.contains(tNEIRecipe.mInputs, tStack)) {
                        this.arecipes.remove(tNEIRecipe);
                        break;
                    }
                }
            }
        }
    }

    public String getOverlayIdentifier() {
        return this.mRecipeMap.mNEIName;
    }

    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(-4, -8, 1, 3, 174, mIsEcelctric?150:75);
    }

    public int recipiesPerPage() {
        return 1;
    }

    public String getRecipeName() {
        return GT_LanguageManager.getTranslation(this.mRecipeMap.mUnlocalizedName);
    }

    public String getGuiTexture() {
        return this.mRecipeMap.mNEIGUIPath;
    }

    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack aStack, List<String> currenttip, int aRecipeIndex) {
        CachedRecipe tObject = (CachedRecipe) this.arecipes.get(aRecipeIndex);
        if ((tObject instanceof CachedDefaultRecipe)) {
            CachedDefaultRecipe tRecipe = (CachedDefaultRecipe) tObject;
            for (PositionedStack tStack : tRecipe.mOutputs) {
                if (aStack == tStack.item) {
                    if ((!(tStack instanceof FixedPositionedStack)) || (((FixedPositionedStack) tStack).mChance <= 0) || (((FixedPositionedStack) tStack).mChance == 10000)) {
                        break;
                    }
                    currenttip.add("Chance: " + ((FixedPositionedStack) tStack).mChance / 100 + "." + (((FixedPositionedStack) tStack).mChance % 100 < 10 ? "0" + ((FixedPositionedStack) tStack).mChance % 100 : Integer.valueOf(((FixedPositionedStack) tStack).mChance % 100)) + "%");
                    break;
                }
            }
            for (PositionedStack tStack : tRecipe.mInputs) {
                if (aStack == tStack.item) {
                    if ((gregtech.api.enums.ItemList.Display_Fluid.isStackEqual(tStack.item, true, true)) ||
                            (tStack.item.stackSize != 0)) {
                        break;
                    }
                    currenttip.add("Does not get consumed in the process");
                    break;
                }
            }
        }
        return currenttip;
    }

    public static boolean checkIfDataItemContainsItems(ItemStack dataItem1, ItemStack dataItem2){ //item 1 can contain multiple items. item 2 only single
        if(dataItem1==null||dataItem2==null||dataItem1.getTagCompound()==null||dataItem2.getTagCompound()==null)
            return false;
        int[] res = new int[16];

        NBTTagCompound tag1 = dataItem1.getTagCompound();
        int t = tag1.getInteger("usedCapacity");
        for(int i = 0; i < t; i++){
            res[i] = tag1.getInteger("rID"+i);
        }
        NBTTagCompound tag2 = dataItem2.getTagCompound();
        if(tag2 == null)
            return false;
        int r2 = tag2.getInteger("rID0");
        if(r2==0)
            return false;
        for(int r1 : res){
            if(r1 == r2)
                return true;
        }
        return false;
    }

    public void drawExtras(int aRecipeIndex) {
        int tEUt = ((CachedDefaultRecipe) this.arecipes.get(aRecipeIndex)).mRecipe.mEUt;
        int tDuration = ((CachedDefaultRecipe) this.arecipes.get(aRecipeIndex)).mRecipe.mDuration;
        int tSpecial = ((CachedDefaultRecipe) this.arecipes.get(aRecipeIndex)).mRecipe.mSpecialValue;
        if (mIsEcelctric){
            drawText(80,80,"Total: "+ tEUt*tDuration + " EU",-16000016);
            drawText(80,90,"Usage: "+ tEUt + " EU/t",-16000016);
            drawText(80,100,"Computation: "+ tSpecial,-16000016);
            drawText(80,110,"Time: "+ tDuration/20 + " secs",-16000016);
        }
        else {
            drawText(5, 80, "Total: " + tEUt * tDuration + " EU", -16777216);
            drawText(5, 90, "Usage: " + tEUt + " EU/t", -16777216);
            drawText(5, 100, "Time: " + tDuration / 20 + " secs", -16777216);
        }
    }

    public static class GT_RectHandler
            implements IContainerInputHandler, IContainerTooltipHandler {
        public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
            //if (canHandle(gui)) {
            //    if (button == 0) {
            //        return transferRect(gui, false);
            //    }
            //    if (button == 1) {
            //        return transferRect(gui, true);
            //    }
            //}
            return false;
        }

        public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {
            return false;
        }

        public boolean canHandle(GuiContainer gui) {
            return false;
            //return (((gui instanceof GT_GUIContainer_BasicMachine)) && (GT_Utility.isStringValid(((GT_GUIContainer_BasicMachine) gui).mNEI)));
        }

        public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
            //if ((canHandle(gui)) && (currenttip.isEmpty())) {
            //    if (new Rectangle(138, 18, 18, 18).contains(new Point(GuiDraw.getMousePosition().x - ((GT_GUIContainer_BasicMachine) gui).getLeft() - codechicken.nei.recipe.RecipeInfo.getGuiOffset(gui)[0], GuiDraw.getMousePosition().y - ((GT_GUIContainer_BasicMachine) gui).getTop() - codechicken.nei.recipe.RecipeInfo.getGuiOffset(gui)[1]))) {
            //        currenttip.add("Recipes");
            //    }
            //}
            return currenttip;
        }

        private boolean transferRect(GuiContainer gui, boolean usage) {
            return (canHandle(gui)) && (new Rectangle(138, 18, 18, 18).contains(new Point(GuiDraw.getMousePosition().x - ((GT_GUIContainer_BasicMachine) gui).getLeft() - codechicken.nei.recipe.RecipeInfo.getGuiOffset(gui)[0], GuiDraw.getMousePosition().y - ((GT_GUIContainer_BasicMachine) gui).getTop() - codechicken.nei.recipe.RecipeInfo.getGuiOffset(gui)[1]))) && (usage ? GuiUsageRecipe.openRecipeGui(((GT_GUIContainer_BasicMachine) gui).mNEI, new Object[0]) : GuiCraftingRecipe.openRecipeGui(((GT_GUIContainer_BasicMachine) gui).mNEI, new Object[0]));

        }

        public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
            return currenttip;
        }

        public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey, List<String> currenttip) {
            return currenttip;
        }

        public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
            return false;
        }

        public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {
        }

        public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        }

        public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {
        }

        public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
            return false;
        }

        public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        }

        public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {
        }
    }

    public class FixedPositionedStack
            extends PositionedStack {
        public final int mChance;
        public boolean permutated = false;

        public FixedPositionedStack(Object object, int x, int y) {
            this(object, x, y, 0);
        }

        public FixedPositionedStack(Object object, int x, int y, int aChance) {
            super(object, x, y, true);
            this.mChance = aChance;
        }

        public void generatePermutations() {
            if (this.permutated) {
                return;
            }
            ArrayList<ItemStack> tDisplayStacks = new ArrayList();
            for (ItemStack tStack : this.items) {
                if (GT_Utility.isStackValid(tStack)) {
                    if (tStack.getItemDamage() == 32767) {
                        List<ItemStack> permutations = codechicken.nei.ItemList.itemMap.get(tStack.getItem());
                        if (!permutations.isEmpty()) {
                            ItemStack stack;
                            for (Iterator i$ = permutations.iterator(); i$.hasNext(); tDisplayStacks.add(GT_Utility.copyAmount(tStack.stackSize, new Object[]{stack}))) {
                                stack = (ItemStack) i$.next();
                            }
                        } else {
                            ItemStack base = new ItemStack(tStack.getItem(), tStack.stackSize);
                            base.stackTagCompound = tStack.stackTagCompound;
                            tDisplayStacks.add(base);
                        }
                    } else {
                        tDisplayStacks.add(GT_Utility.copy(new Object[]{tStack}));
                    }
                }
            }
            this.items = ((ItemStack[]) tDisplayStacks.toArray(new ItemStack[0]));
            if (this.items.length == 0) {
                this.items = new ItemStack[]{new ItemStack(Blocks.fire)};
            }
            this.permutated = true;
            setPermutationToRender(0);
        }
    }

    public class FixedPositionedMultiStack
            extends PositionedStack {
        protected GT_ItemStack[] stacks;
        public boolean permutated = true;

        public FixedPositionedMultiStack(Object object, int x, int y, GT_ItemStack... stacks) {
            this(object, x, y, 0, stacks);
        }

        public FixedPositionedMultiStack(Object object, int x, int y, int aChance, GT_ItemStack... stacks) {
           super(object,x,y,true);
           this.stacks = stacks;
           permutated = false;
           generatePermutations();
        }

        public void generatePermutations() {
            if (this.permutated) {
                return;
            }
            ArrayList<ItemStack> tDisplayStacks = new ArrayList();
            for (ItemStack tStack : this.items) {
                if (GT_Utility.isStackValid(tStack)) {
                    if (tStack.getItemDamage() == 32767) {
                        List<ItemStack> permutations = codechicken.nei.ItemList.itemMap.get(tStack.getItem());
                        if (!permutations.isEmpty()) {
                            ItemStack stack;
                            for (Iterator i$ = permutations.iterator(); i$.hasNext(); tDisplayStacks.add(GT_Utility.copyAmount(tStack.stackSize, new Object[]{stack}))) {
                                stack = (ItemStack) i$.next();
                            }
                        } else {
                            ItemStack base = new ItemStack(tStack.getItem(), tStack.stackSize);
                            base.stackTagCompound = tStack.stackTagCompound;
                            tDisplayStacks.add(base);
                        }
                    } else {
                        tDisplayStacks.add(GT_Utility.copy(new Object[]{tStack}));
                    }
                }
            }
            if(stacks!=null)
                for(GT_ItemStack is : stacks)
                    tDisplayStacks.add(is.toStack());
            this.items = ((ItemStack[]) tDisplayStacks.toArray(new ItemStack[0]));
            if (this.items.length == 0) {
                this.items = new ItemStack[]{new ItemStack(Blocks.fire)};
            }
            this.permutated = true;
            setPermutationToRender(0);
        }
    }
//0-13 iputs = inputs |14-17 = scanInputs| 0 out = data orb| 1 out = ResearchItem
//0-9 iputs = inputs |10-13 = scanInputs| 0 out =  paper with project| 1 out = ResearchItem
    public class CachedDefaultRecipe
            extends CachedRecipe {
        public final GT_Recipe mRecipe;
        public final List<PositionedStack> mOutputs = new ArrayList();
        public final List<PositionedStack> mInputs = new ArrayList();

        public CachedDefaultRecipe(GT_Recipe aRecipe, boolean aIsElectric) {
            super();
            this.mRecipe = aRecipe;
            if(aIsElectric) {
                for (int i = 0; i < 18; i++) {
                    Object obj = aRecipe instanceof GT_Recipe_WithAlt ? ((GT_Recipe_WithAlt) aRecipe).getAltRepresentativeInput(i) : aRecipe.getRepresentativeInput(i);
                    if (obj == null)
                        continue;
                    if (i == 0)
                        this.mInputs.add(new FixedPositionedStack(obj, 23, 76));//28 87
                    if (i == 1)
                        this.mInputs.add(new FixedPositionedStack(obj, 41, 76));
                    if (i > 1 && i < 14) {
                        this.mInputs.add(new FixedPositionedStack(obj, 18 * ((i - 2) % 4) + 5, 18 * ((i - 2) / 4) + 94));
                    }
                    if (i > 13 && i < 18)
                        this.mInputs.add(new FixedPositionedStack(obj, 12 + 18 * ((i - 14) % 4), 1));//13 2

            	/*if (obj != null) {
            		this.mInputs.add(new FixedPositionedStack(obj, 18 * (i % 4) + 12, 18 * (i / 4)));
            	}*/
                }
                if (aRecipe.mSpecialItems != null) {
                    this.mInputs.add(new FixedPositionedStack(aRecipe.mSpecialItems, 138, 36));
                }
                if (aRecipe.getOutput(0) != null) {
                    this.mOutputs.add(new FixedPositionedStack(aRecipe.getOutput(0), 114, 21, aRecipe.getOutputChance(0)));
                }
                HashSet<GT_ItemStack> aStacks = new HashSet<>();
                if(aRecipe.mOutputs.length>2)
                    for(int i = 2; i < aRecipe.mOutputs.length; i++){

                        aStacks.add(new GT_ItemStack(aRecipe.mOutputs[i]));
                    }
                if (aRecipe.getOutput(1) != null) {
                    this.mOutputs.add(new FixedPositionedMultiStack(aRecipe.getOutput(1), 134, 1, aRecipe.getOutputChance(0),aStacks.toArray(new GT_ItemStack[aStacks.size()])));
                }
                if ((aRecipe.mFluidInputs.length > 0) && (aRecipe.mFluidInputs[0] != null) && (aRecipe.mFluidInputs[0].getFluid() != null)) {
                    this.mInputs.add(new FixedPositionedStack(GT_Utility.getFluidDisplayStack(aRecipe.mFluidInputs[0], true), 12, 38));//18 50
                    if ((aRecipe.mFluidInputs.length > 1) && (aRecipe.mFluidInputs[1] != null) && (aRecipe.mFluidInputs[1].getFluid() != null)) {
                        this.mInputs.add(new FixedPositionedStack(GT_Utility.getFluidDisplayStack(aRecipe.mFluidInputs[1], true), 30, 38));
                        if ((aRecipe.mFluidInputs.length > 2) && (aRecipe.mFluidInputs[2] != null) && (aRecipe.mFluidInputs[2].getFluid() != null)) {
                            this.mInputs.add(new FixedPositionedStack(GT_Utility.getFluidDisplayStack(aRecipe.mFluidInputs[2], true), 48, 38));
                            if ((aRecipe.mFluidInputs.length > 3) && (aRecipe.mFluidInputs[3] != null) && (aRecipe.mFluidInputs[3].getFluid() != null)) {
                                this.mInputs.add(new FixedPositionedStack(GT_Utility.getFluidDisplayStack(aRecipe.mFluidInputs[3], true), 54 + 12, 38));
                            }
                        }
                    }
                }
            }
            else{
                for (int i = 0; i < 13; i++) {
                    Object obj = aRecipe instanceof GT_Recipe_WithAlt ? ((GT_Recipe_WithAlt) aRecipe).getAltRepresentativeInput(i) : aRecipe.getRepresentativeInput(i);
                    if (obj == null)
                        continue;
                    if (i < 9) {
                        this.mInputs.add(new FixedPositionedStack(obj, 18 * (i % 3) + 61, 18 * (i / 3) + 14));
                    }
                    if (i > 8 && i < 14)
                        this.mInputs.add(new FixedPositionedStack(obj, 2 + 18 * ((i - 9) % 2), 22 + 18 * ((i - 9) / 2)));//13 2

            	/*if (obj != null) {
            		this.mInputs.add(new FixedPositionedStack(obj, 18 * (i % 4) + 12, 18 * (i / 4)));
            	}*/
                }
                if (aRecipe.mSpecialItems != null) {
                    this.mInputs.add(new FixedPositionedStack(aRecipe.mSpecialItems, 138, 36));
                }
                if (aRecipe.getOutput(0) != null) {
                    this.mOutputs.add(new FixedPositionedStack(aRecipe.getOutput(0), 141, 3, aRecipe.getOutputChance(0)));
                }
                if (aRecipe.getOutput(1) != null) {
                    this.mOutputs.add(new FixedPositionedStack(aRecipe.getOutput(1), 138,33, aRecipe.getOutputChance(0)));
                }
                if ((aRecipe.mFluidInputs.length > 0) && (aRecipe.mFluidInputs[0] != null) && (aRecipe.mFluidInputs[0].getFluid() != null)) {
                    this.mInputs.add(new FixedPositionedStack(GT_Utility.getFluidDisplayStack(aRecipe.mFluidInputs[0], true), 12, 38));//18 50
                }
            }
        }

        public List<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>(getCycledIngredients(GT_NEI_ResearchStationHandler.this.cycleticks / 10, this.mInputs));
            stacks.addAll(getCycledIngredients(GT_NEI_ResearchStationHandler.this.cycleticks / 10, this.mOutputs));
            return stacks;
        }

        public PositionedStack getResult() {
            return null;
        }

        public List<PositionedStack> getOtherStacks() {
            return this.mOutputs;
        }


}
}
