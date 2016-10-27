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
import static log2subband.MyUtils.get_LS_nibble;
import static log2subband.MyUtils.get_MS_nibble;
import static log2subband.MyUtils.get_middle_nibble;
import menuUI.InputMenu;

/**
 * @author Jakub
 */
public class Log2SubBand {
        
    // static so that they can be referenced and changed in get_compressed_data method 
    static String previous_least_significant_nibble;
    static String previous_middle_nibble;
    static String previous_most_significant_nibble;
    static int[] parameters;
    public static boolean debug;
    
    /**
     * Takes a BINARY number in string representation (e.g. "10") and returns compressed binary version based
       on real time-data (same number will be encoded differently in various inputs)
       Compression is dynamic using static integers (e.g. previous_most_significant_nibble)
       Basic algorithm: Check if most_significant_nibble (most significant bit) bit is different from previous_most_significant_nibble
        if so copy all nibbles (nibble in binary = digit in decimal) and append after header, header = 11;
        else if middle nibble changed -> encode middle & least significant bit, header = 10;
        else if least significant bit changed -> encode least significant bit, header = 01;
        else send header = 00 (This means number is same as the previous number)
     * @param binary_input String binary number
     * @return String compressed binary number as string (e.g. "11000100010001") this is to simplify operations on the "number"
     */
    public static String log2_sub_band_compress_number(String binary_input) {
        String least_significant_nibble = MyUtils.get_LS_nibble(binary_input);
        String middle_nibble = MyUtils.get_middle_nibble(binary_input);
        String most_significant_nibble = MyUtils.get_MS_nibble(binary_input);

        if (debug) System.out.println("Nibbles): " + most_significant_nibble + " " + middle_nibble + " " + least_significant_nibble);
   
        String return_value;
        if (previous_most_significant_nibble == most_significant_nibble) {
            if (previous_middle_nibble == middle_nibble){
                if (previous_least_significant_nibble == least_significant_nibble) {
                    return_value = "00";
                }
                else {return_value = "01" + least_significant_nibble;}
            } else {return_value = "10" + middle_nibble + least_significant_nibble;}
        } else { return_value = "11" + most_significant_nibble + middle_nibble + least_significant_nibble;}
        previous_most_significant_nibble = most_significant_nibble;
        previous_middle_nibble = middle_nibble;
        previous_least_significant_nibble = least_significant_nibble;
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
     * @param previous_number Last/currently decoded number
     * @return Array of [decoded_number (currently decoded number), remaining_string (String still left to decode)]
     */
    public static String[] decode_substring(String encoded_substring, String previous_number) {
        String decoded_number = previous_number;
        int middle_nibble_bits = parameters[1];
        int least_signif_nibble_bits = parameters[2];

        String header = encoded_substring.substring(0,2);
        if (debug) System.out.print("Header: " + header + "\n");
        String remaining_string = encoded_substring.substring(2); // Removing header from string
        
        switch (header) {
            case "00":  decoded_number = previous_number;
                break;
            case "01":  decoded_number = get_MS_nibble(previous_number) + get_middle_nibble(previous_number) + get_LS_nibble(remaining_string);
                        remaining_string = remaining_string.substring(least_signif_nibble_bits); // Exclude bits that were encoding
                break;
            case "10":  decoded_number = get_MS_nibble(previous_number) + get_middle_nibble(remaining_string) + get_LS_nibble(remaining_string);
                        remaining_string = remaining_string.substring(least_signif_nibble_bits + middle_nibble_bits); // Exclude bits that were encoding
                break;
            case "11":  decoded_number = get_MS_nibble(remaining_string) + get_middle_nibble(remaining_string) + get_LS_nibble(remaining_string);
                        remaining_string = remaining_string.substring(12); // Exclude bits that were encoding
                break;
        }
        if (debug) System.out.println("Current decoded: " + decoded_number + "\n");
        String stuff_to_return[] = {decoded_number,remaining_string};
        return stuff_to_return;
    }

    public static void main(String[] args) {
//        for (int i = -2047; i < 2048; i++) {
//            String binary_val = MyUtils.decimal_to_binary(String.valueOf(i));
//            System.out.println(i + ":  " + binary_val);
//            System.out.println("Back to dec: " + MyUtils.binary_to_decimal(binary_val));
//        }
        InputMenu input_menu = new InputMenu(); // ALL INPUT OBTAINED FROM THERE
    }
    
    public static void main_execution(InputMenu input_menu) throws Exception {
        String[] raw_values = input_menu.getInput_data();
        boolean open_exported = input_menu.getOpen_exported();
        parameters = new int[]{4,4,4};
        
        Map<String, String> result = MyUtils.perform_log2_sub_band_compression(raw_values);
        String overall_compressed = result.get("overall_compressed");
        String overall_uncompressed = result.get("overall_uncompressed");
        String input_string = result.get("input");
        String output_string = result.get("output");

        MyUtils.print_log2subband_compression_results(input_string, overall_compressed, overall_uncompressed);

        String[] custom_codebook = input_menu.getCodebook_data();
        if (custom_codebook.length > 0) {
            MyUtils.init_codebook_from_imported_codebook(custom_codebook);
        } else {
            HuffmanCode.init_ideal_huffman_dictionaries(raw_values);
        }

        String[] input_array = input_string.split(",");
        String[] output_array = output_string.split(",");
        String[] binary_input = MyUtils.split_by(overall_uncompressed,12);
        String[] export_data = MyUtils.make_export_table(input_array, output_array, binary_input);
        MyUtils.print_Huffman_compression_results(input_array, overall_uncompressed);

        MyUtils.export_codebook(); // uses number_to_encoding_dict
        MyUtils.write_CSV("compressed", export_data);
        
        if(open_exported) MyUtils.open_file("compressed.csv");
        System.exit(0);
    }
}
