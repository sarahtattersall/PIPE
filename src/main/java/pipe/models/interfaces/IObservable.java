package pipe.models.interfaces;

public interface IObservable{

    void registerObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers();
}
