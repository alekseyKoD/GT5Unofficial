package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_ComputerTerminal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Used to transfer Block Events in a much better fashion
 */
public class GT_Packet_CompletedResearches extends GT_Packet {
    private int mX, mZ;
    private short mY;
    private Collection<Integer> mCompleted, mProgress, mStations;
    private boolean mMode;

    public GT_Packet_CompletedResearches() {
        super(true);
    }

    public GT_Packet_CompletedResearches(boolean aMode, int aX, short aY, int aZ, Collection<Integer> aCompleted, Collection<Integer> aStations) {
        super(false);
        mX = aX;
        mY = aY;
        mZ = aZ;
        mCompleted = aCompleted;
        mStations = aStations;
        mMode = aMode;
    }

    public GT_Packet_CompletedResearches(boolean aMode, int aX, short aY, int aZ, Collection<Integer> aProcessing, Collection<Integer> aProgress, Collection<Integer> aStations) {
        super(false);
        mX = aX;
        mY = aY;
        mZ = aZ;
        mCompleted =aProcessing;
        mProgress = aProgress;
        mStations = aStations;
        mMode = aMode;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(mProgress==null?0:mProgress.size()*4+mCompleted.size()*4+35);
        tOut.writeBoolean(mMode);
        tOut.writeInt(mX);
        tOut.writeShort(mY);
        tOut.writeInt(mZ);
        tOut.writeInt(mCompleted.size());
        for(Integer i : mCompleted){
            tOut.writeInt(i);
        }
        if(!mMode){
            tOut.writeInt(mProgress.size());
            for(Integer i : mProgress){
                tOut.writeInt(i);
            }
        }
        tOut.writeInt(mStations.size());
        for(Integer i : mStations){
            tOut.writeInt(i);
        }
        return tOut.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        boolean b = aData.readBoolean();
        int x = aData.readInt();
        short y = aData.readShort();
        int z = aData.readInt();
        int csize = aData.readInt();
        Collection<Integer> completed = b?new HashSet<Integer>():new ArrayList<Integer>();
        for(int i = 0; i < csize; i ++){
            completed.add(aData.readInt());
        }
        ArrayList<Integer> processing = new ArrayList<>();
        if(!b){
            int a = aData.readInt();
            for(int i = 0; i < a; i++){
                processing.add(aData.readInt());
            }
        }
        int rsize = aData.readInt();
        HashSet<Integer> stations = new HashSet<>();
        for(int i = 0; i < rsize; i++){
            stations.add(aData.readInt());
        }
        if(b)
        return new GT_Packet_CompletedResearches(b,x,y,z,completed, stations);
        else
            return new GT_Packet_CompletedResearches(b,x,y,z,completed,processing,stations);
    }

    @Override
    public void process(IBlockAccess aWorld) {
        if (aWorld != null) {
            TileEntity tTileEntity = aWorld.getTileEntity(mX, mY, mZ);
            if(tTileEntity instanceof IGregTechTileEntity && ((IGregTechTileEntity)tTileEntity).getMetaTileEntity() instanceof GT_MetaTileEntity_ComputerTerminal){
                if(mMode)
                    ((GT_MetaTileEntity_ComputerTerminal)(((IGregTechTileEntity)tTileEntity).getMetaTileEntity())).acceptServerInformationComplited((HashSet)mCompleted, (HashSet)mStations);
                else
                    ((GT_MetaTileEntity_ComputerTerminal)(((IGregTechTileEntity)tTileEntity).getMetaTileEntity())).acceptServerInformationProcessing((ArrayList<Integer>)mCompleted,(ArrayList<Integer>)mProgress,mStations);
            }
        }
    }

    @Override
    public byte getPacketID() {
        return 8;
    }
}