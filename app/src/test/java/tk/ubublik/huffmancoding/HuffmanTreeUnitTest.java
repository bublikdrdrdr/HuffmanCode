package tk.ubublik.huffmancoding;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Map;

import tk.ubublik.huffmancoding.logic.BinaryTree;
import tk.ubublik.huffmancoding.logic.HuffmanTree;
import tk.ubublik.huffmancoding.logic.Leaf;
import tk.ubublik.huffmancoding.logic.Pair;
import tk.ubublik.huffmancoding.logic.StrictBitSet;
import tk.ubublik.huffmancoding.logic.Utils;

public class HuffmanTreeUnitTest {

    @Test
    public void sendAndReceiveDynamicTest() throws Exception{
        HuffmanTree huffmanTree1 = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
        HuffmanTree huffmanTree2 = new BinaryTree(HuffmanTree.HuffmanTreeMode.DYNAMIC);
        int charSize = Character.SIZE;
        //String sendString = "uganda warrior";
        String sendString = "24890w8593246790218yqgwdhjxmolmlkcms,lx,ascmkldngkjafdslmasklfajkdmaskf" +
                "nskafjsaklfjmask,xlas,xl,kslzkdlskdlksqwertyuiop[]asdfghjkl;zxcvbnm,./uganda warrio" +
                "r asdlkjasd askdjasd askldjaskldjaskldjaskldjkasnda dkjasdlkjasld asdjasldjlaksdjla" +
                "ksjdlksdgheroihioewtuksnvkmsnvjnc asldkjasckmopfmribnjvnlksmckasjlkasd aslkdjaslkdj" +
                "amcnlknclknclkasns adlasndlkjaslkdnasljfnirjiawnfclkan sldknasldkjalcnlkcnx.,cnmxnksdj";
        StringBuilder receiver = new StringBuilder();
        for (char c: sendString.toCharArray()){
            StrictBitSet bitSet = huffmanTree1.send(c, charSize);
            char receivedChar = huffmanTree2.receive(bitSet, charSize);
            if (!huffmanTree1.compareTree(huffmanTree2)){
                System.out.println("well.. fuck");
            }
            receiver.append(receivedChar);
        }
        Assert.assertEquals(sendString, receiver.toString());
    }

    @Test
    public void sendAndReceiveStaticTest() throws Exception{
        int charSize = Character.SIZE;
        String sendString = "uganda warrior";
        Map<Character, Integer> map = Utils.getStatisticsMap(sendString);
        HuffmanTree huffmanTree1 = new BinaryTree(map);
        HuffmanTree huffmanTree2 = new BinaryTree(map);
        StringBuilder receiver = new StringBuilder();
        for (char c: sendString.toCharArray()){
            StrictBitSet bitSet = huffmanTree1.send(c, charSize);
            char receivedChar = huffmanTree2.receive(bitSet, charSize);
            receiver.append(receivedChar);
        }
        Assert.assertEquals(sendString, receiver.toString());
    }


    public void pairTest(){
        Pair<String, String> pair = new Pair<>("a", "b");
        System.out.println(pair);
    }
}
