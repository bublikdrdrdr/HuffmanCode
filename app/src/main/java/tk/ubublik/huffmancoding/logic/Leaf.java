package tk.ubublik.huffmancoding.logic;

import android.annotation.SuppressLint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.BitSet;
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
    public String toString() {
        try {
            return new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return super.toString();
        }
    }

    public String toString(boolean includeChildLeafs){
        return includeChildLeafs?toString():new Leaf(character, weight).toString();

    }

    @Override
    protected Leaf clone() {
        return new Leaf(character, weight);
    }
}
