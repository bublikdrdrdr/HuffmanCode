package tk.ubublik.huffmancoding.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeInfoBuilder {

    @FunctionalInterface
    private interface CharInfoContainer{
        void add(HuffmanTree.CharInfo charInfo);
    }

    public static List<HuffmanTree.CharInfo> toList(Leaf leaf, boolean includeNIT){
        List<HuffmanTree.CharInfo> list = new ArrayList<>();
        goRecursive(leaf, new StrictBitSet(), list::add, includeNIT);
        return list;
    }

    public static Map<Character, Pair<StrictBitSet, Integer>> toMap(Leaf leaf, boolean includeNIT){
        Map<Character, Pair<StrictBitSet, Integer>> map = new HashMap<>();
        goRecursive(leaf, new StrictBitSet(), charInfo ->
                map.put(charInfo.character, new Pair<>(charInfo.bitSet, charInfo.weight)), includeNIT);
        return map;
    }

    private static void goRecursive(Leaf leaf, StrictBitSet strictBitSet, CharInfoContainer container, boolean includeNIT){
        if (leaf.getCharacter()==null){
            if (leaf.getLeft()!=null) goRecursive(leaf.getLeft(), strictBitSet.clone().add(false), container, includeNIT);
            if (leaf.getRight()!=null) goRecursive(leaf.getRight(), strictBitSet.clone().add(true), container, includeNIT);
        } else if (includeNIT || leaf.getCharacter()!=Leaf.NIT_CHAR) {
            container.add(new HuffmanTree.CharInfo(leaf.getCharacter(), strictBitSet, leaf.getWeight()));
        }
    }
}
