package gregtech.api.metatileentity.implementations;

import gregtech.GT_Mod;
import gregtech.api.datasystem.GT_DataNode;
import gregtech.api.datasystem.GT_NodeConnection;
import gregtech.api.datasystem.INodeContainer;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TextureSet;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IDataConnected;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.metatileentity.IMetaTileEntityDataCable;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaPipeEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Client;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static gregtech.api.enums.GT_Values.VN;

public class GT_MetaPipeEntity_DataCable extends MetaPipeEntity implements IMetaTileEntityDataCable, IDataConnected {

    public final float mThickNess;
    public final Materials mMaterial;
    public final long  mDataAmount;
    public final int mDataChannels;
    public final boolean mInsulated;
    public long mUsedDataChannels = 0, mUsedDataChannelsLast20 = 0, mTransferredDataAmount20 = 0;
    public  boolean isNode = false;
    public static GT_MetaTileEntity_DataSystemController mController = null;

//region cable things
    public GT_MetaPipeEntity_DataCable(int aID, String aName, String aNameRegional, float aThickNess, Materials aMaterial, int aDataChannels, long aDataAmount, boolean aInsulated) {
        super(aID, aName, aNameRegional, 0);
        mThickNess = aThickNess;
        mMaterial = aMaterial;
        mDataChannels = aDataChannels;
        mDataAmount = aDataAmount;
        mInsulated = aInsulated;
    }

    public GT_MetaPipeEntity_DataCable(String aName, float aThickNess, Materials aMaterial, int aDataChannels, long aDataAmount, boolean aInsulated) {
        super(aName, 0);
        mThickNess = aThickNess;
        mMaterial = aMaterial;
        mDataChannels = aDataChannels;
        mDataAmount = aDataAmount;
        mInsulated = aInsulated;
    }

    @Override
    public byte getTileEntityBaseType() {
        return (byte) (mInsulated ? 9 : 8);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaPipeEntity_DataCable(mName, mThickNess, mMaterial, mDataChannels, mDataAmount, mInsulated);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aConnections, byte aColorIndex, boolean aConnected, boolean aRedstone) {
        if (!mInsulated)
            return new ITexture[]{new GT_RenderedTexture(mMaterial.mIconSet.mTextures[TextureSet.INDEX_wire], Dyes.getModulation(aColorIndex, mMaterial.mRGBa) )};
        if (aConnected) {
            float tThickNess = getThickNess();
            if (tThickNess < 0.499F)//0.500 x2
                return new ITexture[]{new GT_RenderedTexture(mMaterial.mIconSet.mTextures[TextureSet.INDEX_wire], mMaterial.mRGBa), new GT_RenderedTexture(Textures.BlockIcons.INSULATION_SMALL, Dyes.getModulation(aColorIndex, Dyes.CABLE_INSULATION.mRGBa))};
            if (tThickNess < 0.874F)//0.825 x12
                return new ITexture[]{new GT_RenderedTexture(mMaterial.mIconSet.mTextures[TextureSet.INDEX_wire], mMaterial.mRGBa), new GT_RenderedTexture(Textures.BlockIcons.INSULATION_LARGE, Dyes.getModulation(aColorIndex, Dyes.CABLE_INSULATION.mRGBa))};
            return new ITexture[]{new GT_RenderedTexture(mMaterial.mIconSet.mTextures[TextureSet.INDEX_wire], mMaterial.mRGBa), new GT_RenderedTexture(Textures.BlockIcons.INSULATION_HUGE, Dyes.getModulation(aColorIndex, Dyes.CABLE_INSULATION.mRGBa))};
        }
        return new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.INSULATION_FULL, Dyes.getModulation(aColorIndex, Dyes.CABLE_INSULATION.mRGBa))};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return false;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    @Override
    public final boolean renderInside(byte aSide) {
        return false;
    }

    @Override
    public int getProgresstime() {
        return (int) mDataAmount * 64;
    }

    @Override
    public int maxProgresstime() {
        return (int) mDataChannels * 64;
    }

//endregion

    @Override
    public void initConnections(byte aSide, GT_MetaTileEntity_DataSystemController aRef, HashSet<TileEntity> aAlreadyPassedSet, ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode aLastNode) {
        if (!isConnectedAtSide(aSide) && aSide != 6)
            return;
        mController = aRef;
        if(mController==null)
            return;

        final IGregTechTileEntity baseMetaTile = getBaseMetaTileEntity();
        aCables.add(this);

        byte connections = 0;
        for (byte i = 0; i < 6 ; i++)
            if(isConnectedAtSide(i))
                connections++;

        if(connections>2){
            isNode = true;
            GT_DataNode newNode = new GT_DataNode();
            if(newNode == aLastNode)
                isNode = isNode;
            mController.mSystem.addConnection(new GT_NodeConnection(aCables, newNode,aLastNode));
            aLastNode = newNode;
            aCables = new ArrayList<>();
        }

        for (byte i = 0; i < 6 ; i++)
        if (i != aSide && isConnectedAtSide(i) && baseMetaTile.getCoverBehaviorAtSide(i).letsEnergyOut(i, baseMetaTile.getCoverIDAtSide(i), baseMetaTile.getCoverDataAtSide(i), baseMetaTile)) {
            final TileEntity tTileEntity = baseMetaTile.getTileEntityAtSide(i);

            if (tTileEntity != null && aAlreadyPassedSet.add(tTileEntity)) {
                final byte tSide = GT_Utility.getOppositeSide(i);
                final IGregTechTileEntity tBaseMetaTile = tTileEntity instanceof IGregTechTileEntity ? ((IGregTechTileEntity) tTileEntity) : null;
                final IMetaTileEntity tMeta = tBaseMetaTile != null ? tBaseMetaTile.getMetaTileEntity() : null;

                if (tMeta instanceof IMetaTileEntityDataCable) {
                    if (tBaseMetaTile.getCoverBehaviorAtSide(tSide).letsEnergyIn(tSide, tBaseMetaTile.getCoverIDAtSide(tSide), tBaseMetaTile.getCoverDataAtSide(tSide), tBaseMetaTile) ) {
                        ((IMetaTileEntityDataCable) ((IGregTechTileEntity) tTileEntity).getMetaTileEntity()).initConnections(tSide, aRef, aAlreadyPassedSet,aCables,aLastNode);
                    }
                }
                else if (tMeta instanceof INodeContainer){
                    ((INodeContainer) tMeta).initConnections(mController,aCables,aLastNode);
                }

            }
        }
    }

    @Override
    public boolean transfersDataAt(byte aSide) {
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTick % 20 == 0) {
                if (mCheckConnections) checkConnections();
            }
        }else if(aBaseMetaTileEntity.isClientSide() && GT_Client.changeDetected==4) aBaseMetaTileEntity.issueTextureUpdate();
    }

    @Override
    public boolean letsIn(GT_CoverBehavior coverBehavior, byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return coverBehavior.letsEnergyIn(aSide, aCoverID, aCoverVariable, aTileEntity);
    }

    @Override
    public boolean letsOut(GT_CoverBehavior coverBehavior, byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return coverBehavior.letsEnergyOut(aSide, aCoverID, aCoverVariable, aTileEntity);
    }

    @Override
    public boolean canConnect(byte aSide, TileEntity tTileEntity) {
        TileEntity aTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(aSide);
        if(!(aTileEntity instanceof BaseMetaTileEntity && ((BaseMetaTileEntity)tTileEntity).getMetaTileEntity()instanceof IDataConnected))
            return false;
        final IGregTechTileEntity baseMetaTile = getBaseMetaTileEntity();
        final GT_CoverBehavior coverBehavior = baseMetaTile.getCoverBehaviorAtSide(aSide);
        final byte tSide = GT_Utility.getOppositeSide(aSide);
        final ForgeDirection tDir = ForgeDirection.getOrientation(tSide);

        return ((IDataConnected) ((BaseMetaTileEntity)tTileEntity).getMetaTileEntity()).transfersDataAt(tSide);

    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Max Data Amount per second: %%%" + EnumChatFormatting.GREEN + mDataAmount + " (" + VN[1] + ")" + EnumChatFormatting.GRAY,
                "Max Data Channels: %%%" + EnumChatFormatting.YELLOW + mDataChannels + EnumChatFormatting.GRAY
        };
    }

    @Override
    public float getThickNess() {
        if (GT_Mod.instance.isClientSide() && (GT_Client.hideValue & 0x1) != 0) return 0.01F;
        return mThickNess;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        if (GT_Mod.instance.isClientSide() && (GT_Client.hideValue & 0x2) != 0)
            return AxisAlignedBB.getBoundingBox(aX, aY, aZ, aX + 1, aY + 1, aZ + 1);
        else
            return getActualCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
    }

    private AxisAlignedBB getActualCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        float tSpace = (1f - mThickNess)/2;
        float tSide0 = tSpace;
        float tSide1 = 1f - tSpace;
        float tSide2 = tSpace;
        float tSide3 = 1f - tSpace;
        float tSide4 = tSpace;
        float tSide5 = 1f - tSpace;

        if(getBaseMetaTileEntity().getCoverIDAtSide((byte) 0) != 0){tSide0=tSide2=tSide4=0;tSide3=tSide5=1;}
        if(getBaseMetaTileEntity().getCoverIDAtSide((byte) 1) != 0){tSide2=tSide4=0;tSide1=tSide3=tSide5=1;}
        if(getBaseMetaTileEntity().getCoverIDAtSide((byte) 2) != 0){tSide0=tSide2=tSide4=0;tSide1=tSide5=1;}
        if(getBaseMetaTileEntity().getCoverIDAtSide((byte) 3) != 0){tSide0=tSide4=0;tSide1=tSide3=tSide5=1;}
        if(getBaseMetaTileEntity().getCoverIDAtSide((byte) 4) != 0){tSide0=tSide2=tSide4=0;tSide1=tSide3=1;}
        if(getBaseMetaTileEntity().getCoverIDAtSide((byte) 5) != 0){tSide0=tSide2=0;tSide1=tSide3=tSide5=1;}

        byte tConn = ((BaseMetaPipeEntity) getBaseMetaTileEntity()).mConnections;
        if((tConn & (1 << ForgeDirection.DOWN.ordinal()) ) != 0) tSide0 = 0f;
        if((tConn & (1 << ForgeDirection.UP.ordinal())   ) != 0) tSide1 = 1f;
        if((tConn & (1 << ForgeDirection.NORTH.ordinal())) != 0) tSide2 = 0f;
        if((tConn & (1 << ForgeDirection.SOUTH.ordinal())) != 0) tSide3 = 1f;
        if((tConn & (1 << ForgeDirection.WEST.ordinal()) ) != 0) tSide4 = 0f;
        if((tConn & (1 << ForgeDirection.EAST.ordinal()) ) != 0) tSide5 = 1f;

        return AxisAlignedBB.getBoundingBox(aX + tSide4, aY + tSide0, aZ + tSide2, aX + tSide5, aY + tSide1, aZ + tSide3);
    }

    @Override
    public void addCollisionBoxesToList(World aWorld, int aX, int aY, int aZ, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider) {
        super.addCollisionBoxesToList(aWorld, aX, aY, aZ, inputAABB, outputAABB, collider);
        if (GT_Mod.instance.isClientSide() && (GT_Client.hideValue & 0x2) != 0) {
            AxisAlignedBB aabb = getActualCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
            if (inputAABB.intersectsWith(aabb)) outputAABB.add(aabb);
        }
    }

    @Override
    public void setCheckConnections() {
        super.setCheckConnections();
        if(mController!=null)
            mController.onSystemChanged();
    }
}