/*  Copyright (c)  2009  Mike Neurohr.
    Modified by Jakub Brezonak in 2017
    Permission is granted to copy, distribute and/or modify this document
    under the terms of the GNU Free Documentation License, Version 1.2
    or any later version published by the Free Software Foundation;
    with no Invariant Sections, no Front-Cover Texts, and no Back-Cover
    Texts.  A copy of the license is included in the section entitled "GNU
    Free Documentation License".

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package log2subband;
import java.util.*;
import static log2subband.MainExecution.debug;
import static log2subband.MainExecution.is_bin_system;
import static log2subband.MyUtils.decimal_to_binary;

/* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }

    /* Downloaded from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
    public int compareTo(HuffmanTree tree) {return frequency - tree.frequency;}
}

/* EDITED from https://rosettacode.org/wiki/Huffman_coding under GNU Free Documentation License 1.2  */
class HuffmanLeaf extends HuffmanTree {
    //NOTE: the original Char has been changed to String to allow numbers above 9 to be a single "symbol"
    public final String value; // String this leaf represents

    public HuffmanLeaf(int freq, String val) {
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
    public static Map<String, String> number_to_encoding_dict = new HashMap<>(); // number => encoding
    public static Map<String, String> encoding_to_number_dict = new HashMap<>(); // encoding => number
    public static final int HUFFMAN_ADDITION = 2048; // NEED TO ADD TO HUFFMAN TO PREVENT NEGATIVE NUMBER INDEXES
    public static final int FREQUENCY_SIGNIFICANCE_MULTIPLIER = 1000;
    public static String[] huffman_DPCM_data; // Input data after running DPCM on them

    public static HuffmanTree buildTree(int[] charFreqs) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < charFreqs.length; i++)
            if (charFreqs[i] > 0)
                trees.offer(new HuffmanLeaf(charFreqs[i], Integer.toString(i)));

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

            number_to_encoding_dict.put(String.valueOf(leaf.value), String.valueOf(prefix));
            //if(debug) System.out.println("ADDING number: " + leaf.value + " ; encoding: " + prefix);
            encoding_to_number_dict.put(String.valueOf(prefix), String.valueOf(leaf.value));

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

    // decode_huffman method currently TEMPORARILY does not work as huffman uses DPCM and it is not yet compatible
    // disabling DPCM_for_Huffman (setting it to false) would make it work again.
    /**
     * Decodes a String input consisting only of ones and zeroes using inputted dictionary
     * @param encoded String of 1s and 0s
     * @param encod_to_number_dict Dictionary mapping of Huffman codes and their respective numbers
     * @return decoded String of decoded values
     */
    @Deprecated
    public static String decode_huffman(String encoded, Map<String, String> encod_to_number_dict) {
        if(debug) System.out.println("DECODE HUFF encoded: " + encoded);
        String current = encoded.substring(0,1);
        String decoded = "";
        while (encoded.length()>0) {
            if (encod_to_number_dict.containsKey(current)) {
                String before_transformation = encod_to_number_dict.get(current);
                int transformed = Integer.valueOf(before_transformation) - HUFFMAN_ADDITION;
                decoded +=  transformed + ",";
                if(debug) System.out.println("DECODED SO FAR : " + decoded + " (" + current + ")");
                current = "";
            }
            encoded = encoded.substring(1);
            if (encoded.length()>0) current += encoded.substring(0,1);
        }

        if (current.length()>0) {
            throw new NoSuchElementException("Unable to decode, remaining " + current + " does not exist in dictionary");
        }
        decoded = decoded.substring(0, decoded.length()-1);
        return decoded;
    }

    /**
     * Encodes a string using best Huffman compression. This method creates a Huffman tree and
     * selects best codebook for given dataset.
     * !!! Requires reading all data twice -> can't use "on the fly"/until all data arrived
     * Populates <code>number_to_encoding_dict</code> and <code>encoding_to_number_dict</code>
     * @param numbers_to_encode
     * @throws Exception If for some reason string after its encoding and decoding results in different string
     *         (can occur only if this implementation is erroneous)
     */
    public static void init_ideal_huffman_dictionaries(String[] numbers_to_encode) throws Exception {
        int[] charFreqs = new int[4096]; // Need to support all 4096 different numbers

        if(MainExecution.is_bin_system) MyUtils.bin_array_to_dec_array(numbers_to_encode);

        String[] cloned_numbers_to_encode =  numbers_to_encode.clone();
        if (MainExecution.DPCM_for_Huffman) {
            cloned_numbers_to_encode = CompressionUtils.DPCM(cloned_numbers_to_encode);
            huffman_DPCM_data = new String[cloned_numbers_to_encode.length];

            // Need to convert data to 12-bit binary numbers
            for(int i=0; i<cloned_numbers_to_encode.length; i++) {
                String cur_number = cloned_numbers_to_encode[i];
                if(!is_bin_system) {cur_number = decimal_to_binary(cur_number);}
                else {cur_number = MyUtils.binary_to_12_bits(cur_number);}
                huffman_DPCM_data[i] = cur_number;
            }
        }
        for (String numb : cloned_numbers_to_encode) {
            int number = Integer.valueOf(numb) + HUFFMAN_ADDITION; // Adds constant to avoid negative numbers
            //System.out.println(number);
            charFreqs[number]++; // Read each Number (represented as String) and record the frequencies
            if(debug) System.out.println(numb + ": " + charFreqs[number]);
        }
        charFreqs = CompressionUtils.make_frequencies_significant(charFreqs); // also forces Huffman to create encoding for all
        HuffmanTree tree = buildTree(charFreqs); // build tree
        create_huffman_tree(tree, new StringBuffer());

//        String encoded = "";
//        for (String number : numbers_to_encode) encoded += number_to_encoding_dict.get(number);
//        String decoded = decode_huffman(encoded, encoding_to_number_dict);
//        String[] decod = decoded.split(",[ ]*");
//
//        // CHECK that original data is same as one that was encoded and then decoded
//        numbers_to_encode = MyUtils.add_to_string_array(numbers_to_encode, -HUFFMAN_ADDITION);
//        if (Arrays.equals(decod, numbers_to_encode)) System.out.println("\nSUCCESS -> DECODED STRING ENCODED SAME AS ORIGINAL STRING");
//        else {
//            System.err.println("nSOMETHING WENT WRONG -> DECODED STRING ENCODED RESULTED IN DIFFERENT STRING");
//            throw new Exception("");
//        }
    }
}