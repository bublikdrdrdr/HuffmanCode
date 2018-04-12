package tk.ubublik.huffmancoding;

import tk.ubublik.huffmancoding.logic.Pair;

import android.os.Parcel;
import android.os.Parcelable;

public class CharStringPair extends Pair<Character, String> implements Parcelable {

    public CharStringPair(Character first, String second) {
        super(first, second);
    }

    private CharStringPair(Parcel in) {
        int tmpCharacter = in.readInt();
        first = tmpCharacter != Integer.MAX_VALUE ? (char) tmpCharacter : null;
        second = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(first != null ? (int) first : Integer.MAX_VALUE);
        dest.writeString(second);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CharStringPair> CREATOR = new Creator<CharStringPair>() {
        @Override
        public CharStringPair createFromParcel(Parcel in) {
            return new CharStringPair(in);
        }

        @Override
        public CharStringPair[] newArray(int size) {
            return new CharStringPair[size];
        }
    };
}
