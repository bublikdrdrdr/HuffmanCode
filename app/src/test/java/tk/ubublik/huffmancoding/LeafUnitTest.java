package tk.ubublik.huffmancoding;

import org.junit.Test;

import java.util.Random;

import tk.ubublik.huffmancoding.logic.BinaryTree;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;
import tk.ubublik.huffmancoding.logic.Utils;

import static tk.ubublik.huffmancoding.logic.Leaf.NIT_CHAR;

public class LeafUnitTest {

   /* private Random random = new Random();
    private char[] chars = shuffleArray(getCharArray());
    private BinaryTree binaryTree;

    //@Test
    public void leafToStringTest(){
        System.out.println("toString() test:");
        binaryTree = new BinaryTree(generateLeafTree(), HuffmanTree.HuffmanTreeMode.DYNAMIC);
        System.out.println(binaryTree);
        System.out.println("////////////////////////////");
        System.out.println(binaryTree.find(NIT_CHAR).toString(false));
    }

    @Test
    public void leafFromMapTest(){
        System.out.println(new BinaryTree(Utils.getStatisticsMap("uganda warrior")));
    }

    private Leaf generateLeafTree(){
        return new Leaf('a', 1,
                new Leaf('b', 2,
                        new Leaf('d', 4),
                        new Leaf('e', 5)),
                new Leaf('c', 3,
                        new Leaf('f', 6),
                        new Leaf('g', 7,
                                new Leaf(NIT_CHAR, 0),
                                null)));
    }

    private Leaf generateRandomLeafTree(int depth){
        index = 0;
        return generateRandomLeafTreeRecursive(depth);
    }
    private int index;
    private Leaf generateRandomLeafTreeRecursive(int depth){
        if (depth==0) return null;
        if (depth>Math.log(chars.length+1)/Math.log(2.0)) throw new RuntimeException("Char array doesn't have enough items to build binary tree with depth = "+depth);
        depth--;
        return new Leaf(chars[index++], random.nextInt(100), generateRandomLeafTreeRecursive(depth), generateRandomLeafTreeRecursive(depth));
    }

    private char[] getCharArray(){
        int digitsCount = 10;
        int lettersCount = 26;
        char[] array = new char[lettersCount*2+digitsCount];
        for (int i = 0; i < digitsCount; i++) array[i] = (char)('0'+i);
        for (int i = 0; i < lettersCount; i++) array[i+digitsCount] = (char)('A'+i);
        for (int i = 0; i < lettersCount; i++) array[i+digitsCount+lettersCount] = (char)('a'+i);
        return array;
    }

    public char[] shuffleArray(char[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
        return a;
    }

    private void swap(char[] a, int i, int change) {
        char helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }*/
}
