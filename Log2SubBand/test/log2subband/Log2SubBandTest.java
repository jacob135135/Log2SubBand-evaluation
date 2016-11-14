/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import java.util.Map;
import static log2subband.Log2SubBand.log2_sub_band_decode_string;
import static log2subband.Log2SubBand.parameters;
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
        parameters = new int[]{4,4,4};
        Log2SubBand.update_previous_bands(parameters);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void compression_test() {
        String[] input = new String[]{"1","2","3","4","5","6","7","8","9","10","15","20","40","80","120","131","131","131"};
        String expected_compressed = "010001010010010011010100010101010110010111011000011001011010011111100001010010001010001001010000100111100010100000110000";
        
        Map<String, String> result = CompressionUtils.perform_log2_sub_band_compression(input);
        String compressed = result.get("overall_compressed");

        assertEquals(expected_compressed, compressed);   
    }

    @Test
    public void decompression_test() {
        String input = "1,2,3,4,5,6,7,8,9,10,15,20,40,80,120,131,131,131";
        String compressed = "010001010010010011010100010101010110010111011000011001011010011111100001010010001010001001010000100111100010100000110000";
        String decoded = log2_sub_band_decode_string(compressed);

        Assert.assertEquals(decoded, input);
    }

    @Test
    public void full_log2subBand_test() {
        String[] input = new String[]{"11","23","23","24","45","46","47","68","9","10","15","20","40","80","120","131","131"};
        String compressed = CompressionUtils.perform_log2_sub_band_compression(input).get("overall_compressed");
        System.out.println("COMPRESSED :" + compressed);
        String decoded = log2_sub_band_decode_string(compressed);
        String[] decoded_array = MyUtils.CSstring_to_array(decoded);

        Assert.assertArrayEquals(decoded_array, input);
    }
}
