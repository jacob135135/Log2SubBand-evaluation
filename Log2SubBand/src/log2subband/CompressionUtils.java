package log2subband;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import static log2subband.HuffmanCode.number_to_encoding_dict;
import static log2subband.HuffmanCode.encoding_to_number_dict;
import static log2subband.MainExecution.debug;
import static log2subband.MainExecution.is_bin_system;
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
        //if(debug) System.out.println("INITIAL TO ENCODE: " + to_encode);
        to_encode = binary_to_decimal(binary_to_12_bits(to_encode));//binary_to_decimal(String.valueOf(transformed));
        //if(debug) System.out.println("DECIMAL TO ENCODE: " + to_encode);
        to_encode = String.valueOf(Integer.valueOf(to_encode) + HuffmanCode.HUFFMAN_ADDITION);
        //if(debug) System.out.println("TRANSFORMED TO ENCODE: " + to_encode);
        String result = number_to_encoding_dict.get(to_encode);
        //if(debug) System.out.println("ENCODED INTO :" + result);
        if (result == null) throw new NoSuchElementException("Codebook ERROR, no encoding found for '" + to_encode + "'");
        return result;
    }
    
        /**
     * Prints compression results
     * @param input_string original string to encode
     * @param overall_compressed full compressed string [1,0]*
     * @param bin_concat_input original string compressed and then decompressed (ideally same as input string)
     */
    public static double get_log2subband_CR(String input_string, String overall_compressed, String bin_concat_input) {
        if(debug) System.out.println("Input:   " + input_string);
        if(debug) System.out.println("Compressed data:   " + overall_compressed);
        if(debug) System.out.println("Total compressed length = " + overall_compressed.length());
        if(debug) System.out.println("Original binary concatenated input data: " + bin_concat_input);
        if(debug) System.out.println("Total binary concatenated input data length = " + bin_concat_input.length());
        double compression_rate = compression_rate(overall_compressed, bin_concat_input);
        if(debug) System.out.println("Log2SubBand Original/Compressed: " + compression_rate);
        //if(debug) System.out.println("Decompressed data: " + Log2SubBand.log2_sub_band_decode_string(overall_compressed));
        return compression_rate;
    }
    
    /**
     * Compresses input array of numbers, saving data through the process
     * @param raw_values String[] of numbers to compress
     * @return Map<String, String> to_return  (almost like an associative array), to get values:
     *  <br><b>to_return.get("compr");</b> Binary concatenated string of all compressed values in given array
        <br><b>to_return.get("bin_concat_input");</b> String concatenated binary input numbers (without commas)
        <br><b>to_return.get("cs_input");</b> Comma separated String of values in input
        <br><b>to_return.get("cs_output");</b> Comma separated String of compressed values (i.e. overall_compressed with commas in between)
     */
    public static Map<String, String> perform_log2_sub_band(String[] raw_values, boolean data_ready) {
        System.out.println("Starting performing log2SubBand: (" + LocalDateTime.now() + ")" );
        String ovrl_compr = "",cs_output = "";

        int total_to_encode = raw_values.length;
        int index = 0;
        int percentage = 0;
        for (String raw_value : raw_values) {
            raw_value = Log2SubBand.prepend_zeroes_if_needed(raw_value);
            String current_compressed = Log2SubBand.get_compressed_data(raw_value);
            ovrl_compr += current_compressed;
            cs_output += "," + current_compressed;
            if (debug) System.out.println("Current compressed data: " + current_compressed);
            index++;
            if (total_to_encode > 100 && index%(total_to_encode/100) == 0) {
                System.out.println("Approx " + percentage + "% complete");
                percentage++;
            }
        }

        Map<String, String> to_return = new HashMap<>();
        to_return.put("compr", ovrl_compr);
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
        System.out.println("overall compr length: " + overall_compressed.length());
        System.out.println("bin_concat_input length: " + bin_concat_input.length());
        double compr_ratio =  (1.0 * bin_concat_input.length())/ overall_compressed.length();
        return compr_ratio;
    }
    
    /**
     * Prints information regarding Huffman compression results.
     * Prints Full compressed string and ratio (with respect to original binary concatenated input string)
     * @param cs_input
     * @param bin_concat_input
     */
    static double get_Huffman_CR(String cs_input, String bin_concat_input) {
        String[] input_array;
        if (MainExecution.DPCM_for_Huffman) {
            input_array = HuffmanCode.huffman_DPCM_data;
        } else {
           input_array = cs_input.split(",");
        }

        String compressed = get_full_huffman_encoding(input_array);
        double compression_rate = compression_rate(compressed, bin_concat_input);
        if(debug) System.out.println("Huffman compressed: " + compressed);
        if(debug) System.out.println("Huffman Original/Compressed: " + compression_rate);

        return compression_rate;
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
        int total_to_encode = to_encode.length;
        int index = 0;
        int percentage = 0;
        for(String element : to_encode) {
            encoded += CompressionUtils.get_huffman_encoding(element);
            index++;
            if (total_to_encode > 100 && index%(total_to_encode/100) == 0) {
                System.out.println("Approx " + percentage + "% complete");
                percentage++;
            }
        }
        return encoded;
    }

    static Map<String, String> GetDataInfo(String[] raw_values) {
        String bin_concat_input = "", cs_input = "";

        int i = 0;
        for (String raw_value : raw_values) {
            if (i%4098 == 0) {
                // Assuming files have 4097 numbers in them
                System.out.println("Got data info from file" + (i/4098 + 1) +" : (" + LocalDateTime.now() + ")" );
            }
            if(!is_bin_system) {raw_value = decimal_to_binary(raw_value);}
            else {raw_value = MyUtils.binary_to_12_bits(raw_value);}
            cs_input += "," + raw_value;
            bin_concat_input += raw_value;
            i++;
        }

        Map<String, String> to_return = new HashMap<>();
        to_return.put("bin_concat_input", bin_concat_input);
        to_return.put("cs_input", cs_input.substring(1));

        return to_return;
    }

    /**
     * Initialises codebook for Huffman. If codebook is not imported, optimal codebook is created
     * @param custom_codebook Codebook provided by user, possibly empty
     * @param raw_values_array Array with input data
     * @throws Exception
     */
    static void set_up_Huffman(String[] custom_codebook , String[] raw_values_array) throws Exception {
        number_to_encoding_dict = new HashMap<>();
        encoding_to_number_dict = new HashMap<>();
        if (custom_codebook.length > 0) CompressionUtils.init_codebook_from_imported_codebook(custom_codebook );
        else {
            System.out.println("Starting to initialise ideal huffman dict from data: (" + LocalDateTime.now() + ")" );
            HuffmanCode.init_ideal_huffman_dictionaries(raw_values_array);
            System.out.println("Ideal Huff dictionary : (" + LocalDateTime.now() + ")" );
        }
    }

    public static String[] DPCM(String[] input) {
        String[] to_return = new String[input.length];
        to_return[0] = input[0];
        for(int i=1;i<input.length;i++) {
            int temp = Integer.parseInt(input[i]) - Integer.parseInt(input[i-1]);
            to_return[i] = String.valueOf(temp);
        }
        return to_return;
    }

}
