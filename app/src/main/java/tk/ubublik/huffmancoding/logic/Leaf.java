package tk.ubublik.huffmancoding.logic;

import android.annotation.SuppressLint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Objects;

/**
 * Created by Bublik on 31-Mar-18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Leaf implements Cloneable{

    public static final char NIT_CHAR = 0;

    private Character character;
    private int weight;
    private Leaf left, right;

    public Leaf() {
    }

    public Leaf(Character character, int weight) {
        this.character = character;
        this.weight = weight;
    }

    public Leaf(Character character, int weight, Leaf left, Leaf right) {
        this.character = character;
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    public static char getNitChar() {
        return NIT_CHAR;
    }

    public void set(Character character, int weight, Leaf left, Leaf right){
        setCharacter(character);
        setWeight(weight);
        setLeft(left);
        setRight(right);
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void changeWeight(int difference){
        this.weight += difference;
    }

    public Leaf getLeft() {
        return left;
    }

    public void setLeft(Leaf left) {
        this.left = left;
    }

    public Leaf getRight() {
        return right;
    }

    public void setRight(Leaf right) {
        this.right = right;
    }

    @Override
    public String toString(){
        return prettyToString(true, true);
    }

    public String fullToString(){
        try {
            return new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return super.toString();
        }
    }

    public String prettyToString(boolean includeChildren, boolean includeClassName){
        StringBuilder stringBuilder = new StringBuilder((includeClassName?"Leaf{":"")+"'"+character+"'="+weight);
        if (includeChildren && (left!=null || right!=null)){
            stringBuilder.append(':');
            stringBuilder.append(left==null?"null":left.prettyToString(false, false));
            stringBuilder.append('|');
            stringBuilder.append(right==null?"null":right.prettyToString(false, false));
        }
        if (includeClassName) stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public String toString(boolean includeChildLeafs){
        return includeChildLeafs?toString():new Leaf(character, weight).toString();

    }

    @Override
    protected Leaf clone() {
        return new Leaf(character, weight);
    }

    public boolean fixWeight() throws UnsupportedOperationException{
        if (character!=null) throw new UnsupportedOperationException(String.format("Can't fix weight of last leaf with char value %s", character==NIT_CHAR?"NIT":Character.toString(character)));
        int oldWeight = weight;
        weight = 0;
        if (left!=null) weight += left.getWeight();
        if (right!=null) weight += right.getWeight();
        return weight!=oldWeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Leaf)) return false;
        Leaf leafObject = (Leaf)obj;
        return equals(this, leafObject);
    }

    private boolean equals(Leaf leaf1, Leaf leaf2) {
        if (leaf1 == leaf2) return true;
        if (!Objects.equals(leaf1.getCharacter(), leaf2.getCharacter())) return false;
        if (!Objects.equals(leaf1.getWeight(), leaf2.getWeight())) return false;
        boolean l1LeftNull = leaf1.left == null, l1RightNull = leaf1.right == null, l2LeftNull = leaf2.left == null, l2RightNull = leaf2.right == null;
        return l1LeftNull == l2LeftNull && l1RightNull == l2RightNull && equals(leaf1.left, leaf2.left) && equals(leaf1.right, leaf2.right);
    }
}
