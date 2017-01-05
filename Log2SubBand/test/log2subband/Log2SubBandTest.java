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
        parameters = new int[]{4,4,4,0};
        Log2SubBand.update_previous_bands(parameters);
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
        String compressed = CompressionUtils.perform_log2_sub_band(input, true).get("compr");
        String expected = "11111111110000010111010100101000010000111000000000001001000000";
        assertEquals(expected, compressed);
    }

    @Test
    public void binary_input_test_345() {
        parameters = new int[]{3,4,5,0};
        Log2SubBand.update_previous_bands(parameters);
        String[] input = new String[]{"111111100000","111111101111","111111101111","111100001111","000000000000","011000000000"};
        String compressed = CompressionUtils.perform_log2_sub_band(input, true).get("compr");
        String expected = "11111111100000010111100101000011111100000000000011011000000000";
        assertEquals(expected, compressed);
    }

    @Test
    public void decompression_test() {
        String input = "1,2,3,4,5,6,7,8,9,10,15,20,40,80,120,131,131,131";
        String compressed = "010001010010010011010100010101010110010111011000011001011010011111100001010010001010001001010000100111100010100000110000";
        String decoded = log2_sub_band_decode_string(compressed);

        Assert.assertEquals(decoded, input);
    }

    @Test
    public void full_log2_sub_band_test() {
        String[] input = new String[]{"11","23","23","24","45","46","47","68","9","10","15","20","40","80","120","131","131"};
        String compressed = CompressionUtils.perform_log2_sub_band(input, false).get("compr");

        String decoded = log2_sub_band_decode_string(compressed);
        String[] decoded_array = MyUtils.CSstring_to_array(decoded);

        Assert.assertArrayEquals(decoded_array, input);
    }

    @Test
    public void full_log2_sub_band_test_3333() {
        parameters = new int[]{3,3,3,3};
        Log2SubBand.update_previous_bands(parameters);
        String[] input = new String[]{"11","23","23","24","45","46","47","68","9","10","15","20","40","80","120","131","131"};
        String compressed = CompressionUtils.perform_log2_sub_band(input, false).get("compr");

        String decoded = log2_sub_band_decode_string(compressed);
        String[] decoded_array = MyUtils.CSstring_to_array(decoded);

        Assert.assertArrayEquals(decoded_array, input);
    }

    @Test
    public void full_log2_sub_band_test_with_354_test() {
        parameters = new int[]{3,5,4,0};
        Log2SubBand.update_previous_bands(parameters);

        String[] input = new String[]{"11","23","23","24","45","46","47","68","9","10","15","20","40","80","120","131","131"};
        String expected_compressed = "0110111000001011100011000100001011010111100111111000100010010000001001011010011111100000101001000010100010001010000100011110001001000001100";
        String compressed = CompressionUtils.perform_log2_sub_band(input, false).get("compr");
        assertEquals(compressed, expected_compressed);

        String decoded = log2_sub_band_decode_string(compressed);
        String[] decoded_array = MyUtils.CSstring_to_array(decoded);
        Assert.assertArrayEquals(decoded_array, input);
    }

    @Test
    public void full_log2_sub_band_test_with_273_test() {
        parameters = new int[]{2,7,3,0};
        Log2SubBand.update_previous_bands(parameters);

        String[] input = new String[]{"11","23","23","24","45","46","47","68","9","10","15","20","40","80","120","131","131"};
        String expected_compressed = "100000001011100000010111001000000110001000001011010111001111100001000100100000001001010100111110000001010010000010100010000101000010000111100010001000001100";
        String compressed = CompressionUtils.perform_log2_sub_band(input, false).get("compr");
        assertEquals(compressed, expected_compressed);

        String decoded = log2_sub_band_decode_string(compressed);
        String[] decoded_array = MyUtils.CSstring_to_array(decoded);
        Assert.assertArrayEquals(decoded_array, input);
    }
}
