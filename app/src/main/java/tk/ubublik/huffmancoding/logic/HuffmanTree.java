package tk.ubublik.huffmancoding.logic;

public interface HuffmanTree {

    enum HuffmanTreeMode{
        STATIC, DYNAMIC
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

    /**
     * compare own tree and {@param tree} and return value "are they equal"
     */
    boolean compareTree(HuffmanTree tree);
}
