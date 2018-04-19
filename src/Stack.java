import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stack<T> implements Iterable<T> {
    private List<T> array = new ArrayList<T>();

    public void push(T value) {
        array.add(value);
    }
    public T pop() {
        int last = array.size() - 1;
        return array.remove(last);
    }
    public T peek() {
        int last = array.size() - 1;
        return array.get(last);
    }
    public void clear() {
        array.clear();
    }
    public boolean isEmpty() {
        return array.isEmpty();
    }
    public int getSize() {
        return array.size();
    }
    @Override
    public String toString() {
        return array.toString();
    }
    @Override
    public Iterator<T> iterator() {
        return array.iterator();
    }
}