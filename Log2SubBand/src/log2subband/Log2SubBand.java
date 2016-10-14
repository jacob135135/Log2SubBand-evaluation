/*
 * Based on table on page 112.
 * Algorithm should (hopefully) be semantically same (work the same) as one on pages 141 & 142 in thesis
 *
 * Current implementation assumptions:
 * Range of numbers "allowed" is from 0 to 999 (inclusive)
 * Result will have 12 bits -> [4,4,4]
 */
package log2subband;

import java.util.Map;
import static log2subband.HuffmanCode.number_to_encoding_dict;
import static log2subband.MyUtils.dec_to_bin_nibble;
import static log2subband.MyUtils.bin_nibble_to_dec;

/**
 * @author Jakub
 */
public class Log2SubBand {
        
    // position 0 is most signifficant bit, position n is least signifficant bit
    // static so that they can be referenced and changed in get_compressed_data method 
    static int previous_digit_pos2 = 0;
    static int previous_digit_pos1 = 0; 
    static int previous_digit_pos0 = 0;
    static boolean debug = false;
    
    /**
     * Takes a decimal number in string representation (e.g. "10") and returns compressed binary version based
       on real time-data (same number will be encoded differently in various inputs)
     * Compression is dynamic using static integers (e.g. previous_digit_pos0)
     * Basic algorithm: Check if digit_pos0 (most significant bit) bit is different from previous_digit_pos0
        if so copy all nibbles (nibble in binary = digit in decimal) and append after header, header = 11;
        else if middle nibble changed -> encode middle & least significant bit, header = 10;
        else if least significant bit changed -> encode least significant bit, header = 01;
        else send header = 00 (This means number is same as the previous number)
     * @param input_number String 
     * @return String compressed binary number as string (e.g. "11000100010001") this is to simplify operations on the "number"
     */
    public static String log2_sub_band_compress_number(String input_number) {    
        char digit_pos2 = input_number.charAt(2);
        char digit_pos1 = input_number.charAt(1);
        char digit_pos0 = input_number.charAt(0);

        if (debug) System.out.println("Digits(decimal): " + digit_pos0 + " " + digit_pos1 + " " + digit_pos2);
   
        String return_value;
        if (previous_digit_pos0 == digit_pos0) {
            if (previous_digit_pos1 == digit_pos1){
                if (previous_digit_pos2 == digit_pos2) {
                    return_value = "00";
                }
                else {return_value = "01" + dec_to_bin_nibble(digit_pos2);}
            } else {return_value = "10" + dec_to_bin_nibble(digit_pos1) + dec_to_bin_nibble(digit_pos2);}
        } else { return_value = "11" + dec_to_bin_nibble(digit_pos0) + dec_to_bin_nibble(digit_pos1) + dec_to_bin_nibble(digit_pos2);}
        previous_digit_pos0 = digit_pos0;
        previous_digit_pos1 = digit_pos1;
        previous_digit_pos2 = digit_pos2;
        return return_value;
    }
          
    /**
     * Uses Log2SubBand algorithm for decoding.
     * Decodes String of zeroes and ones into comma separated String of numbers
     * @param encoded String to decode
     * @return Decoded numbers separated by comma
     */
    public static String log2_sub_band_decode_string(String encoded) {
        String remaining_string = encoded;
        String current_number = "";
        String decoded_string = "";
        String[] results;
        
        while (!remaining_string.isEmpty()) {
            if(debug) System.out.print("remaining: " + remaining_string + "(" + remaining_string.length() + ")\n");
            results = decode_substring(remaining_string, current_number);
            current_number = results[0];
            decoded_string += "," + current_number;
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
     * @param current_number Last/currently decoded number
     * @return Array of [decoded_number (currently decoded number), remaining_string (String still left to decode)]
     */
    public static String[] decode_substring(String encoded_substring, String current_number) {
        String decoded_number = current_number;
        String header = encoded_substring.substring(0,2);
        if (debug) System.out.print("Header: " + header + "\n");
        String remaining_string = encoded_substring.substring(2); // Removing header from string
        
        switch (header) {
            case "00":  decoded_number = current_number;
                break;
            case "01":  decoded_number = current_number.substring(0,2) + bin_nibble_to_dec(remaining_string.substring(0,4));
                        remaining_string = remaining_string.substring(4);
                break;
            case "10":  decoded_number = current_number.substring(0,1) + bin_nibble_to_dec(remaining_string.substring(0,4))
                                       + bin_nibble_to_dec(remaining_string.substring(4,8));
                        remaining_string = remaining_string.substring(8);
                break;
            case "11":  decoded_number = bin_nibble_to_dec(remaining_string.substring(0,4))
                                       + bin_nibble_to_dec(remaining_string.substring(4,8))
                                       + bin_nibble_to_dec(remaining_string.substring(8,12));
                        remaining_string = remaining_string.substring(12);
                break;
        }
        if (debug) System.out.println("Current decoded: " + decoded_number + "\n");
        String stuff_to_return[] = {decoded_number,remaining_string};
        return stuff_to_return;
    }

    public static void main(String[] args) throws Exception {
        String[] raw_values = MyUtils.get_data_from_user();
        Map<String, String> result = MyUtils.perform_log2_sub_band_compression(raw_values);
        String overall_compressed = result.get("overall_compressed");
        String overall_uncompressed = result.get("overall_uncompressed");
        String input_string = result.get("input");
        String output_string = result.get("output");

        MyUtils.print_compression_results(input_string, overall_compressed, overall_uncompressed);
        HuffmanCode.huffman_best_compression(raw_values); // Does not return data but assigns to number_to_encoding_dict

        String[] input_array = input_string.split(",");
        String[] output_array = output_string.split(",");
        String[] binary_input = MyUtils.split_by(overall_uncompressed,12);
        String[] export_data = MyUtils.make_export_table(input_array, output_array, binary_input, number_to_encoding_dict);
        MyUtils.write_CSV("compressed", export_data);
        MyUtils.open_file("compressed.csv");
    }
}
