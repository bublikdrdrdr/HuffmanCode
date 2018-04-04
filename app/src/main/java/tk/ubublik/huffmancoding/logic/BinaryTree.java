package tk.ubublik.huffmancoding.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Bublik on 31-Mar-18.
 */

public class BinaryTree implements HuffmanTree {

    private Leaf leaf = new Leaf(Leaf.NIT_CHAR, 0);
    private final HuffmanTreeMode mode;

    public BinaryTree() {
        this(HuffmanTreeMode.DYNAMIC);
    }

    public BinaryTree(HuffmanTreeMode mode) {
        this.mode = mode;
    }

    public BinaryTree(Map<Character, Integer> map) {
        this(getFromMap(map), HuffmanTreeMode.STATIC);
    }

    private BinaryTree(Leaf leaf, HuffmanTreeMode mode) {
        this.mode = mode;
        this.leaf = leaf;
    }

    /**
     * extend tree if leaf char is NIT (and return {@code true})
     * or increase leaf weight by 1 (and return {@code false))
     */
    private boolean add(Leaf leaf, Callable<Character> charDecoder, int weight) throws Exception {
        return add(leaf, charDecoder.call(), weight);
    }

    private boolean add(Leaf leaf, char c, int weight) {
        if (leaf.getCharacter() == Leaf.NIT_CHAR) {
            leaf.set(null, weight,
                    new Leaf(Leaf.NIT_CHAR, 0),
                    new Leaf(checkOnNitAndThrow(c), weight));
            return true;
        } else {
            leaf.changeWeight(1);
            return false;
        }
    }

    /**
     * get Leaf by bitSet address, changing leafs weights on the way and return number of used bits from set
     */
    private Pair<Leaf, Integer> get(StrictBitSet bitSet, int weightDifference) {
        Leaf currentLeaf = leaf;
        int index = 0;
        while (true) {
            boolean side = bitSet.get(index++);
            currentLeaf.changeWeight(weightDifference);
            Leaf previousLeaf = currentLeaf;
            currentLeaf = side ? currentLeaf.getRight() : currentLeaf.getLeft();
            if (currentLeaf == null) return new Pair<>(previousLeaf, index - 1);
        }
    }

    private void update() {
        new RecursiveTreeUpdater().update(leaf);
    }

    public Leaf find(char c) {
        return find(c, 0).first.clone();
    }

    private Pair<Leaf, StrictBitSet> find(char c, int weightDifference) {
        Pair<Leaf, StrictBitSet> result = find(leaf, c, weightDifference);
        if (result!=null && result.second!=null) result.second.invert();
        return result;
    }

    //BITSET IS INVERTED!!! DO NOT USE WITHOUT StrictBitSet.invert();
    private Pair<Leaf, StrictBitSet> find(Leaf leaf, char c, int weightDifference) {
        if (leaf == null) return null;
        if (leaf.getCharacter() != null) {
            if (leaf.getCharacter() == c) {
                leaf.changeWeight(weightDifference);
                return new Pair<>(leaf, new StrictBitSet());
            }
            if (leaf.getCharacter() == Leaf.NIT_CHAR) return null;
        }
        Pair<Leaf, StrictBitSet> result = find(leaf.getLeft(), c, weightDifference);
        boolean right = false;
        if (result == null) {
            right = true;
            result = find(leaf.getRight(), c, weightDifference);
        }
        if (result != null) {
            leaf.changeWeight(weightDifference);
            result.second.set(result.second.getLength(), right);
        }
        return result;
    }

    private static Leaf getFromMap(Map<Character, Integer> map) {
        List<Leaf> list = new ArrayList<>();
        list.add(new Leaf(Leaf.NIT_CHAR, 0));
        Set<Map.Entry<Character, Integer>> set = Utils.sortMapValues(map, false);
        for (Map.Entry<Character, Integer> entry : set) {
            if (entry.getKey() == Leaf.NIT_CHAR)
                throw new IllegalArgumentException("Map contains NIT char");
            list.add(new Leaf(entry.getKey(), entry.getValue()));
        }
        while (list.size() > 1) {
            List<Leaf> smallest = Utils.findTwoSmallest(list);
            list.removeAll(smallest);
            Leaf grandLeaf = new Leaf(null,
                    smallest.get(0).getWeight() + smallest.get(1).getWeight(),
                    smallest.get(0),
                    smallest.get(1));
            list.add(grandLeaf);
        }
        return list.get(0);
    }

    @Override
    public String toString() {
        return "BinaryTree{mode="+mode.name()+", tree="+leaf.toString()+"}";
    }

    @Override
    public char receive(StrictBitSet bitSet, int charSize) throws Exception {
        int weightDifference = mode == HuffmanTreeMode.DYNAMIC ? 1 : 0;
        Pair<Leaf, Integer> pair = get(bitSet, weightDifference);
        if (mode == HuffmanTreeMode.DYNAMIC) {
            if (pair.first.getCharacter() == Leaf.NIT_CHAR) {
                boolean added = add(pair.first, () -> Utils.bitsToChar(bitSet, pair.second, charSize), weightDifference);
                if (added) pair.first = pair.first.getRight();
            }
            update();
        }
        return pair.first.getCharacter();
    }

    @Override
    public StrictBitSet send(char c, int charSize) {
        checkOnNitAndThrow(c);
        int weightDifference = mode == HuffmanTreeMode.DYNAMIC ? 1 : 0;
        Pair<Leaf, StrictBitSet> pair = find(c, weightDifference);
        if (pair == null) pair = find(Leaf.NIT_CHAR, weightDifference);
        if (pair.first.getCharacter() == Leaf.NIT_CHAR && mode == HuffmanTreeMode.STATIC)
            throw new IllegalArgumentException(String.format("Char %c (%d) not found in static mode", c, (int) c));
        if (mode == HuffmanTreeMode.DYNAMIC) {
            if (pair.first.getCharacter() == Leaf.NIT_CHAR) {
                boolean added = add(pair.first, c, weightDifference);
                if (added) Utils.insertChar(pair.second, c, pair.second.getLength(), charSize);
            }
            update();
        }
        return pair.second;
    }

    @Override
    public void setWeightMap(StrictBitSet bitSet, int charSize, int weightSize) {
        Map<Character, Integer> map = new HashMap<>();
        int count = bitSet.getLength() / (charSize + weightSize);
        for (int i = 0; i < count; i++) {
            char c = Utils.bitsToChar(bitSet, i * (charSize + weightSize), charSize);
            int w = Utils.bitsToInt(bitSet, i * (charSize + weightSize) + charSize, weightSize);
            map.put(c, w);
        }
        leaf = getFromMap(map);
    }

    @Override
    public boolean compareTree(HuffmanTree tree) {
        return tree instanceof BinaryTree && leaf.equals(((BinaryTree) tree).leaf);
    }

    @Override
    public Leaf getTree() {
        return leaf;
    }

    @Override
    public List<CharInfo> getCharCodeWeightList() {
        return  TreeInfoBuilder.toList(leaf);
    }

    @Override
    public Map<Character, Pair<StrictBitSet, Integer>> getCharCodeWeightMap() {
        return TreeInfoBuilder.toMap(leaf);
    }

    private char checkOnNitAndThrow(char c) {
        if (c == Leaf.NIT_CHAR)
            throw new IllegalArgumentException(String.format("Illegal new char value: %c, (%d) (NIT_CHAR)", c, (int) c));
        return c;
    }
}
