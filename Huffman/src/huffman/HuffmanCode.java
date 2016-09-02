
package huffman;
import java.util.*;
import javax.swing.JOptionPane;

/* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }
 
    /* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
    // compares on the frequency
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}

/* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
class HuffmanLeaf extends HuffmanTree {
    public final char value; // the character this leaf represents
 
    public HuffmanLeaf(int freq, char val) {
        super(freq);
        value = val;
    }
}

/* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
class HuffmanNode extends HuffmanTree {
    public final HuffmanTree left, right; // subtrees
 
    public HuffmanNode(HuffmanTree l, HuffmanTree r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}
 
/* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
public class HuffmanCode {
    public static Map<String, String> key_val_dict = new HashMap<String, String>();
    public static Map<String, String> val_key_dict = new HashMap<String, String>();
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(int[] charFreqs) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < charFreqs.length; i++)
            if (charFreqs[i] > 0)
                trees.offer(new HuffmanLeaf(charFreqs[i], (char)i));
 
        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();
 
            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }
    
    /* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
    public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
        assert tree != null;
        
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
 
            // print out character, frequency, and code for this leaf (which is just the prefix)
            System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            key_val_dict.put(String.valueOf(leaf.value), String.valueOf(prefix));
            val_key_dict.put(String.valueOf(prefix), String.valueOf(leaf.value));
            
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);     
        }
    }
 
    /**
     * Decoded a String input consisting only of ones and zeroes using inputted dictionary 
     * @param encoded String of 1s and 0s
     * @param val_key_dict Dictionary mapping of symbols and their respective encodings
     * @return decoded String of decoded values
     */
    public static String decodeHuf(String encoded, Map<String, String> val_key_dict) {
        String enc = encoded;
        String current = enc.substring(0,1);
        String decoded = "";
        while (enc.length()>0) {
            if (val_key_dict.containsKey(current)) {
                decoded += val_key_dict.get(current);
                current = "";
            }
            enc = enc.substring(1);
            if (enc.length()>0) current += enc.substring(0,1);
        }
        return decoded;
    }
    public static void main(String[] args) {        
        String answer;
        answer = JOptionPane.showInputDialog(null, "Type in string to encode.");
        String test = answer;
 
        int[] charFreqs = new int[256]; // we will assume max 256 different characters   
        for (char c : test.toCharArray()) charFreqs[c]++; // read each character and record the frequencies
  
        HuffmanTree tree = buildTree(charFreqs); // build tree
 
        // print out results
        System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
        printCodes(tree, new StringBuffer());
        
        String encoded = "";
        for (char c : test.toCharArray()) encoded += key_val_dict.get(String.valueOf(c));
        
        String altogether = "String to encode:\n" + test;
        altogether += "\nWhole encoded string: \n" + encoded;
        String decoded = decodeHuf(encoded, val_key_dict);
        
        System.out.println(altogether);
        System.out.println("\nDecoded:\n" + decoded);
        
        if (decoded.equals(test)) System.out.println("\nSUCCESS -> DECODED STRING ENCODED SAME AS ORIGINAL STRING");
        else System.out.println("\nSOMETHING WENT WRONG -> DECODED STRING ENCODED RESULTED IN DIFFERENT STRING");
              
    }
}