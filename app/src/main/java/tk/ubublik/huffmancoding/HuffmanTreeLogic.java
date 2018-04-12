package tk.ubublik.huffmancoding;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.ubublik.huffmancoding.logic.BinaryTree;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.StrictBitSet;
import tk.ubublik.huffmancoding.logic.Utils;

public class HuffmanTreeLogic implements Parcelable {
    private final int charSize;

    public HuffmanTreeLogic(int charSize) {
        this.charSize = charSize;
    }

    private HuffmanTree dynamicHuffmanTreeSender = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
    private HuffmanTree dynamicHuffmanTreeReceiver = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
    private HuffmanTree staticHuffmanTree = new BinaryTree(HuffmanTree.HuffmanTreeMode.STATIC);
    private String inputData = "";
    private List<CharStringPair> dataList = new ArrayList<>();
    private Map<StatsKey, String> statsMap = new HashMap<>();
    private long summaryDynamicNanos = 0;
    private StringBuilder encodedStringBuilder = new StringBuilder();

    public HuffmanTreeLogic(Parcel in) {
        charSize = in.readInt();
        dynamicHuffmanTreeSender = in.readParcelable(HuffmanTree.class.getClassLoader());
        dynamicHuffmanTreeReceiver = in.readParcelable(HuffmanTree.class.getClassLoader());
        staticHuffmanTree = in.readParcelable(HuffmanTree.class.getClassLoader());
        inputData = in.readString();
        dataList = in.createTypedArrayList(CharStringPair.CREATOR);
        summaryDynamicNanos = in.readLong();
        for (StatsKey statsKey:StatsKey.values()){
            statsMap.put(statsKey,in.readString());
        }
        encodedStringBuilder = pairListToEncodedString(dataList);
    }

    public static final Creator<HuffmanTreeLogic> CREATOR = new Creator<HuffmanTreeLogic>() {
        @Override
        public HuffmanTreeLogic createFromParcel(Parcel in) {
            return new HuffmanTreeLogic(in);
        }

        @Override
        public HuffmanTreeLogic[] newArray(int size) {
            return new HuffmanTreeLogic[size];
        }
    };

    public StringBuilder getEncodedStringBuilder(){
        return encodedStringBuilder;
    }

    public HuffmanTree getTree(HuffmanTree.HuffmanTreeMode mode){
        switch (mode) {
            case STATIC:
                return staticHuffmanTree;
            case DYNAMIC:
                return dynamicHuffmanTreeSender;
            default:
                throw new IllegalArgumentException("Unknown Huffman tree mode: " + mode.name());
        }
    }

    public String getLastInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public List<CharStringPair> getDataList() {
        return dataList;
    }

    public void send(char character) {
        long nanos = System.nanoTime();
        StrictBitSet bitSet = dynamicHuffmanTreeSender.send(character, charSize);
        nanos = System.nanoTime() - nanos;
        statsMap.put(StatsKey.DYNAMIC_SEND, formatToMicroseconds(nanos));
        summaryDynamicNanos += nanos;
        statsMap.put(StatsKey.DYNAMIC_SUMMARY_SEND, formatToMicroseconds(summaryDynamicNanos));
        nanos = System.nanoTime();
        try {
            dynamicHuffmanTreeReceiver.receive(bitSet, charSize);
        } catch (Exception ignored) {
        }
        nanos = System.nanoTime() - nanos;
        statsMap.put(StatsKey.DYNAMIC_RECEIVE, formatToMicroseconds(nanos));
        nanos = System.nanoTime();
        staticHuffmanTree.setWeightMap(Utils.getStatisticsMap(inputData));
        nanos = System.nanoTime()-nanos;
        statsMap.put(StatsKey.STATIC_BUILD_TREE, formatToMicroseconds(nanos));
        nanos = System.nanoTime();
        staticHuffmanTree.send(character, charSize);
        nanos = System.nanoTime() - nanos;
        statsMap.put(StatsKey.STATIC_SEND, formatToMicroseconds(nanos));
        nanos = System.nanoTime();
        try {
            staticHuffmanTree.receive(bitSet, charSize);
        } catch (Exception ignored) {
        }
        nanos = System.nanoTime() - nanos;
        statsMap.put(StatsKey.STATIC_RECEIVE, formatToMicroseconds(nanos));
        statsMap.put(StatsKey.DYNAMIC_ENTROPY, formatDouble(Utils.getEntropy(dynamicHuffmanTreeSender)));
        statsMap.put(StatsKey.DYNAMIC_AVERAGE, formatDouble(Utils.getAverageCodeLength(dynamicHuffmanTreeSender)));
        statsMap.put(StatsKey.STATIC_ENTROPY, formatDouble(Utils.getEntropy(staticHuffmanTree)));
        statsMap.put(StatsKey.STATIC_AVERAGE, formatDouble(Utils.getAverageCodeLength(staticHuffmanTree)));
        dataList.add(new CharStringPair(character, bitSet.toString(false, true)));
        encodedStringBuilder.append(bitSet.toString(false, true));
        long staticCodeBits = calculateStaticCodeBits();
        statsMap.put(StatsKey.STATIC_CODE_COMPRESSION, formatPercent((double) staticCodeBits/(inputData.length()*charSize)));
        statsMap.put(StatsKey.DYNAMIC_CODE_COMPRESSION, formatPercent((double) encodedStringBuilder.length()/(inputData.length()*charSize)));
    }

    public void clearData() {
        summaryDynamicNanos = 0;
        dynamicHuffmanTreeReceiver = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
        dynamicHuffmanTreeSender = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
        staticHuffmanTree = new BinaryTree(HuffmanTree.HuffmanTreeMode.STATIC);
        inputData = "";
        dataList = new ArrayList<>();
        statsMap = new HashMap<>();
        encodedStringBuilder = new StringBuilder();
    }

    public Map<StatsKey, String> getStats() {
        return statsMap;
    }

    private StringBuilder pairListToEncodedString(List<CharStringPair> list){
        StringBuilder encoded = new StringBuilder();
        for (CharStringPair pair: list){
            encoded.append(pair.second);
        }
        return encoded;
    }

    private long calculateStaticCodeBits(){
        long count = 0;
        for (char c: inputData.toCharArray())
            count += staticHuffmanTree.send(c, charSize).getLength();
        return count;
    }

    @SuppressLint("DefaultLocale")
    private String formatToMicroseconds(long nanos) {
        double timeValue = nanos;
        final String[] units = new String[]{"ns", "us", "ms", "s"};
        String unit = units[0];
        for (String currentUnit: units){
            if (timeValue<1000 || unit.equals(units[units.length-1])){
                unit = currentUnit;
                break;
            }
            else timeValue /= 1000;
        }
        return String.format("%.3f %s", timeValue, unit);
    }

    @SuppressLint("DefaultLocale")
    private String formatDouble(double value) {
        return String.format("%.4f", value);
    }

    @SuppressLint("DefaultLocale")
    private String formatPercent(double value) {
        return String.format("%.1f%%", value*100);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(charSize);
        dest.writeParcelable(dynamicHuffmanTreeSender, flags);
        dest.writeParcelable(dynamicHuffmanTreeReceiver, flags);
        dest.writeParcelable(staticHuffmanTree, flags);
        dest.writeString(inputData);
        dest.writeTypedList(dataList);
        dest.writeLong(summaryDynamicNanos);
        for (StatsKey statsKey: StatsKey.values()){
            dest.writeString(statsMap.get(statsKey));
        }
    }

    public enum StatsKey {
        DYNAMIC_SEND(R.string.dynamicSend),
        DYNAMIC_SUMMARY_SEND(R.string.dynamicSummarySend),
        DYNAMIC_RECEIVE(R.string.dynamicReceive),
        STATIC_BUILD_TREE(R.string.staticBuildTree),
        STATIC_SEND(R.string.staticSend),
        STATIC_RECEIVE(R.string.staticReceive),
        DYNAMIC_ENTROPY(R.string.dynamicEntropy),
        STATIC_ENTROPY(R.string.staticEntropy),
        DYNAMIC_AVERAGE(R.string.dynamicAverage),
        STATIC_AVERAGE(R.string.staticAverage),
        DYNAMIC_CODE_COMPRESSION(R.string.dynamicCodeCompression),
        STATIC_CODE_COMPRESSION(R.string.staticCodeCompression);

        private final int stringId;

        StatsKey(int stringId) {
            this.stringId = stringId;
        }

        public int getStringId() {
            return stringId;
        }
    }
}
