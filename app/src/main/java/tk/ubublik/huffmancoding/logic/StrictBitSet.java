package tk.ubublik.huffmancoding.logic;

import java.io.Serializable;
import java.util.BitSet;

public class StrictBitSet implements Cloneable, Serializable{

    private static final long serialVersionUID = 5465421873248942468L;

    private BitSet bitSet;
    private int length = 0;

    public int getLength() {
        return length;
    }

    private void updateLength(int maxIndex){
        if (maxIndex>=length) length = maxIndex+1;
    }

    public StrictBitSet() {
        bitSet = new BitSet();
    }

    public StrictBitSet(int nbits) {
        updateLength(nbits-1);
        bitSet = new BitSet(nbits);
    }

    public StrictBitSet(BitSet bitSet, int length){
        this.bitSet = bitSet;
        this.length = length;
    }

    public byte[] toByteArray() {
        return bitSet.toByteArray();
    }

    public void flip(int bitIndex) {
        flip(bitIndex, length-1);
    }

    public void flip(int fromIndex, int toIndex) {
        updateLength(toIndex);
        bitSet.flip(fromIndex, toIndex);
    }

    public void set(int bitIndex) {
        set(bitIndex, true);
    }

    public void set(int bitIndex, boolean value) {
        updateLength(bitIndex);
        bitSet.set(bitIndex, value);
    }

    public void set(int fromIndex, int toIndex) {
        set(fromIndex, toIndex, true);
    }

    public void set(int fromIndex, int toIndex, boolean value) {
        updateLength(toIndex);
        bitSet.set(fromIndex, toIndex, value);
    }

    public void clear(int bitIndex) {
        set(bitIndex, false);
    }

    public void clear(int fromIndex, int toIndex) {
        set(fromIndex, toIndex, false);
    }

    public void clear() {
        bitSet.clear();
    }

    public boolean get(int bitIndex) {
        return bitSet.get(bitIndex);
    }

    public StrictBitSet get(int fromIndex, int toIndex) {
        return new StrictBitSet(bitSet.get(fromIndex, toIndex), Math.abs(toIndex-fromIndex));
    }

    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    public void and(BitSet set) {
        bitSet.and(set);
    }

    public void or(BitSet set) {
        bitSet.or(set);
    }

    public void xor(BitSet set) {
        bitSet.xor(set);
    }

    public void andNot(BitSet set) {
        bitSet.andNot(set);
    }

    public void setLength(int newLength){
        if (newLength<length)
            bitSet.clear(newLength, length-1);
        length = newLength;
    }

    public StrictBitSet add(boolean value){
        set(length, value);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StrictBitSet)) return false;
        StrictBitSet castObject = (StrictBitSet)obj;
        return bitSet.equals(castObject.bitSet)&&(length==castObject.length);
    }

    @Override
    public StrictBitSet clone() {
        return new StrictBitSet((BitSet)bitSet.clone(), length);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean includeClassName) {
        return toString(includeClassName, false);
    }

    public String toString(boolean includeClassName, boolean inverted){
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++){
            sb.append(bitSet.get(inverted?i:length-i-1)?1:0);
        }
        return includeClassName?("StrictBitSet{"+sb.toString()+"}"):sb.toString();
    }

    public void invert() {
        for (int i = 0; i < length/2; i++){
            boolean temporaryValue = bitSet.get(i);
            bitSet.set(i, bitSet.get(length-i-1));
            bitSet.set(length-i-1, temporaryValue);
        }
    }
}
