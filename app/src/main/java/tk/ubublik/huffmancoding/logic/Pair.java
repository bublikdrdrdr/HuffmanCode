package tk.ubublik.huffmancoding.logic;

public class Pair<F, S> implements Cloneable {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    protected Pair<F, S> clone()  {
        return new Pair<>(first, second);
    }
}