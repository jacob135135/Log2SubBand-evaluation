/*
 * Based on table on page 112.
 * Algorithm should (hopefully) be semantically same (work the same) as one on pages 141 & 142 in thesis
 *
 * Current implementation assumptions:
 * Range of numbers "allowed" is from -2048 to 2047 (inclusive)
 * Result will have 12 bits
 */
package log2subband;

import static log2subband.MainExecution.debug;
import static log2subband.CompressionUtils.get_band0;
import static log2subband.CompressionUtils.get_band1;
import static log2subband.CompressionUtils.get_band2;
import static log2subband.CompressionUtils.get_band3;

/**
 * @author Jakub
 */
public class Log2SubBand {
        
    // static so that they can be referenced and changed in get_compressed_data method 
    static String previous_band0;
    static String previous_band1;
    static String previous_band2;
    static String previous_band3;
    static int[] parameters;
    
    /**
     * Takes a BINARY number in string representation (e.g. "10") and returns compressed binary version based
       on real time-data (same number will be encoded differently in various inputs)
       Compression is dynamic using static integers (e.g. previous_band0)
       Basic algorithm: Check if band0 (most significant bit) bit is different from previous_band0
        if so copy all bands (band in binary = digit in decimal) and append after header, header = 11;
        else if middle band changed -> encode middle & least significant bit, header = 10;
        else if least significant bit changed -> encode least significant bit, header = 01;
        else send header = 00 (This means number is same as the previous number)
     * @param binary_input String binary number
     * @return String compressed binary number as string (e.g. "11000100010001") this is to simplify operations on the "number"
     */
    public static String log2_sub_band_compress_number(String binary_input) {
        String band0 = get_band0(binary_input);
        String band1 = get_band1(binary_input);
        String band2 = get_band2(binary_input);
        String band3 = get_band3(binary_input);

        if (debug) System.out.println("Bands: " + band0 + " " + band1 + " " + band2 + " " + band3);
   
        String return_value;
        if (band0.equals(previous_band0)) {
            if (band1.equals(previous_band1)){
                if (band2.equals(previous_band2)) {
                    return_value = "00";
                    if (parameters[3] != 0) return_value += band3;
                }
                else {
                    return_value = "01" + band2;
                    if (parameters[3] != 0) return_value += band3;
                }
            } else {
                return_value = "10" + band1 + band2;
                if (parameters[3] != 0) return_value += band3;
            }
        } else {
            return_value = "11" + band0 + band1 + band2;
            if (parameters[3] != 0) return_value += band3;
        }
        previous_band0 = band0;
        previous_band1 = band1;
        previous_band2 = band2;
        return return_value;
    }
          
    /**
     * Uses Log2SubBand algorithm for decoding.
     * Decodes String of zeroes and ones into comma separated String of numbers
     * @param encoded String to decode
     * @return Decoded decimal numbers separated by comma
     */
    public static String log2_sub_band_decode_string(String encoded) {
        String remaining_string = encoded;
        String current_number = MyUtils.generate_zeroes(12);
        String decoded_string = "";
        String[] results;
        
        while (!remaining_string.isEmpty()) {
            if(debug) System.out.print("remaining: " + remaining_string + "(" + remaining_string.length() + ")\n");
            results = decode_substring(remaining_string, current_number);
            current_number = results[0];
            decoded_string += "," + MyUtils.binary_to_decimal(current_number);
            remaining_string = results[1];
        }
        decoded_string = decoded_string.substring(1); // Remove trailing comma
        return decoded_string;
    }
    
    /**
     * Takes in remaining string to decode and last number that was decoded. Based on header 
       binary code then reads relevant number of following binary numbers (0 to 12).
     * Resulting string to decode is then returned as well as number decoded using header code
       and appropriate number of bits from string to decode
     * @param encoded_substring Substring of initial String to decode   
     * @param previous_number Last/currently decoded number
     * @return Array of [decoded_number (currently decoded number), remaining_string (String still left to decode)]
     */
    public static String[] decode_substring(String encoded_substring, String previous_number) {
        String decoded_number = previous_number;
        int band0_bits = parameters[0];
        int band1_bits = parameters[1];
        int band2_bits = parameters[2];
        int band3_bits = parameters[3];

        String header = encoded_substring.substring(0,2);
        if (debug) System.out.print("Header: " + header + "\n");
        String remaining_string = encoded_substring.substring(2); // Removing header from string

        if(debug)System.out.println("PREV NUMBER " + previous_number);
        if(band3_bits == 0) {
            switch (header) {
                case "00":  decoded_number = previous_number;
                    break;
                case "01":  decoded_number = get_band0(previous_number) + get_band1(previous_number) + remaining_string.substring(0, parameters[2]);
                            remaining_string = remaining_string.substring(band2_bits); // Exclude bits that were encoding
                    break;
                case "10":  decoded_number = get_band0(previous_number) + remaining_string.substring(0, 12 - parameters[0]);
                            remaining_string = remaining_string.substring(band2_bits + band1_bits); // Exclude bits that were encoding
                    break;
                case "11":  decoded_number = remaining_string.substring(0,12);
                            remaining_string = remaining_string.substring(12); // Exclude bits that were encoding
                    break;
            }
        } else {
            switch (header) {
                case "00":  decoded_number = previous_number.substring(0, 12 - band3_bits) + remaining_string.substring(0,band3_bits);
                            remaining_string = remaining_string.substring(band3_bits);
                    break;
                case "01":  decoded_number = get_band0(previous_number) + get_band1(previous_number) +
                                             remaining_string.substring(0, 12 - band2_bits - band3_bits);
                                             remaining_string = remaining_string.substring(12 - band2_bits - band3_bits);
                    break;
                case "10":  decoded_number = get_band0(previous_number) + remaining_string.substring(0, 12 - band0_bits);
                            remaining_string = remaining_string.substring(12-band0_bits); // Exclude bits that were encoding
                    break;
                case "11":  decoded_number = remaining_string.substring(0,12);
                            remaining_string = remaining_string.substring(12); // Exclude bits that were encoding
                    break;
            }
        }
        if (debug) System.out.println("Current decoded: " + decoded_number + "\n");
        String stuff_to_return[] = {decoded_number,remaining_string};
        return stuff_to_return;
    }

    public static void update_previous_bands(int[] parameters) {
        Log2SubBand.previous_band0 = MyUtils.generate_zeroes(parameters[0]);
        Log2SubBand.previous_band1 = MyUtils.generate_zeroes(parameters[1]);
        Log2SubBand.previous_band2 = MyUtils.generate_zeroes(parameters[2]);
        Log2SubBand.previous_band3 = MyUtils.generate_zeroes(parameters[3]);
    }

}
