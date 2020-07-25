package gregtech.api.datasystem;

import java.util.HashSet;

public interface IDataHandler<E> {

    HashSet<E> getStoredData(int selector);

    boolean saveData(E data);

    void addAllDataToHashSet(HashSet<E> set);

    boolean canStore(E item);

}
