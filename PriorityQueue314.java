public class PriorityQueue314<E> extends Comparable<E> implements Queue<E> {

    private ArrayList<E> con;

    // what other methods do I need to implement?

    public PriorityQueue314<E> () {
        con = new ArrayList(); // but get for add is O(N) //but adding or removing at the front for arraylist is O(N)
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

        int curr = con.size();
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
        return con.get(con.size()-1);
    }

    public int size() {
        return con.size();
    }

}