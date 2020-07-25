package gregtech.api.metatileentity.implementations;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_Container_2by2;
import gregtech.api.gui.GT_Container_4by4;
import gregtech.api.gui.GT_GUIContainer_2by2;
import gregtech.api.gui.GT_GUIContainer_4by4;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_ComputerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;

public class GT_MetaTileEntity_Hatch_DataAccess extends GT_MetaTileEntity_Hatch {

    public boolean isComputerPart = false;
    public boolean needsUpdate = true;
    public GT_MetaTileEntity_ComputerBase mComputer = null;

    public GT_MetaTileEntity_Hatch_DataAccess(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 16, new String[]{
        		"Data Access for Multiblocks",
        		"Adds " + (aTier == 4 ? 4 : 16) + " extra slots for Data Sticks"});
    }

    public GT_MetaTileEntity_Hatch_DataAccess(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aTier == 4 ? 4 : 16, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Hatch_DataAccess(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aTier == 4 ? 4 : 16, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_DATA_ACCESS)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_DATA_ACCESS)};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_DataAccess(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        aBaseMetaTileEntity.openGUI(aPlayer);
        if(isComputerPart)
            needsUpdate = true;
        return true;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
    	switch (mTier) {
    	case 4:
    		return new GT_Container_2by2(aPlayerInventory, aBaseMetaTileEntity);
    	default:
    		return new GT_Container_4by4(aPlayerInventory, aBaseMetaTileEntity);
    	}
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
    	switch (mTier) {
    	case 4:
    		return new GT_GUIContainer_2by2(aPlayerInventory, aBaseMetaTileEntity, "Data Access Hatch", "DataAccess");
    	case 6:
    		return new GT_GUIContainer_4by4(aPlayerInventory, aBaseMetaTileEntity, "Data Access Hatch", "DataAccess");
    	default:
    		return new GT_GUIContainer_4by4(aPlayerInventory, aBaseMetaTileEntity, "Data Access Hatch", "DataAccess");
    	}
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    public int getFreeSpace(){
        if(needsUpdate){
            formatDataItems();
            needsUpdate = false;
        }
        if(!isComputerPart)
            return 0;
        int freeSpace = 0;
        System.out.println("IS is: "+mInventory.length);
        for(ItemStack aStack: mInventory){
            if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)|| ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)|| ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
                continue;
            NBTTagCompound tTag = aStack.getTagCompound();
            if(tTag == null)
               continue;
            if(tTag.getBoolean("isLocked"))
                continue;
            int size = tTag.getInteger("capacitySize");
            int usedCapacity = tTag.getInteger("usedCapacity");
            freeSpace+=(size-usedCapacity);
        }
        return freeSpace;
    }

    public boolean saveRecipeData(Integer aData){
        if(needsUpdate){
            formatDataItems();
            needsUpdate = false;
        }
        for(ItemStack aStack: mInventory){
            int freeSpace = 0;
            if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)|| ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)|| ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
                continue;
            NBTTagCompound tTag = aStack.getTagCompound();
            if(tTag == null)
                continue;
            if(tTag.getBoolean("isLocked"))
                continue;
            int size = tTag.getInteger("capacitySize");
            int usedCapacity = tTag.getInteger("usedCapacity");
            freeSpace+=(size-usedCapacity);
            if(freeSpace<=0)
                continue;
            GT_Recipe.GT_Recipe_ResearchStation aRecipe = GT_Recipe.GT_Recipe_ResearchStation.mIDtoRecipeMap.get(aData);
            if(aRecipe == null)
                return false;
            tTag.setInteger("rID"+(usedCapacity),aRecipe.mID);
            tTag.setInteger("usedCapacity",(usedCapacity+1));
            aStack.setTagCompound(tTag);
            System.out.println("saved recipe");
            return true;
        }
        return false;
    }

    public boolean addAllToHashSet(HashSet<Integer> aList){
        if(needsUpdate){
            formatDataItems();
            needsUpdate = false;
        }
        for(ItemStack aStack: mInventory){
            if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)|| ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)|| ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
                continue;
            NBTTagCompound tTag = aStack.getTagCompound();
            if(tTag == null)
                continue;
            int usedCapacity = tTag.getInteger("usedCapacity");
            for(int i = 0;i<usedCapacity;i++){
                int id = tTag.getInteger("rID"+(i));
                if(id != 0)
                    aList.add(id);
            }
        }
        return true;
    }

  /*  public boolean addAllToHashSet(HashSet<String> aSet){
        if(needsUpdate){
            formatDataItems();
            needsUpdate = false;
        }
        for(ItemStack aStack: mInventory){
            if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)||ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)||ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
                continue;
            NBTTagCompound tTag = aStack.getTagCompound();
            if(tTag == null)
                continue;
            int size = tTag.getInteger("capacitySize");
            int usedCapacity = tTag.getInteger("usedCapacity");
            System.out.println("getting all data "+usedCapacity);
            for(int i = 0;i<usedCapacity;i++){
                System.out.println("getting at "+i);
                String aUnlocalized = tTag.getString("unlocalized"+i);
                System.out.println("unlocal is  "+ aUnlocalized);
                if(aUnlocalized==null)
                    continue;
                aSet.add(aUnlocalized);
            }
        }
        return true;
    }*/

    public boolean formatDataItems(){
        for(ItemStack aStack: mInventory){
            System.out.println("cleaning dataSticks, can access stack"+(aStack==null|| !ItemList.Tool_DataStick.isStackEqual(aStack,false,true)));
            if(aStack==null|| !(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)|| ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)|| ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)))
                continue;
            NBTTagCompound tTag = aStack.getTagCompound();
            if(tTag == null)
                tTag = new NBTTagCompound();
            if(tTag.getBoolean("isLocked")||tTag.getBoolean("isComputer"))
                continue;
            tTag = new NBTTagCompound();

            int size = 0;
            int usedCapacity = 0;

            if(ItemList.Tool_DataStick.isStackEqual(aStack,false,true)){
                size = 1;
            }
            else if (ItemList.Tool_DataOrb.isStackEqual(aStack,false,true)){
                size = 4;
            }
            else if(ItemList.Tool_DataCluster.isStackEqual(aStack,false,true)){
                size = 16;
            }

            tTag.setBoolean("isComputer",true);
            tTag.setInteger("capacitySize",size);
            tTag.setInteger("usedCapacity",usedCapacity);
            aStack.setTagCompound(tTag);

        }
        return true;
    }
  
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void onCloseGUI() {
        needsUpdate = true;
        if(mComputer!=null)
            mComputer.onDataContainersUpdated();
        super.onCloseGUI();
    }
}