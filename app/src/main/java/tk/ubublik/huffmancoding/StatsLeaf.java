package tk.ubublik.huffmancoding;

import android.os.Parcel;
import android.os.Parcelable;

import tk.ubublik.huffmancoding.logic.StrictBitSet;

public class StatsLeaf implements Parcelable{

    public Character character;
    public int count;
    public float probability;
    public String code;

    public StatsLeaf() {
    }

    public StatsLeaf(char character, int count, float probability, String code) {
        this.character = character;
        this.count = count;
        this.probability = probability;
        this.code = code;
    }

    protected StatsLeaf(Parcel in) {
        int tmpCharacter = in.readInt();
        character = tmpCharacter != Integer.MAX_VALUE ? (char) tmpCharacter : null;
        count = in.readInt();
        probability = in.readFloat();
        code = in.readString();
    }

    public static final Creator<StatsLeaf> CREATOR = new Creator<StatsLeaf>() {
        @Override
        public StatsLeaf createFromParcel(Parcel in) {
            return new StatsLeaf(in);
        }

        @Override
        public StatsLeaf[] newArray(int size) {
            return new StatsLeaf[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(character != null ? (int) character : Integer.MAX_VALUE);
        dest.writeInt(count);
        dest.writeFloat(probability);
        dest.writeString(code);
    }
}
