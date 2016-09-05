
package log2subband;
import java.util.*;

/* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }

    /* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
    // compares on the frequency
    public int compareTo(HuffmanTree tree) {return frequency - tree.frequency;}
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
    public static Map<String, String> symbol_to_encoding_dict = new HashMap<>(); // symbol => encoding
    public static Map<String, String> encoding_to_symbol_dict = new HashMap<>(); // encoding => symbol

    public static HuffmanTree buildTree(int[] charFreqs) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<>();
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

    /* EDITED from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
    public static void run_huffman(HuffmanTree tree, StringBuffer prefix) {
        assert tree != null;

        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;

            symbol_to_encoding_dict.put(String.valueOf(leaf.value), String.valueOf(prefix));
            encoding_to_symbol_dict.put(String.valueOf(prefix), String.valueOf(leaf.value));

        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;

            // traverse left
            prefix.append('0');
            run_huffman(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);

            // traverse right
            prefix.append('1');
            run_huffman(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

    /**
     * Decodes a String input consisting only of ones and zeroes using inputted dictionary
     * @param encoded String of 1s and 0s
     * @param encoding_to_symbol_dict Dictionary mapping of Huffman codes and their respective symbols
     * @return decoded String of decoded values
     */
    public static String decodeHuf(String encoded, Map<String, String> encoding_to_symbol_dict) {
        String enc = encoded;
        String current = enc.substring(0,1);
        String decoded = "";
        while (enc.length()>0) {
            if (encoding_to_symbol_dict.containsKey(current)) {
                decoded += encoding_to_symbol_dict.get(current);
                current = "";
            }
            enc = enc.substring(1);
            if (enc.length()>0) current += enc.substring(0,1);
        }
        return decoded;
    }
    public static void Huffman_compress(String string_to_encode) {
        int[] charFreqs = new int[256]; // Assume max 256 different characters
        for (char c : string_to_encode.toCharArray()) charFreqs[c]++; // read each character and record the frequencies
        HuffmanTree tree = buildTree(charFreqs); // build tree

        run_huffman(tree, new StringBuffer());

        String encoded = "";
        for (char c : string_to_encode.toCharArray()) encoded += symbol_to_encoding_dict.get(String.valueOf(c));
        String decoded = decodeHuf(encoded, encoding_to_symbol_dict);
        if (decoded.equals(string_to_encode)) System.out.println("\nSUCCESS -> DECODED STRING ENCODED SAME AS ORIGINAL STRING");
        else System.out.println("\nSOMETHING WENT WRONG -> DECODED STRING ENCODED RESULTED IN DIFFERENT STRING");
    }
}