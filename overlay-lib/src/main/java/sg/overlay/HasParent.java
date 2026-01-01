package sg.overlay;

public interface HasParent<T> {
    T withParent(T volume);
    T getParent();
}
