package tk.ubublik.huffmancoding;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tk.ubublik.huffmancoding.logic.Leaf;
import tk.ubublik.huffmancoding.logic.Utils;

public class VisualizedLeaf implements Parcelable {

    /**

     # ---------->  X
     |
     |  Canvas
     |
     |
     v

     Y

     */

    public Character character;
    public int weight;
    public PointF position;
    public PointF connectedTo;

    /*TODO: try to implement this
    public PointF connectedToLeft;
    public PointF connectedToRight;
     */

    public VisualizedLeaf() {
    }

    public VisualizedLeaf(Character character, int weight, PointF position, PointF connectedTo) {
        this.character = character;
        this.weight = weight;
        this.position = position;
        this.connectedTo = connectedTo;
    }

    protected VisualizedLeaf(Parcel in) {
        int tmpCharacter = in.readInt();
        character = tmpCharacter != Integer.MAX_VALUE ? (char) tmpCharacter : null;
        weight = in.readInt();
        position = in.readParcelable(PointF.class.getClassLoader());
        connectedTo = in.readParcelable(PointF.class.getClassLoader());
    }

    public static final Creator<VisualizedLeaf> CREATOR = new Creator<VisualizedLeaf>() {
        @Override
        public VisualizedLeaf createFromParcel(Parcel in) {
            return new VisualizedLeaf(in);
        }

        @Override
        public VisualizedLeaf[] newArray(int size) {
            return new VisualizedLeaf[size];
        }
    };

    public static ArrayList<VisualizedLeaf> treeToList(Leaf leaf, PointF padding){
        return treeToListRecursive(leaf, false, null, padding, new ArrayList<>(), Utils.getMaxTreeDepth(leaf));
    }

    private static ArrayList<VisualizedLeaf> treeToListRecursive(Leaf leaf, boolean side, VisualizedLeaf parent, PointF padding, ArrayList<VisualizedLeaf> list, int treeHeight){
        PointF position = new PointF();
        if (parent!=null){
            position.set(parent.position.x + (float)Math.pow(2, treeHeight-1)*padding.x*(side?1:-1), parent.position.y+padding.y);
        }
        VisualizedLeaf visualizedLeaf = new VisualizedLeaf(leaf.getCharacter(), leaf.getWeight(), position, parent==null?null:parent.position);
        //System.out.println(String.format("Leaf: %s, %d, %s -> %s", String.valueOf(visualizedLeaf.character), visualizedLeaf.weight, visualizedLeaf.position.toString(), visualizedLeaf.connectedTo));
        list.add(visualizedLeaf);
        if (leaf.getLeft()!=null) treeToListRecursive(leaf.getLeft(), false, visualizedLeaf, padding, list, treeHeight-1);
        if (leaf.getRight()!=null) treeToListRecursive(leaf.getRight(), true, visualizedLeaf, padding, list, treeHeight-1);
        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(character != null ? (int) character : Integer.MAX_VALUE);
        dest.writeInt(weight);
        dest.writeParcelable(position, flags);
        dest.writeParcelable(connectedTo, flags);
    }

    /*public static VisualizedLeaf buildFromTree(Leaf leaf, @Nullable VisualizedLeaf parentLeaf){
        if (leaf==null) return null;
        VisualizedLeaf result = new VisualizedLeaf(leaf.getCharacter(), leaf.getWeight());
        result.left = buildFromTree(leaf.getLeft(), result);
        result.right = buildFromTree(leaf.getRight(), result);
        result.parent = parentLeaf;
        return result;
    }*/
}
