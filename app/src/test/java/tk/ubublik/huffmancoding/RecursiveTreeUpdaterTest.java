package tk.ubublik.huffmancoding;

import org.junit.Test;

import tk.ubublik.huffmancoding.logic.Leaf;
import tk.ubublik.huffmancoding.logic.RecursiveTreeUpdater;

public class RecursiveTreeUpdaterTest {

   /* private final RecursiveTreeUpdater recursiveTreeUpdater = new RecursiveTreeUpdater();
    private final Leaf leaf1 = new Leaf('a', 1);
    private  final Leaf leaf2 = new Leaf(null, 3,
            leaf1,
            new Leaf(null, 2,
                    new Leaf('b', 1),
                    leaf1));

    //@Test
    public void depthTest(){
        System.out.println("Leaf 1 depth = "+recursiveTreeUpdater.getMaxTreeDepth(leaf1));
        System.out.println("Leaf 2 depth = "+recursiveTreeUpdater.getMaxTreeDepth(leaf2));
    }

    @Test
    public void mostTest(){
        for (int i = 1; i < 5; i++){
            System.out.println(String.format("Depth = %d, max = %s, min = %s",
                    i,
                    recursiveTreeUpdater.getTheMostWeightFromDepth(leaf2, i, true),
                    recursiveTreeUpdater.getTheMostWeightFromDepth(leaf2, i, false)));
        }
    }*/
}
