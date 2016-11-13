/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import java.util.Map;
import static log2subband.Log2SubBand.parameters;
import org.junit.After;
import org.junit.AfterClass;
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
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void compression_test() {
        parameters = new int[]{4,4,4};
        Log2SubBand.previous_least_significant_nibble = MyUtils.generate_zeroes(parameters[2]);
        Log2SubBand.previous_middle_nibble = MyUtils.generate_zeroes(parameters[1]);
        Log2SubBand.previous_most_significant_nibble = MyUtils.generate_zeroes(parameters[0]);
        
        String[] input = new String[]{"1","2","3","4","5","6","7","8","9","10","15","20","40","80","120","131","131","131"};
        String expected_compressed = "010001010010010011010100010101010110010111011000011001011010011111100001010010001010001001010000100111100010100000110000";
        
        Map<String, String> result = CompressionUtils.perform_log2_sub_band_compression(input);
        String compressed = result.get("overall_compressed");

        assertEquals(expected_compressed, compressed);   
    }
}
