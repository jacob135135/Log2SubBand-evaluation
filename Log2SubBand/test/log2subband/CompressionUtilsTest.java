/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import static log2subband.CompressionUtils.get_band0;
import static log2subband.CompressionUtils.get_band1;
import static log2subband.CompressionUtils.get_band2;
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
public class CompressionUtilsTest {
    
    private static String sample_bin_input;
    private String result;
    
    public CompressionUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        Log2SubBand.parameters = new int[] {4,4,4,0};
        Log2SubBand.update_previous_bands(parameters);
        sample_bin_input = "010110101110";
    }
    
    @After
    public void tearDown() {
    }

    @Test 
    public void get_band0_test() {
        result = get_band0(sample_bin_input);
        assertEquals("0101", result);
    }
    
    @Test 
    public void get_band1_test() {
        result = get_band1(sample_bin_input);
        assertEquals("1010", result);
    }
    
    @Test 
    public void get_band3_test() {
        result = get_band2(sample_bin_input);
        assertEquals("1110", result);
    }
    
    @Test
    public void bands_test0() {
        Log2SubBand.parameters = new int[] {5,0,7,0};
        
        result = get_band0(sample_bin_input);
        assertEquals("01011", result);
        result = get_band1(sample_bin_input);
        assertEquals("", result);
        result = get_band2(sample_bin_input);
        assertEquals("0101110", result);
    }
    
    @Test
    public void bands_test1() {
        Log2SubBand.parameters = new int[] {0,6,6,0};
        
        result = get_band0(sample_bin_input);
        assertEquals("", result);
        result = get_band1(sample_bin_input);
        assertEquals("010110", result);
        result = get_band2(sample_bin_input);
        assertEquals("101110", result);
    }
    
    @Test
    public void bands_test2() {
        Log2SubBand.parameters = new int[] {1,11,0,0};
        result = get_band0(sample_bin_input);
        assertEquals("0", result);
        result = get_band1(sample_bin_input);
        assertEquals("10110101110", result);
        result = get_band2(sample_bin_input);
        assertEquals("", result);
    }

    @Test
    public void DPCM_test1() {
        String[] input = {"5","6","4","3","6","8","1"};
        String[] expected = {"5","1","-2","-1","3","2","-7"};
        String[] reslt = CompressionUtils.DPCM(input);
        assertArrayEquals(expected, reslt);
    }

    @Test
    public void DPCM_test2() {
        String[] input = {"1","3","5","7","9","7","5","-2"};
        String[] expected = {"1","2","2","2","2","-2","-2","-7"};
        String[] reslt = CompressionUtils.DPCM(input);
        assertArrayEquals(expected, reslt);
    }
}
