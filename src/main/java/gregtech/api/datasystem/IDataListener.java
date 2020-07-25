package gregtech.api.datasystem;

import java.util.Collection;

public interface IDataListener<E> {

    void onDataUpdated();

    void onDataAdded(E data);

    void onTempDataUpdated(Collection<E> newData, Collection<E> additionalData);

    void onRegularDataUpdate(int ID, E data);
}
