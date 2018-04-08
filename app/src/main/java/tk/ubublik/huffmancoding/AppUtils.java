package tk.ubublik.huffmancoding;

import java.util.concurrent.Callable;

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
}
