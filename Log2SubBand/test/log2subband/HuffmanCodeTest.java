/*
 * TESTS ASSUME that FREQUENCY_SIGNIFICANCE_MULTIPLIER = 1000;
 */
package log2subband;

import java.util.HashMap;
import java.util.Map;
import static log2subband.MyUtils.decimal_to_binary;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HuffmanCodeTest {
    private final int[] freq_array = new int[4096];
    
    public HuffmanCodeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        freq_array[12] = 13;
        freq_array[11] = 10;
        HuffmanCode.encoding_to_number_dict = new HashMap<>(); // IMPORTANT
        HuffmanCode.number_to_encoding_dict = new HashMap<>(); // IMPORTANT
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void build_tree_test() {
        HuffmanTree sample_tree = HuffmanCode.buildTree(freq_array);
        assertTrue(sample_tree.frequency == 23);
        HuffmanNode node = (HuffmanNode)sample_tree;
        assertTrue((node.left.frequency  == 10) | (node.left.frequency  == 13));
        assertTrue((node.right.frequency == 10) | (node.right.frequency == 13));
        assertTrue(node.right.frequency  != node.left.frequency);   
    }
     
    @Test
    public void create_huffman_tree_test() {
        HuffmanTree sample_tree = HuffmanCode.buildTree(freq_array);
        HuffmanCode.create_huffman_tree(sample_tree, new StringBuffer());
        assertTrue("1".equals(HuffmanCode.number_to_encoding_dict.get("12")));
        assertTrue("0".equals(HuffmanCode.number_to_encoding_dict.get("11")));
        
        assertTrue("12".equals(HuffmanCode.encoding_to_number_dict.get("1")));
        assertTrue("11".equals(HuffmanCode.encoding_to_number_dict.get("0")));
    }
    
    @Test
    public void decode_huffman_test() {
        String input = "1,2,3,4,5,6,7,8,9,10,15,20,40,80,120,131,131,131";
        String compressed = "1111110111110101011000101110100010011010000001100111110110001110011011100100100";
        
        // Manually init dictionary; 2048 added to all to also allow negative numbers
       Map<String, String> enc_dict = new HashMap<>();
        enc_dict.put("11111", "2049");
        enc_dict.put("10111", "2050");
        enc_dict.put("11010", "2051");
        enc_dict.put("10110", "2052");
        enc_dict.put("0010", "2053");
        enc_dict.put("11101", "2054");
        enc_dict.put("0001", "2055");
        enc_dict.put("0011", "2056");
        enc_dict.put("0100", "2057");
        enc_dict.put("0000", "2058");
        enc_dict.put("11001", "2063");
        enc_dict.put("11110", "2068");
        enc_dict.put("11000", "2088");
        enc_dict.put("11100", "2128");
        enc_dict.put("11011", "2168");
        enc_dict.put("100", "2179");
        
        String decoded = HuffmanCode.decode_huffman(compressed, enc_dict);
        assertEquals(decoded, input);   
    }
    
    @Test
    public void init_ideal_huffman_dictionaries_test() throws Exception {
        String[] input = new String[]{"1","2","3","4","5","6","7","8","9","10","15","20","40","80","120","131","131","131"};
        HuffmanCode.init_ideal_huffman_dictionaries(input);
        
        assertEquals(HuffmanCode.number_to_encoding_dict.get("2049"),"11111");
        assertEquals(HuffmanCode.encoding_to_number_dict.get("11111"),"2049");
        
        assertEquals(HuffmanCode.number_to_encoding_dict.get("2068"),"11110");
        assertEquals(HuffmanCode.encoding_to_number_dict.get("11110"),"2068");
        
        assertEquals(HuffmanCode.number_to_encoding_dict.get("2050"),"10111");
        assertEquals(HuffmanCode.encoding_to_number_dict.get("10111"),"2050");
        
        assertEquals(HuffmanCode.number_to_encoding_dict.get("2052"),"10110");
        assertEquals(HuffmanCode.encoding_to_number_dict.get("10110"),"2052");
        
        assertEquals(HuffmanCode.number_to_encoding_dict.get("2179"),"100");
        assertEquals(HuffmanCode.encoding_to_number_dict.get("100"),"2179");
    }

    @Test
    public void import_file_full_compression_test() {
        String path = System.getProperty("user.dir");
        String[] file_contents = CSVUtils.parse_CSV(path + "/test/log2subband/test2.csv");
        CompressionUtils.init_codebook_from_imported_codebook(CSVUtils.parse_CSV(path + "/test/log2subband/codebook.csv"));

        String input = "";
        for (String raw_value : file_contents) {
            raw_value = decimal_to_binary(raw_value);
            input += "," + raw_value;
        }
        String[] input_data = MyUtils.CSstring_to_array(input);
        String encoding = CompressionUtils.get_full_huffman_encoding(input_data);

        String expected = "011101000110110111010001101101110100011011110010110010100000101101110001001011011100010110111010001101101110100011011011101000110110111010001101101110100011011011101000110110111010001101101110100011011110001101000011001100100111010110100101010100000111011101010001110111010001101101110100011011011101000110110111010001101101110100011011011101000110110111010001101101110100011011";
        assertEquals(expected, encoding);
    }
}
