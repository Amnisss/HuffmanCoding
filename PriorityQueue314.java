import java.util.ArrayList;
import java.lang.Comparable;
import java.util.Queue;
import java.util.NoSuchElementException;
import java.util.Collection;

public class PriorityQueue314<E extends Comparable<E>> {

    private ArrayList<E> con;

    public PriorityQueue314() {
        con = new ArrayList(); 
    }

    public boolean enqueue(E value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (con.size() == 0) {
            con.add(value);
            return true;
        }

        int indexToAdd = con.size();
        boolean flag = false;

        int curr = con.size()-1;
        while (!flag && curr >= 0) {
            int cmp = value.compareTo(con.get(curr));

            if (cmp < 0) {
                indexToAdd = curr;
                flag = true;
            }

            curr--;
        }

        con.add(indexToAdd, value);
        return true;
    }

    public boolean isEmpty() {
        return con.size() == 0;
    }

    public E dequeue() {
        return con.remove(0);
    }

    public E peek() {
        if (con.size() == 0) {
            return null;
        }
        return con.get(con.size()-1);
    }

    public int size() {
        return con.size();
    }

}