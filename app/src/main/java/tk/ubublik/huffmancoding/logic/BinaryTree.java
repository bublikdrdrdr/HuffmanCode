package tk.ubublik.huffmancoding.logic;

import android.util.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Bublik on 31-Mar-18.
 */

public class BinaryTree implements HuffmanTree{

    private Leaf leaf = new Leaf(Leaf.NIT_CHAR, 0);
    private final HuffmanTreeMode mode;

    public BinaryTree(){
        this(HuffmanTreeMode.DYNAMIC);
    }

    public BinaryTree(HuffmanTreeMode mode){
        this.mode = mode;
    }

    public BinaryTree(Map<Character, Integer> map){
        this(getFromMap(map), HuffmanTreeMode.STATIC);
    }

    //public for testing
    public BinaryTree(Leaf leaf, HuffmanTreeMode mode){
        this.mode = mode;
        this.leaf = leaf;
    }

    /**
     * extend tree if leaf char is NIT (and return {@code true})
     * or increase leaf weight by 1 (and return {@code false))
     */
    private boolean add(Leaf leaf, Callable<Character> charDecoder) throws Exception{
        return add(leaf, charDecoder.call());
    }

    private boolean add(Leaf leaf, char c) {
        if (leaf.getCharacter()==Leaf.NIT_CHAR){
            leaf.set(null, 1,
                    new Leaf(Leaf.NIT_CHAR, 0),
                    new Leaf(checkOnNitAndThrow(c), 1));
            return true;
        } else {
            leaf.changeWeight(1);
            return false;
        }
    }

    /**
     * get Leaf by bitSet address and return number of used bits from set
     */
    private Pair<Leaf, Integer> get(StrictBitSet bitSet){
        Leaf currentLeaf = leaf;
        int index = 0;
        while (true){
            boolean side = bitSet.get(index++);
            Leaf previousLeaf = currentLeaf;
            currentLeaf = side?currentLeaf.getRight():currentLeaf.getLeft();
            if (currentLeaf==null) return new Pair<>(previousLeaf, index);
        }
    }

    private void update(){
        // TODO: 02-Apr-18
    }

    public Leaf find(char c){
        return find(c, 0).first.clone();
    }

    private Pair<Leaf, StrictBitSet> find(char c, int weightDifference){
        Pair<Leaf, StrictBitSet> findResult = find(leaf, c);
        if (findResult!=null && findResult.first.getCharacter()!=Leaf.NIT_CHAR) findResult.first.changeWeight(weightDifference);
        return findResult;
    }

    private Pair<Leaf, StrictBitSet> find(Leaf leaf, char c){
        if (leaf==null) return null;
        if (leaf.getCharacter()==c) return new Pair<>(leaf, new StrictBitSet());
        if (leaf.getCharacter()==Leaf.NIT_CHAR) return null;
        Pair<Leaf, StrictBitSet> result = find(leaf.getLeft(), c);
        boolean right = false;
        if (result==null){
            right = true;
            result = find(leaf.getRight(), c);
        }
        if (result!=null) result.second.set(result.second.getLength(), right);
        return result;
    }

    private static Leaf getFromMap(Map<Character, Integer> map){
        List<Leaf> list = new ArrayList<>();
        list.add(new Leaf(Leaf.NIT_CHAR, 0));
        Set<Map.Entry<Character, Integer>> set = Utils.sortMapValues(map, false);
        for (Map.Entry<Character, Integer> entry: set) {
            if (entry.getKey()==Leaf.NIT_CHAR) throw new IllegalArgumentException("Map contains NIT char");
            list.add(new Leaf(entry.getKey(), entry.getValue()));
        }
        while (list.size()>1){
            List<Leaf> smallest = Utils.findTwoSmallest(list);
            list.removeAll(smallest);
            Leaf grandLeaf = new Leaf(null,
                    smallest.get(0).getWeight()+smallest.get(1).getWeight(),
                    smallest.get(0),
                    smallest.get(1));
            list.add(grandLeaf);
        }
        return list.get(0);
    }

    @Override
    public String toString() {
        return leaf.toString();
    }

    @Override
    public char receive(StrictBitSet bitSet, int charSize) throws Exception{
        Pair<Leaf, Integer> pair = get(bitSet);
        if (mode==HuffmanTreeMode.DYNAMIC){
                add(pair.first, () -> Utils.bitsToChar(bitSet, pair.second, charSize));
            update();
        }
        return pair.first.getCharacter();
    }

    @Override
    public StrictBitSet send(char c, int charSize) {
        checkOnNitAndThrow(c);
        Pair<Leaf, StrictBitSet> pair = find(c, mode==HuffmanTreeMode.DYNAMIC?1:0);
        if (pair==null) pair = find(Leaf.NIT_CHAR, 0);
        if (pair.first.getCharacter()==Leaf.NIT_CHAR && mode==HuffmanTreeMode.STATIC)
            throw new IllegalArgumentException(String.format("Char %c (%d) not found in static mode", c, (int)c));
        if (mode==HuffmanTreeMode.DYNAMIC){
            boolean added = add(pair.first, c);
            if (added) Utils.insertChar(pair.second, c, pair.second.getLength(), charSize);
            update();
        }
        return pair.second;
    }

    @Override
    public void setWeightMap(StrictBitSet bitSet, int charSize, int weightSize) {
        Map<Character, Integer> map = new HashMap<>();
        int count = bitSet.getLength()/(charSize+weightSize);
        for (int i = 0; i < count; i++){
            char c = Utils.bitsToChar(bitSet, i*(charSize+weightSize), charSize);
            int w = Utils.bitsToInt(bitSet, i*(charSize+weightSize)+charSize, weightSize);
            map.put(c, w);
        }
        leaf = getFromMap(map);
    }

    private char checkOnNitAndThrow(char c){
        if (c==Leaf.NIT_CHAR) throw new IllegalArgumentException(String.format("Illegal new char value: %c, (%d) (NIT_CHAR)", c, (int)c));
        return c;
    }
}
