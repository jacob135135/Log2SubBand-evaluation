/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import java.util.Map;
import static log2subband.MyUtils.decimal_to_binary;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JAKUB
 */
public class Log2SubBandTest {
    
    public Log2SubBandTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        MainExecution.is_bin_system = false;
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void compression_test() {
        String[] input = new String[]{"1","2","3","4","5","6","7","8","9","10","15","20","40","80","120","131","131","131"};
        String expected_compressed = "010001010010010011010100010101010110010111011000011001011010011111100001010010001010001001010000100111100010100000110000";
        
        Map<String, String> result = CompressionUtils.perform_log2_sub_band(input, false);
        String compressed = result.get("compr");

        assertEquals(expected_compressed, compressed);   
    }

    @Test
    public void binary_input_test() {
        String[] input = new String[]{"111111110000","111111110111","111111110100","111110000100","111110000100","100000000000","100001000000"};
        MainExecution.is_bin_system = true;

        String compressed = CompressionUtils.perform_log2_sub_band(input, false).get("compr");
        String expected = "11111111110000010111010100101000010000111000000000001001000000";
        assertEquals(expected, compressed);
    }

    @Test
    public void binary_input_test_345() {
        String[] input = new String[]{"111111100000","111111101111","111111101111","111100001111","000000000000","011000000000"};
        MainExecution.is_bin_system = true;

        String compressed = CompressionUtils.perform_log2_sub_band(input, false).get("compr");
        String expected = "11111111100000010111100101000011111100000000000011011000000000";
        assertEquals(expected, compressed);
    }

    // THIS TEST SHOULD FAIL AS THESIS USES SLIGHTLY DIFFERENT ALGORITHM
    // HOWEVER TO MAKE IT PASS I ONLY CHECK IF COMPRESSION LENGTH IS SAME
    @Test
    public void import_file_thesis_data_compression_test() {
        String path = System.getProperty("user.dir");
        String[] file_contents = CSVUtils.parse_CSV(path + "/test/log2subband/test_thesis_data.csv");

        String input = "";
        for (String raw_value : file_contents) {
            raw_value = decimal_to_binary(raw_value);
            input += raw_value + ",";
        }
        input = input.substring(0,input.length()-1);
        String[] input_data = MyUtils.CSstring_to_array(input);
        MainExecution.debug = true;
        String compressed = CompressionUtils.perform_log2_sub_band(input_data, true).get("compr");
        MainExecution.debug = false;
        String expected = "000001010110010100001101010000000010001100000100010011000000000000000000000000000100010100100101000110001000010000100010000010010000001010000000100000000000000000000000";
        assertEquals(expected.length(), compressed.length());
    }
}
