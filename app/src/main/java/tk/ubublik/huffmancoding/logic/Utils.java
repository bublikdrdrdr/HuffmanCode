package tk.ubublik.huffmancoding.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
            bitSet.set(offset+i, (c&(1<<i))!=0);
        }
    }
}
