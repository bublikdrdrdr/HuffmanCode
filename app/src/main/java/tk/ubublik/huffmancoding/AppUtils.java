package tk.ubublik.huffmancoding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import tk.ubublik.huffmancoding.fragments.StatsFragment;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;

public class AppUtils {

    public static <T> T tryOrNull(Callable<T> callable){
        try{
            return callable.call();
        } catch (Exception e){
            return null;
        }
    }

    public static <T> T valueOrDefault(T value, T defaultValue){
        return value==null?defaultValue:value;
    }

    public static ArrayList<StatsLeaf> treeToStatsTable(HuffmanTree huffmanTree){
        float weightSum = huffmanTree.getTree().getWeight();
        List<HuffmanTree.CharInfo> charInfoList = huffmanTree.getCharCodeWeightList(true);
        ArrayList<StatsLeaf> resultList = new ArrayList<>(charInfoList.size());
        for (HuffmanTree.CharInfo info: charInfoList){
            resultList.add(new StatsLeaf(info.character, info.weight, weightSum==0?1:(info.weight/weightSum), info.bitSet.toString(false, false)));
        }
        return resultList;
    }

    public enum SortByColumn {CHAR, WEIGHT, CODE}

    public static void sortStats(List<StatsLeaf> list, SortByColumn column, boolean desc){
        Collections.sort(list, (o1, o2) -> {
            int res = 0;
            switch (column) {
                case CHAR:
                    res = ((o1.character == null ? -1 : (int) o1.character) - (o2.character == null ? -1 : (int) o2.character));
                    break;
                case WEIGHT:
                    res = Integer.compare(o1.count, o2.count);
                    break;
                case CODE:
                    res = Integer.compare(o1.code.length(), o2.code.length());
                    if (res==0) res = o1.code.compareTo(o2.code);
                    break;
            }
            return res*(desc?-1:1);
        });
    }

    public static String nitableCharToString(Character character){
        if (character==null) return "null";
        if (character== Leaf.NIT_CHAR) return "NIT";
        return String.valueOf(character);
    }
}
