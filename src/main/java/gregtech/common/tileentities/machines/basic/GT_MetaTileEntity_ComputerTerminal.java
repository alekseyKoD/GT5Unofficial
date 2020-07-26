package gregtech.common.tileentities.machines.basic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.datasystem.*;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IDataConnected;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_DataCable;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_DataAccess;
import gregtech.api.net.GT_Packet_CompletedResearches;
import gregtech.api.net.GT_Packet_ExtendedBlockEvent;
import gregtech.api.net.GT_Packet_ResearchProgress;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_ComputerTerminal;
import gregtech.common.gui.GT_GUIContainer_ComputerTerminal;
import gregtech.loaders.postload.GT_ResearchStationRecipeLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

import static gregtech.api.enums.GT_Values.E;
import static gregtech.api.enums.GT_Values.NW;
import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_MetaTileEntity_ComputerTerminal extends GT_MetaTileEntity_BasicMachine implements IDataListener<Integer>, IDataConnected, INodeContainer, IDataDevice {

    public int mRecipeID = -1, mErrorState = 0;
    public boolean mSetNewID = false;
    public GT_DataNode mNode;
    public GT_MetaTileEntity_DataSystemController mController;
    public HashSet<Integer> mDoneResearches =new HashSet<>();
    public HashSet<Integer> mProcessingResearches = new HashSet<>();
    public HashSet<Integer> mUnsavedResearches = new HashSet<>();
    public GT_MetaTileEntity_Hatch_DataAccess mDataHatch = null;
    public HashMap<Integer,Integer> mResearchProgressMap = new HashMap<>();

    private int mUpdate = 100;

    public GT_MetaTileEntity_ComputerTerminal(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 1, new String[]{"Copies seeds with efficiency: "+Math.min((aTier+5)*10,100)+"%","Uses UUMatter for each seed","The better crop the more UUMatter it needs","Can replicate only scanned seeds"}, 1, 1, "OrganicReplicator.png", "", new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_SIDE_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_SIDE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_FRONT_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_FRONT")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_TOP_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_TOP")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/organic_replicator/OVERLAY_BOTTOM_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("OVERLAY_BOTTOM")));
    }

    public GT_MetaTileEntity_ComputerTerminal(String aName, int aTier, String aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 1, aGUIName, aNEIName);
    }

    public GT_MetaTileEntity_ComputerTerminal(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 1, aGUIName, aNEIName);
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_ComputerTerminal(this.mName, this.mTier, this.mDescriptionArray, this.mTextures, this.mGUIName, this.mNEIName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        if(mDataHatch!=null)
            mDataHatch.formatDataItems();
        mErrorState = 0;
        return new GT_GUIContainer_ComputerTerminal(aPlayerInventory.player,aBaseMetaTileEntity,mDoneResearches == null?new HashSet<Integer>():mDoneResearches,mProcessingResearches==null?new HashSet<Integer>():mProcessingResearches,mResearchProgressMap, mUnsavedResearches, mDataHatch);
        //return new GT_GUIContainer_Research(null, new GT_Container_Research(aPlayerInventory,aBaseMetaTileEntity) );
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        if(mDataHatch!=null)
            mDataHatch.formatDataItems();
        return new GT_Container_ComputerTerminal(aPlayerInventory,aBaseMetaTileEntity);


       // return super.getServerGUI(aID,aPlayerInventory,aBaseMetaTileEntity);
    }

    public int checkRecipe(){
        return 0;
    }
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return (super.allowPutStack(aBaseMetaTileEntity, aIndex, aSide, aStack)) && (ItemList.IC2_Crop_Seeds.isStackEqual(aStack));
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        mUpdate = 100;
    }

    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return aFluid.isFluidEqual(Materials.UUMatter.getFluid(1L));
    }

    public int getCapacity() {
        return 100000;
    }

    @Override
    public void onDataAdded(Integer data) {

    }

    @Override
    public void onDataUpdated() {
        mDoneResearches = mController.getCompletedResearches();
        mUnsavedResearches = mController.mUnsavedResearches;
        NW.sendPacketToAllPlayersInRange(getBaseMetaTileEntity().getWorld(),new GT_Packet_CompletedResearches(true,getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getYCoord(),getBaseMetaTileEntity().getZCoord(),mDoneResearches, mUnsavedResearches),getBaseMetaTileEntity().getXCoord(),getBaseMetaTileEntity().getZCoord());
    }

    @Override
    public boolean transfersDataAt(byte aSide) {
        return getBaseMetaTileEntity().getFrontFacing()!=aSide;
    }

    @Override
    public void acceptBundle(GT_InformationBundle aBundle) {

    }

    @Override
    public GT_DataNode getNode() {
        return mNode;
    }

    @Override
    public void onPacketStuck() {

    }

    @Override
    public void initConnections(GT_MetaTileEntity_DataSystemController aController, ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode aLastNode){
        mNode = new GT_DataNode(this);
        mController = aController;
        aController.mSystem.addConnection(new GT_NodeConnection(aCables, mNode,aLastNode));
        aController.addListener(this);
    }

    public void acceptServerInformationComplited(HashSet<Integer> aCollection, HashSet<Integer> aUnsaved){ // todo : hashsets are unnecessary
        mDoneResearches.clear();
        mDoneResearches.addAll(aCollection);
        mUnsavedResearches.clear();
        mUnsavedResearches.addAll(aUnsaved);

    }

    public void acceptServerInformationProcessing(ArrayList<Integer> aRecipes, ArrayList<Integer> aProgress, Collection<Integer> aStations){
        mProcessingResearches.clear();
        mProcessingResearches.addAll(aRecipes);
        mResearchProgressMap.clear();
        Iterator<Integer> iterator = aRecipes.iterator();
        int n = 0;
        while (iterator.hasNext()){
            int i = iterator.next();
            mResearchProgressMap.put(i,aProgress.get(n));
            n++;
        }


    }

    public void acceptServerInformationSingleProcessing(int aID, int aProgress){
        mResearchProgressMap.put(aID,aProgress);
    }

    @Override
    public void onProcessAborted() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onTempDataUpdated(Collection<Integer> collection, Collection<Integer> process) {
        NW.sendPacketToAllPlayersInRange(getBaseMetaTileEntity().getWorld(),new GT_Packet_CompletedResearches(false,getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getYCoord(),getBaseMetaTileEntity().getZCoord(),collection,process, new ArrayList<Integer>()),getBaseMetaTileEntity().getXCoord(),getBaseMetaTileEntity().getZCoord());
    }

    @Override
    public void onRegularDataUpdate(int ID, Integer data) {
        NW.sendPacketToAllPlayersInRange(getBaseMetaTileEntity().getWorld(),new GT_Packet_ResearchProgress(getBaseMetaTileEntity().getXCoord(),getBaseMetaTileEntity().getYCoord(),getBaseMetaTileEntity().getZCoord(),ID,data),getBaseMetaTileEntity().getXCoord(),getBaseMetaTileEntity().getZCoord());
    }

    public void startResearch(int aID){
        if(mController == null) {
            IGregTechTileEntity tTile = getBaseMetaTileEntity();
            GT_Values.NW.sendPacketToAllPlayersInRange(tTile.getWorld(), new GT_Packet_ExtendedBlockEvent(tTile, 132, 2), tTile.getXCoord(), tTile.getZCoord());
            return;
        }
        mController.mSystem.sendInformation(getNode(),mController.mSystem.getPathToController(getNode()),new GT_MessageBundle(1,aID).addSender(getNode()));
    }

    public void saveRecipe(int mRecipeID, int mSlotID){
        mSlotID-=36;
        int freeSpace = 0;
        if(mDataHatch == null||mDataHatch.getBaseMetaTileEntity().isDead())
            return;
        ItemStack aStack = mDataHatch.getStackInSlot(mSlotID);
        if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)|| ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)|| ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
            return;
        NBTTagCompound tTag = aStack.getTagCompound();
        if(tTag == null)
            return;
        if(tTag.getBoolean("isLocked"))
            return;
        int size = tTag.getInteger("capacitySize");
        int usedCapacity = tTag.getInteger("usedCapacity");
        freeSpace+=(size-usedCapacity);
        if(freeSpace<=0)
            return;
        GT_Recipe.GT_Recipe_ResearchStation aRecipe = GT_Recipe.GT_Recipe_ResearchStation.mIDtoRecipeMap.get(mRecipeID);
        if(aRecipe == null)
            return;
        tTag.setInteger("rID"+(usedCapacity),aRecipe.mID);
        tTag.setInteger("usedCapacity",(usedCapacity+1));
        aStack.setTagCompound(tTag);
        System.out.println("saved recipe");
        return;
    }

    @Override
    public void onAdjacentBlockChange(int aX, int aY, int aZ) {
        mUpdate = 50;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if(mUpdate == 0){
            for(byte i = 0; i < 6; i++){
                if(i == getBaseMetaTileEntity().getFrontFacing())
                    continue;
                TileEntity tile = getBaseTile().getTileEntityAtSide(i);
                if(tile instanceof IGregTechTileEntity && ((IGregTechTileEntity)tile).getMetaTileEntity() instanceof GT_MetaTileEntity_Hatch_DataAccess) {
                    mDataHatch = (GT_MetaTileEntity_Hatch_DataAccess) ((IGregTechTileEntity) tile).getMetaTileEntity();
                    mDataHatch.isComputerPart = true;
                }


            }
        }
        else
            mUpdate--;

    }

    @Override
    public IGregTechTileEntity getBaseTile() {
        return getBaseMetaTileEntity();
    }

    @Override
    public void receiveExtendedBlockEvent(int aID, int aValue) {
        if(aID == 132)
            mErrorState = aValue;

    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if(aPlayer.isSneaking()&&aPlayer.capabilities.isCreativeMode){
            GT_Recipe.GT_Recipe_ResearchStation.mIDtoRecipeMap.clear();
            GT_Recipe.GT_Recipe_ResearchStation.mRecipeToIDsMap.clear();
            GT_Recipe.GT_Recipe_ResearchStation.mRecipeItemMap.clear();
            for(int i = 0; i < GT_Recipe.GT_Recipe_ResearchStation.mResearchPageCount; i++){
                GT_Recipe.GT_Recipe_ResearchStation.mPageNoDependanciesRecipes[i] = new ArrayList<>();
            }
            GT_Recipe.GT_Recipe_ResearchStation.sLargeResearchStationRecipeList.clear();
            GT_Recipe.GT_Recipe_Map.sResearchStationVisualRecipes.mRecipeList.clear();
            GT_Recipe.GT_Recipe_Map.sResearchStationVisualRecipes.mRecipeItemMap.clear();
            new GT_ResearchStationRecipeLoader().run();
            GT_Recipe.GT_Recipe_ResearchStation.reInit();
            GT_Utility.sendChatToPlayer(aPlayer,"Recipes reloaded");

        }
    }
}
