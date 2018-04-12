package tk.ubublik.huffmancoding.logic;

import android.os.Parcelable;

import java.util.List;
import java.util.Map;

public interface HuffmanTree extends Parcelable{

    enum HuffmanTreeMode{
        STATIC, DYNAMIC
    }

    class CharInfo{
        public char character;
        public StrictBitSet bitSet;
        public int weight;

        public CharInfo() {
        }

        public CharInfo(char character, StrictBitSet bitSet, int weight) {
            this.character = character;
            this.bitSet = bitSet;
            this.weight = weight;
        }
    }

    /**
     * set received data {@param bitSet}, update tree and get received char to print
     */
    char receive(StrictBitSet bitSet, int charSize) throws Exception;

    /**
     * find or add char, update tree and get bits (with length) to send
     */
    StrictBitSet send(char c, int charSize);

    /**
     * set tree data from bit array with next structure:
     * {char: charSize}
     * {byte-int: weightSize}
     * {next char}
     * ...
     */
    void setWeightMap(StrictBitSet bitSet, int charSize, int weightSize);

    void setWeightMap(Map<Character, Integer> map);

    /**
     * compare own tree and {@param tree} and return value "are they equal"
     */
    boolean compareTree(HuffmanTree tree);

    /**
     * get main component of whole tree
     */
    Leaf getTree();

    /**
     * get list of used characters, their codes and weights
     */
    List<CharInfo> getCharCodeWeightList(boolean includeNIT);

    /**
     * get map of used characters, their codes and weights
     */
    Map<Character, Pair<StrictBitSet, Integer>> getCharCodeWeightMap(boolean includeNIT);
}
