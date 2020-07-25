package gregtech.api.datasystem;

public interface IResearcher {
    boolean isProcessing();
    Object getProcessing();
    int getProgress();
    boolean setNextResearch(int aID);
}
