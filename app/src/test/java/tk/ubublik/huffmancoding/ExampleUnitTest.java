package tk.ubublik.huffmancoding;

import org.junit.Test;

import java.util.Map;

import tk.ubublik.huffmancoding.logic.Utils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void statisticsTest(){
        Map<Character, Integer> map = Utils.getStatisticsMap("aadjdkjdkjdllllllllasdkj");
        for (Map.Entry<Character, Integer> entry: Utils.sortMapValues(map)){
            System.out.println(String.format("Entry key: %c, value: %d", entry.getKey(), entry.getValue()));
        }
    }
}