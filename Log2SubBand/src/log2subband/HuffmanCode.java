
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

/* EDITED from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
public class HuffmanCode {
    /* Needs to be static as using recursive function to add data to it */
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
    public static void create_huffman_tree(HuffmanTree tree, StringBuffer prefix) {
        assert tree != null;

        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;

            symbol_to_encoding_dict.put(String.valueOf(leaf.value), String.valueOf(prefix));
            encoding_to_symbol_dict.put(String.valueOf(prefix), String.valueOf(leaf.value));

        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;

            // traverse left
            prefix.append('0');
            create_huffman_tree(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);

            // traverse right
            prefix.append('1');
            create_huffman_tree(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

    /**
     * Decodes a String input consisting only of ones and zeroes using inputted dictionary
     * @param encoded String of 1s and 0s
     * @param encod_to_symbol_dict Dictionary mapping of Huffman codes and their respective symbols
     * @return decoded String of decoded values
     */
    public static String decode_huffman(String encoded, Map<String, String> encod_to_symbol_dict) {
        String current = encoded.substring(0,1);
        String decoded = "";
        while (encoded.length()>0) {
            if (encod_to_symbol_dict.containsKey(current)) {
                decoded += encod_to_symbol_dict.get(current);
                current = "";
            }
            encoded = encoded.substring(1);
            if (encoded.length()>0) current += encoded.substring(0,1);
        }

        if (current.length()>0) {
            throw new NoSuchElementException("Unable to decode, remaining" + current + " does not exist in dictionary");
        }
        return decoded;
    }

    /**
     * Encodes a String input to ones and zeroes using inputted Huffman codebook
     * Does not require building Huffman tree
     * @param to_encode String to encode
     * @param symbol_to_encod_dict Dictionary mapping of symbols and their respective Huffman codes
     * @return encoded String using Huffman codebook
     */
    public static String encode_huffman(String to_encode, Map<String, String> symbol_to_encod_dict) {
        String current = to_encode.substring(0,1);
        String encoded = "";
        while (to_encode.length()>0) {
            if (symbol_to_encod_dict.containsKey(current)) {
                encoded += symbol_to_encod_dict.get(current);
                current = "";
            } else {
                throw new NoSuchElementException("Codebook ERROR, no encoding found for '" + current + "'");
            }
            to_encode = to_encode.substring(1);
            if (to_encode.length()>0) current += to_encode.substring(0,1);
        }
        return encoded;
    }

    /**
     * Encodes a string using best Huffman compression. This method creates a Huffman tree and
     * selects best codebook for given dataset.
     * !!! Requires reading all data twice -> can't use "on the fly"/until all data arrived
     * Populates <code>symbol_to_encoding_dict</code> and <code>encoding_to_symbol_dict</code>
     * @param string_to_encode
     * @throws Exception If for some reason string after its encoding and decoding results in different string
     *         (can occur only if this implementation is erroneous)
     */
    public static void Huffman_best_compression(String string_to_encode) throws Exception {
        int[] charFreqs = new int[256]; // Assume max 256 different characters
        for (char c : string_to_encode.toCharArray()) charFreqs[c]++; // read each character and record the frequencies
        HuffmanTree tree = buildTree(charFreqs); // build tree

        create_huffman_tree(tree, new StringBuffer());

        String encoded = "";
        for (char c : string_to_encode.toCharArray()) encoded += symbol_to_encoding_dict.get(String.valueOf(c));
        String decoded = decode_huffman(encoded, encoding_to_symbol_dict);
        if (decoded.equals(string_to_encode)) System.out.println("\nSUCCESS -> DECODED STRING ENCODED SAME AS ORIGINAL STRING");
        else throw new Exception("\nSOMETHING WENT WRONG -> DECODED STRING ENCODED RESULTED IN DIFFERENT STRING");
    }
}