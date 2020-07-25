package gregtech.common.tileentities.machines.basic;

import gregtech.api.datasystem.*;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IDataConnected;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_DataCable;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.net.GT_Packet_ExtendedBlockEvent;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Utility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class GT_MetaTileEntity_DataSystemController extends GT_MetaTileEntity_BasicMachine implements INodeContainer, IDataConnected {

    public GT_DataSystem mSystem;
    public static int mSystemLoadingTime = 25;
    public ArrayList<IResearcher> mWorkers = new ArrayList<>();
    public ArrayList<IDataConsumer> mConsumers = new ArrayList<>();
    public ArrayList<IDataProducer> mProducers = new ArrayList<>();
    public ArrayList<IDataHandler<Integer>> mHandlers = new ArrayList<>();
    public ArrayList<IDataListener<Integer>> mListeners = new ArrayList<>();
    public HashMap<IDataDevice,Integer> mDevicesMap = new HashMap<>();

    int mUpdate = 180;
    int mDataUpdate = -1;

    GT_DataNode mNode;

    protected int[] mComputationRequests = new int[]{0};

    public HashSet<Integer> mUnsavedResearches = new HashSet<>();


    public GT_MetaTileEntity_DataSystemController(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 1, new String[]{"Used to link devices via cables","Like a heart for you data processing system"}, 1, 1, "E_Oven.png", "", new ITexture[]{new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_SIDE_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_SIDE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_FRONT_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_FRONT")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_TOP_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_TOP")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_BOTTOM_ACTIVE")), new GT_RenderedTexture(new Textures.BlockIcons.CustomIcon("basicmachines/ELECTRIC_OVEN/OVERLAY_BOTTOM"))});
    }

    public GT_MetaTileEntity_DataSystemController(String aName, int aTier, String aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 0, 0, aGUIName, aNEIName);
    }

    public GT_MetaTileEntity_DataSystemController(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 0, 0, aGUIName, aNEIName);
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_DataSystemController(this.mName, this.mTier, this.mDescriptionArray, this.mTextures, this.mGUIName, this.mNEIName);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {

        super.onPostTick(aBaseMetaTileEntity, aTick);
        if(getBaseMetaTileEntity().isClientSide())
            return;
        onPreTick();
        if(mUpdate>-50) {
            mUpdate--;
            if (mUpdate == 0) {
                if (mSystem == null)
                    initDataSystem();
                else {
                    reInitDataSystem();
                }
            }
            if(mUpdate == -20)
                onDataUnpdated();
        }
        if(mDataUpdate>0){
            mDataUpdate--;
            if(mDataUpdate == 0)
                onDataUnpdated();
        }
        if(mSystem == null)
            return;
        mSystem.onUpdate();

    }

    @Override
    public boolean allowToCheckRecipe() {
        return false;
    }

    @Override
    public void acceptBundle(GT_InformationBundle aBundle) {
        if(aBundle.isAutomated){
            mSystem.sendInformation(getNode(),mSystem.getPathFromController(aBundle.to),aBundle);
        }
        if(aBundle instanceof GT_MessageBundle){
            if(((GT_MessageBundle)aBundle).mID==1){
                Iterator<IResearcher> iterator = mWorkers.iterator();
                boolean hadSetResearch = false;
                while (iterator.hasNext()){
                    if(iterator.next().setNextResearch(((GT_MessageBundle)aBundle).mInformation)) {
                        hadSetResearch = true;
                        break;
                    }
                }
                if(!hadSetResearch) {
                    if(aBundle.from.getContainer() instanceof GT_MetaTileEntity_ComputerTerminal) {
                        GT_MetaTileEntity_ComputerTerminal tTerminal = (GT_MetaTileEntity_ComputerTerminal) aBundle.from.getContainer();
                        IGregTechTileEntity tTile = tTerminal.getBaseMetaTileEntity();
                        GT_Values.NW.sendPacketToAllPlayersInRange(tTile.getWorld(), new GT_Packet_ExtendedBlockEvent(tTile,132,1),tTile.getXCoord(),tTile.getZCoord());
                    }
                }

            }
        }
        if(aBundle instanceof GT_ResearchDoneBundle){
            boolean hasSaved = false;
            for(IDataHandler<Integer> tHandler: mHandlers){
                if(tHandler.canStore(((GT_ResearchDoneBundle) aBundle).mResearch.mID)){
                    if(tHandler.saveData(((GT_ResearchDoneBundle) aBundle).mResearch.mID)) {
                        for (IDataListener<Integer> tListener : mListeners) {
                            tListener.onDataUpdated();
                        }
                        hasSaved = true;
                        break;
                    }
                }
            }
            if(!hasSaved){
                mUnsavedResearches.add(((GT_ResearchDoneBundle) aBundle).mResearch.mID);
                for (IDataListener<Integer> tListener : mListeners) {
                    tListener.onDataUpdated();
                }

            }

        }
        if(aBundle instanceof GT_CalculationBundle){
            GT_CalculationBundle calcBundle = (GT_CalculationBundle)aBundle;
            int leftToSend = calcBundle.mDataFlow;
            Iterator<IDataConsumer> iterator = mConsumers.iterator();
            while (leftToSend>0&&iterator.hasNext()){
                IDataConsumer tConsumer = iterator.next();
                leftToSend -= tConsumer.requestComputation()[calcBundle.mType];
                mSystem.sendInformation(getNode(),mSystem.getPathFromController(tConsumer.getNode()),new GT_CalculationBundle(calcBundle.mType,leftToSend>0?calcBundle.mType:calcBundle.mDataFlow));
            }
        }
    }

    protected void reInitDataSystem(){
        IDataDevice[] aWorkers = mWorkers.toArray(new IDataDevice[mWorkers.size()]);
        IDataDevice[] aProducers = mProducers.toArray(new IDataDevice[mProducers.size()]);
        mSystem.resetMaps();
        mSystem = null;
        mWorkers.clear();
        mProducers.clear();
        mListeners.clear();
        mHandlers.clear();
        initDataSystem();
        for(IDataDevice aDevice : aWorkers){
            if(!mWorkers.contains(aDevice))
                aDevice.onDisconnected();
        }
        for(IDataDevice aDevice : aProducers){
            if(!mProducers.contains(aDevice))
                aDevice.onDisconnected();
        }
        onTempDataUpdate();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        mUpdate = mSystemLoadingTime;
    }

    protected void initDataSystem(){
        mSystem = new GT_DataSystem(this);
        mNode = new GT_DataNode(this);
        for(byte i = 0; i < 6; i++){
            TileEntity tile = getBaseMetaTileEntity().getTileEntityAtSide(i);
            if(tile instanceof IGregTechTileEntity && ((IGregTechTileEntity) tile).getMetaTileEntity() instanceof GT_MetaPipeEntity_DataCable){
                GT_MetaPipeEntity_DataCable cable = (GT_MetaPipeEntity_DataCable)((IGregTechTileEntity) tile).getMetaTileEntity();
                if(cable.isConnectedAtSide(GT_Utility.getOppositeSide(i))){
                    cable.initConnections(GT_Utility.getOppositeSide(i),this, new HashSet<TileEntity>(),new ArrayList<GT_MetaPipeEntity_DataCable>(),mNode);
                }
            }
        }
    }

    @Override
    public GT_DataNode getNode() {
        return mNode;
    }

    public void onSystemChanged(){
        mUpdate = 50;
    }


    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public boolean transfersDataAt(byte aSide) {
        return getBaseMetaTileEntity().getBackFacing()!=aSide;
    }

    public boolean addWorker(IResearcher aWorker){
        if(mWorkers.contains(aWorker))
            return false;
        return mWorkers.add(aWorker);
    }

    public boolean addConsumer(IDataConsumer aConsumer){
        addDevice(aConsumer);
        if(mConsumers.contains(aConsumer))
            return false;
        return mConsumers.add(aConsumer);
    }

    public boolean addProducer(IDataProducer aProducer){
        addDevice(aProducer);
        if(mProducers.contains(aProducer))
            return false;
        return mProducers.add(aProducer);
    }

    public boolean addHandler(IDataHandler<Integer> aHandler){
        if(aHandler instanceof IDataDevice)
            addDevice((IDataDevice)aHandler);
        if(mHandlers.add(aHandler)) {
           mDataUpdate = 5;
            return true;
        }
        return false;
    }

    public boolean addListener(IDataListener<Integer> aListener){
        if(aListener instanceof IDataDevice)
            addDevice((IDataDevice)aListener);
        if(mListeners.contains(aListener))
            return false;
        return mListeners.add(aListener);
    }

    @Override
    public void onPacketStuck() {

    }

    public void addDevice(IDataDevice aDevice){
        if(mDevicesMap.get(aDevice)!=null)
            mDevicesMap.put(aDevice,mDevicesMap.size());
    }

    public HashSet<Integer> getCompletedResearches(){
        HashSet<Integer> tOut = new HashSet<>(100);
        for(IDataHandler<Integer> tHandler : mHandlers){
            tHandler.addAllDataToHashSet(tOut);
        }
        return tOut;
    }

    public void onPreTick(){
        mComputationRequests[0] = 0;
        for(IDataConsumer tConsumer : mConsumers){
            int[] comp = tConsumer.requestComputation();
            for(int i = 0; i < mComputationRequests.length; i++)
                mComputationRequests[i] += comp[i];
        }

        for(IDataProducer tProducer : mProducers){
            mComputationRequests = tProducer.setProducingPower(mComputationRequests);
        }
    }

    @Override
    public void initConnections(GT_MetaTileEntity_DataSystemController aController, ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode aLastNode) {

    }

    public void onTempDataUpdate(){
        ArrayList<Integer> ar = new ArrayList<>();
        ArrayList<Integer> arr = new ArrayList<>();
        for(IResearcher aResearcher : mWorkers){
            ar.add((Integer)aResearcher.getProcessing());
            arr.add(aResearcher.getProgress());
        }

        for(IDataListener<Integer> aListener : mListeners)
            aListener.onTempDataUpdated(ar,arr);
    }

    public void onDataUnpdated(){
        for(int i : mUnsavedResearches){
            for(IDataHandler<Integer> tHandler: mHandlers){
                if(tHandler.canStore(i)){
                    if(tHandler.saveData(i)) {
                        mUnsavedResearches.remove(i);
                        break;
                    }
                }
            }
        }
        for(IDataListener<Integer> tListener: mListeners){
            tListener.onDataUpdated();
        }

    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        int q = aNBT.getInteger("mUnsS");
        for(int i = 0; i < q; i++){
            mUnsavedResearches.add(aNBT.getInteger("mUns"+i));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        if(mUnsavedResearches.size()>0){
            aNBT.setInteger("mUnsS",mUnsavedResearches.size());
            Iterator<Integer> iterator = mUnsavedResearches.iterator();
            int i = 0;
            while (iterator.hasNext()){
                aNBT.setInteger("mUns"+i,iterator.next());
            }
        }
        super.saveNBTData(aNBT);
    }
}
