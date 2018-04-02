package tk.ubublik.huffmancoding;

import org.junit.Test;

import java.util.Random;

/**
 * Created by Bublik on 31-Mar-18.
 */

public class SpeedComparisonTest {

    @Test
    public void stringCharIteratorTest(){
        final int stringLength = 20000;
        System.out.println("String length: "+stringLength);
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++){
            stringBuilder.append('a'+random.nextInt(28));
        }
        StringBuilder worker = new StringBuilder();
        String string = stringBuilder.toString();
        long nanos = System.nanoTime();
        for (int i = 0; i < string.length(); i++){
            worker.append(string.charAt(i));
        }
        nanos = System.nanoTime()-nanos;
        System.out.println("string.charAt nanos: \t\t"+nanos);
        worker = new StringBuilder();
        nanos = System.nanoTime();
        for (char c: string.toCharArray()){
            worker.append(c);
        }
        nanos = System.nanoTime()-nanos;
        System.out.println("toCharArray() nanos: \t\t"+nanos);

    }
}
