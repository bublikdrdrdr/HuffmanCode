package tk.ubublik.huffmancoding.logic;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.ToDoubleFunction;

/**
 * Created by Bublik on 31-Mar-18.
 */

public class Utils {

    public static Map<Character, Integer> getStatisticsMap(String s){
        Map<Character, Integer> map = new HashMap<>();
        for (char c: s.toCharArray()){
            Integer count = map.get(c);
            if (count==null) count = 0;
            count++;
            map.put(c, count);
        }
        return map;
    }

    public static Set<Map.Entry<Character, Integer>> sortMapValues(Map<Character, Integer> map){
        return sortMapValues(map, true);
    }

    public static Set<Map.Entry<Character, Integer>> sortMapValues(Map<Character, Integer> map, boolean desc){
        Set<Map.Entry<Character, Integer>> set = new TreeSet<>((o1, o2) -> {
            int countComparisonResult = Integer.compare(o1.getValue(), o2.getValue())*(desc?-1:1);
            return countComparisonResult == 0 ? Character.compare(o1.getKey(), o2.getKey()) : countComparisonResult;
        });
        set.addAll(map.entrySet());
        return set;
    }

    public static List<Leaf> findTwoSmallest(List<Leaf> list){
        Leaf min1 = null, min2 = null;
        for (Leaf leaf: list){
            if (min1==null){
                min1 = leaf;
            } else if (min2==null){
                min2 = leaf;
            } else {
                if (min1.getWeight()>min2.getWeight()){
                    Leaf swap = min1;
                    min1 = min2;
                    min2 = swap;
                }
                if (min2.getWeight()>leaf.getWeight()){
                    min2 = leaf;
                }
            }
        }
        List<Leaf> resultList = new ArrayList<>();
        if (min1!=null) resultList.add(min1);
        if (min2!=null) resultList.add(min2);
        return resultList;
    }

    public static char bitsToChar(StrictBitSet bitSet, int offset, int charSize){
        return (char)bitsToInt(bitSet, offset, charSize);
    }

    public static int bitsToInt(StrictBitSet bitSet, int offset, int intSize){
        char r = 0;
        for (int i = 0; i < intSize; i++){
            r |= ((bitSet.get(offset+i)?1:0)<<i);
        }
        return r;
    }

    public static void insertChar(StrictBitSet bitSet, char c, int offset, int charSize){
        for (int i = 0; i < charSize; i++){
            int and = c&(1<<i);
            bitSet.set(offset+i, and!=0);
        }
    }

    public static <T> List<T> getSingleValueList(T value){
        List<T> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    public static double getEntropy(HuffmanTree huffmanTree){
        return getEntropy(huffmanTree.getCharCodeWeightList(false), huffmanTree.getTree().getWeight());
    }

    public static double getEntropy(List<HuffmanTree.CharInfo> charInfoList, double weightSum) {
        double result = 0;
        //charInfoList.stream().mapToDouble(element -> (element.weight/weightSum*log(2, weightSum/element.weight))).sum();
        //unfortunately java 8 features are available only from Android API 24+
        for (HuffmanTree.CharInfo charInfo : charInfoList)
            result += (charInfo.weight / weightSum) * log(2, weightSum / charInfo.weight);
        return result;
    }

    public static double getAverageCodeLength(HuffmanTree huffmanTree){
        return getAverageCodeLength(huffmanTree.getCharCodeWeightList(false), huffmanTree.getTree().getWeight());
    }

    public static double getAverageCodeLength(List<HuffmanTree.CharInfo> charInfoList, double weightSum){
        double result = 0;
        for (HuffmanTree.CharInfo charInfo : charInfoList)
            result += (charInfo.weight / weightSum) * charInfo.bitSet.getLength();
        return result;
    }

    //Java doesn't have log2(x) in standard library... facepalm...
    public static double log(double base, double value){
        return Math.log(value)/Math.log(base);
    }

    public static List<List<Pair<Leaf, Leaf>>> getTreeTable(Leaf tree){
        return getTreeTable(getSingleValueList(new Pair<>(tree, null)));
    }

    public static List<List<Pair<Leaf, Leaf>>> getTreeTable(List<Pair<Leaf, Leaf>> list) {
        List<Pair<Leaf, Leaf>> childList = new ArrayList<>();
        for (Pair<Leaf, Leaf> pair : list) {
            if (pair.first.getLeft() != null)
                childList.add(new Pair<>(pair.first.getLeft(), pair.first));
            if (pair.first.getRight() != null)
                childList.add(new Pair<>(pair.first.getRight(), pair.first));
        }
        if (childList.size() == 0) return Utils.getSingleValueList(list);
        List<List<Pair<Leaf, Leaf>>> moreChildrenLeafs = getTreeTable(childList);
        moreChildrenLeafs.add(0, list);
        return moreChildrenLeafs;
    }

    public static int getMaxTreeDepth(Leaf leaf){
        int maxResult = 0;
        if (leaf.getLeft()!=null) maxResult = getMaxTreeDepth(leaf.getLeft());
        if (leaf.getRight()!=null) maxResult = Math.max(maxResult, getMaxTreeDepth(leaf.getRight()));
        return ++maxResult;
    }
}
