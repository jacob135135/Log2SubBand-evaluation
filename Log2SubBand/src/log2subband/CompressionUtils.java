package log2subband;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import static log2subband.HuffmanCode.number_to_encoding_dict;
import static log2subband.MainExecution.debug;
import static log2subband.Log2SubBand.parameters;
import static log2subband.MyUtils.binary_to_12_bits;
import static log2subband.MyUtils.binary_to_decimal;
import static log2subband.MyUtils.decimal_to_binary;

/**
 * @author JAKUB
 */
public class CompressionUtils {
    
        /**
     * After <code>init_ideal_huffman_dictionaries(String string_to_encode) </code> is run,
       this method can be used to return Huffman encoding of <code>to_encode</code>
       as a concatenation of Huffman encodings of all symbols (characters) in <code>to_encode</code>
       using <code>number_to_encoding_dict</code> (Mapping of numbers and their respective Huffman codes)
     * @param to_encode String to get Huffman encoding of
     * @return Concatenation of Huffman encodings of all symbols (characters) in <code>to_encode</code>
     */
    public static String get_huffman_encoding(String to_encode) {
        if(debug) System.out.println("INITIAL TO ENCODE: " + to_encode);
        to_encode = binary_to_decimal(binary_to_12_bits(to_encode));//binary_to_decimal(String.valueOf(transformed));
        if(debug) System.out.println("DECIMAL TO ENCODE: " + to_encode);
        to_encode = String.valueOf(Integer.valueOf(to_encode) + HuffmanCode.HUFFMAN_ADDITION);
        if(debug) System.out.println("TRANSFORMED TO ENCODE: " + to_encode);
        String result = number_to_encoding_dict.get(to_encode);
        if(debug) System.out.println("ENCODED INTO :" + result);
        if (result == null) throw new NoSuchElementException("Codebook ERROR, no encoding found for '" + to_encode + "'");
        return result;
    }
    
        /**
     * Prints compression results
     * @param input_string original string to encode
     * @param overall_compressed full compressed string [1,0]*
     * @param bin_concat_input original string compressed and then decompressed (ideally same as input string)
     */
    public static void print_log2subband_results(String input_string, String overall_compressed, String bin_concat_input) {
        System.out.println("Input:   " + input_string);
        System.out.println("Compressed data:   " + overall_compressed);
        System.out.println("Total compressed length = " + overall_compressed.length());
        System.out.println("Original binary concatenated input data: " + bin_concat_input);
        System.out.println("Total binary concatenated input data length = " + bin_concat_input.length());
        double compression_rate = compression_rate(overall_compressed, bin_concat_input);
        System.out.println("Log2SubBand Original/Compressed: " + compression_rate);
        System.out.println("Decompressed data: " + Log2SubBand.log2_sub_band_decode_string(overall_compressed));
    }
    
    /**
     * Compresses input array of numbers, saving data through the process
     * @param raw_values String[] of numbers to compress
     * @param is_binary Boolean Set to true if input numbers are in binary
     * @return Map<String, String> to_return  (almost like an associative array), to get values:
     *  <br><b>to_return.get("compr");</b> Binary concatenated string of all compressed values in given array
        <br><b>to_return.get("bin_concat_input");</b> String concatenated binary input numbers (without commas)
        <br><b>to_return.get("cs_input");</b> Comma separated String of values in input
        <br><b>to_return.get("cs_output");</b> Comma separated String of compressed values (i.e. overall_compressed with commas in between)
     */
    public static Map<String, String> perform_log2_sub_band(String[] raw_values, boolean is_binary) {
        String ovrl_compr, bin_concat_input, cs_input, cs_output;
        ovrl_compr = bin_concat_input = cs_input = cs_output = "";

        for (String raw_value : raw_values) {
            if(!is_binary) {raw_value = decimal_to_binary(raw_value);}
            else {raw_value = MyUtils.binary_to_12_bits(raw_value);}
            cs_input += "," + raw_value;

            String current_compressed = Log2SubBand.log2_sub_band_compress_number(raw_value);
            ovrl_compr += current_compressed;
            cs_output += "," + current_compressed;
            if (debug) System.out.println("Current compressed data: " + current_compressed);
            bin_concat_input += raw_value;
        }

        Map<String, String> to_return = new HashMap<>();
        to_return.put("compr", ovrl_compr);
        to_return.put("bin_concat_input", bin_concat_input);
        to_return.put("cs_input", cs_input.substring(1));
        to_return.put("cs_output", cs_output.substring(1));

        return to_return;
    }
    
      /**
     * Given parsed codebook data, escapes first 2 values (assumed to be column names). and assuming that
     * <br>Every alternating number (1st, 3rd, 5th...) is number for codebook
     * <br>Every alternating number (2nd, 4th, 6th...) is encoding for codebook
     * <br>Creates mapping of number => encoding and initialises <code>number_to_encoding_dict</code>
     * @param codebook_imported_data Parsed codebook data (from csv file)
     */
    public static void init_codebook_from_imported_codebook (String[] codebook_imported_data) {
        for (int i = 2; i < codebook_imported_data.length; i+=2) {
            number_to_encoding_dict.put(codebook_imported_data[i], codebook_imported_data[i+1]);
        }
    }
    
    public static Double compression_rate(String overall_compressed, String bin_concat_input) {
        return Math.round(100.0 * bin_concat_input.length()/overall_compressed.length())/100.0;
    }
    
    /**
     * Prints information regarding Huffman compression results.
     * Prints Full compressed string and ratio (with respect to original binary concatenated input string)
     * @param cs_input
     * @param bin_concat_input
     */
    static void print_Huffman_compression_results(String cs_input, String bin_concat_input) {
        String[] input_array = cs_input.split(",");
        String compressed = get_full_huffman_encoding(input_array);
        double compression_rate = compression_rate(compressed, bin_concat_input);
        System.out.println("Huffman compressed: " + compressed);
        System.out.println("Huffman Original/Compressed: " + compression_rate);
    }
    
        /**
     * This function is used to make differences in frequencies significant.
     * It greatly increases frequency of most frequent ones.
     * It also adds (as last step) +1 to all frequencies so that every number has to be encoded
     * @param numbers_frequencies Integer array recording frequencies of numbers
     * @return numbers_frequencies Input array with differences between frequencies made more significant
     */
    public static int[] make_frequencies_significant(int[] numbers_frequencies) {
        for (int i=0 ; i<numbers_frequencies.length; i++) {
            int previous_value = numbers_frequencies[i];
            numbers_frequencies[i] = previous_value * HuffmanCode.FREQUENCY_SIGNIFICANCE_MULTIPLIER + 1;
        }
        return numbers_frequencies;
    }

    /**
     * Uses <code>get_huffman_encoding</code> method and concatenates encoding of individual
     * elements in given array
     * @param to_encode String[] Array to encode using Huffman
     * @return
     */
    public static String get_full_huffman_encoding(String[] to_encode) {
        String encoded = "";
        for(String element : to_encode) {
            encoded += CompressionUtils.get_huffman_encoding(element);
        }
        return encoded;
    }

    /**
     * WARNING: returns "" if string is not long enough
     * Gets least significant band
     * Uses static <code>parameters</code> variable and return substring of input.
     * Returns last <code>parameters[2]</code> digits (represented as String) of input
     * @param binary_input String input of length 12
     * @return Last <code>parameters[2]</code> digits of input
     */
    static String get_band3(String binary_input) {
        if (binary_input.length() > 11) return binary_input.substring(parameters[0] + parameters[1] + parameters[2], 12);
        else {System.out.println("NO BAND3 FOUND"); return "";}
    }
    
     /**
     * WARNING: returns "" if string is not long enough
     * Gets least significant band
     * Uses static <code>parameters</code> variable and return substring of input.
     * Returns last <code>parameters[2]</code> digits (represented as String) of input
     * @param binary_input String input of length 12
     * @return Last <code>parameters[2]</code> digits of input
     */
    static String get_band2(String binary_input) {
        if (binary_input.length() > 11) return binary_input.substring(parameters[0] + parameters[1], 12 - parameters[3]);
        else {System.out.println("NO BAND2 FOUND"); return "";}
    }

    /**
     * WARNING: returns "" if string is not long enough
     * Uses static <code>parameters</code> variable and return substring of input.
     * Returns middle <code>parameters[1]</code> digits (represented as String) of input
     * @param binary_input String input of length 12
     * @return Input string without first <code>parameters[0]</code> digits and last <code>parameters[2]</code> digits of input
     */
    static String get_band1(String binary_input) {
        if (binary_input.length() > (11 - parameters[2])) return binary_input.substring(parameters[0], parameters[0] + parameters[1]);
        else {System.out.println("NO BAND1 FOUND"); return "";}
    }

    /**
     * ASSUMES input is of length 12
     * Uses static <code>parameters</code> variable and return substring of input.
     * Returns middle <code>parameters[1]</code> digits (represented as String) of input
     * @param binary_input String input of length 12
     * @return First <code>parameters[0]</code> digits of input string
     */
    static String get_band0(String binary_input) {
        return binary_input.substring(0, parameters[0]);
    }
    
}
