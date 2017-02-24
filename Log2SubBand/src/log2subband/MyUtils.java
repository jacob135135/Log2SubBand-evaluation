/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import java.awt.Desktop;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import menuUI.InputMenu;

/**
 *
 * @author Jakub
 */
public class MyUtils {
        
    /**
     * Splits String composed of values separated by comma into array, removing all spaces
     * @param str_input Comma separated string of values
     * e.g. "12, 21, 12, 43 " => ["12", "21", "12", "43"]
     * @return Array obtained by separating commas in string and removing extra spaces
     */
    public static String[] CSstring_to_array(String str_input) {
        return str_input.split(",[ ]*");
    }

    /**
     * Launches a file given its path
     * If file in current directory, filename is sufficient
     * @param filepath
     */
    public static void open_file(String filepath) {
    File f = new File(filepath);
        try {Desktop.getDesktop().open(f);} catch (IOException e) {System.out.println("ERROR, Can't open file.");}
    }

    /**
     * Creates CSV table outlining original and encoded values of strings using
       <code>original</code> and <code>encoded</code>
     * <br> Uses <code>number_to_encoding_dict</code> as mapping of numbers and their respective Huffman codes
     * @param cs_original Comma separated original data
     * @param cs_encoded Comma separated encoded data
     * @return String[] of CSV table data
     */
    public static String[] make_single_param_export_table(String cs_original, String cs_encoded, double huff_compr_rate, double subband_cr) {
        String[] orig = cs_original.split(",");
        String[] encod = cs_encoded.split(",");
        String result_string = "";
        if (InputMenu.export_all_encoding_info) {
            result_string = "Original(dec)," + append_spaces("Original(bin),", 14) + append_spaces("Encoded", 14) + ",Huffman*";
            for (int i=0; i<orig.length; i++) {
                String crap = binary_to_decimal(orig[i]);
                result_string += "\n" + append_spaces(crap, 13) + "," + append_spaces(orig[i], 13) + "," + append_spaces(encod[i],14);
                result_string += "," + CompressionUtils.get_huffman_encoding(orig[i]);
            }
        }
        int total_headers = Log2SubBand.header00_count + Log2SubBand.header01_count + Log2SubBand.header10_count + Log2SubBand.header11_count;

        String huf_mode = "Huffman CR: ";
        if (MainExecution.DPCM_for_Huffman) huf_mode = "DPCM + Huffman CR: ";
        result_string += "\n\n Log2SubBand CR: " + subband_cr  + "\n " + huf_mode + huff_compr_rate;
        result_string += "\n\n " + MainExecution.running_setting;

        if (!"".equals(InputMenu.codebook_filename)) {
            result_string += "\n Codebook imported from " + InputMenu.codebook_filename;
        }

        result_string += "\n\n Data files imported: " + MainExecution.data_files;
        result_string += "\n\nHEADER 00 count: " + Log2SubBand.header00_count + "(" + Log2SubBand.header00_count*(100.0)/total_headers + "%)";
        result_string += "\nHEADER 01 count: " + Log2SubBand.header01_count + "(" + Log2SubBand.header01_count*(100.0)/total_headers + "%)";
        result_string += "\nHEADER 10 count: " + Log2SubBand.header10_count + "(" + Log2SubBand.header10_count*(100.0)/total_headers + "%)";
        result_string += "\nHEADER 11 count: " + Log2SubBand.header11_count + "(" + Log2SubBand.header11_count*(100.0)/total_headers + "%)";
        String[] result = result_string.split(",");
        return result;
    }

    /**
     * Creates CSV table outlining Log2subband compression rate for every parameter + Huffman compression rate
       <code>original</code> and <code>encoded</code>
     * <br> Uses <code>number_to_encoding_dict</code> as mapping of numbers and their respective Huffman codes
     * @param permutations String[]  Array containing all permutations. Individual bands separated by '
     * @param CRs String[] of compression rates for Log2subband parameters
     * @param huff_compr_rate double Huffman compression rate for input array
     * @return String[] of CSV table data
     */
    public static String[] make_all_permutations_export_table(String[] permutations, String[] CRs, double huff_compr_rate) {
        String result_string = "Permutation," + append_spaces("Compression rate,", 14) + "Huffman rate";
        for (int i=0; i<permutations.length; i++) {
            result_string += "\n" + append_spaces(permutations[i], 13) + "," + append_spaces(CRs[i], 13) + "," + huff_compr_rate;
        }

        if (!"".equals(InputMenu.codebook_filename)) {
            result_string += "\n\n Codebook imported from " + InputMenu.codebook_filename;
        }

        result_string += "\n\n Data files imported: " + MainExecution.data_files;
        String[] result = result_string.split(",");
        return result;
    }

    /**
     * Formatting method that appends spaces to <code>input</code> to achieve
       desired length of <code>input</code>
     * Only works when <code>desired_length > input</code>
     * @param input original string
     * @param desired_length length of desired string
     * @return Original string with appended spaces based on original and desired length
     */
    public static String append_spaces(String input, int desired_length) {
        return String.format("%" + (-desired_length) + "s", input);
    }

    /**
     * Splits <code>input</code> string into substrings of length <code>length</code>
     * @param input
     * @param length
     * @return String[] of substrings of original string
     */
    public static String[] split_by_length (String input, int length) {
        return input.split("(?<=\\G.{" + length + "})"); //http://stackoverflow.com/questions/3760152/split-string-to-equal-length-substrings-in-java
    }

    /**
     * Converts a number in decimal into SIGNED binary number
     *
     * @param decimal_n String decimal number
     * @return SIGNED binary number corresponding to decimal input
     */
    static String decimal_to_binary(String decimal_n) {
        Integer dec_int = Integer.valueOf(decimal_n);
        String binary_repr = Integer.toBinaryString(dec_int);
        if (binary_repr.length()>12) binary_repr = binary_repr.substring(binary_repr.length()-12,binary_repr.length());
        else binary_repr = MyUtils.binary_to_12_bits(binary_repr);
        return binary_repr;
    }

    /**
     * Converts SIGNED binary number into its equivalent decimal number
     * @param binary_n String representation of binary number
     * @return Decimal value of input string
     */
    static String binary_to_decimal(String binary_n) {
        String first_bit = binary_n.substring(0,1); // "1" means number is negative
        Integer decimal_val = Integer.parseInt(binary_n, 2);
        if ("1".equals(first_bit)) {
            decimal_val -= 4096;
        }
        return String.valueOf(decimal_val);
    }

    /**
     * Prepends spaces (if needed) to <code>binary_input</code> to achieve length of 12 bits
     * If input has 12 bit, no action performed
     * @param binary_input original binary string
     * @return Original string with appended spaces (if applicable)
     */
    public static String binary_to_12_bits(String binary_input) {
        if (binary_input.length() < 12) {
            binary_input = generate_zeroes(12 - binary_input.length()) + binary_input;
        }
        return binary_input;
    }

    /**
     * Adds a constant to every element of array.
     * Can be used to add/subtract a constant value from all elements of the array
     * @param input_array Input array
     * @param constant Number to sum with each individual element of <code>input_array</code>
     * @return <code>input_array</code> with every element summed by <code>constant</code>
     */
    public static String[] add_to_string_array(String[] input_array, int constant) {
        for(int i=0; i< input_array.length;i++){
            int prev_val = Integer.valueOf(input_array[i]);
            int new_val = prev_val + constant;
            input_array[i] = String.valueOf(new_val);
        }
        return input_array;
    }

    /**
     * Produces number of zeroes based on input
     * @param total_number number of zeroes to output
     * @return <code>total_number</code> of zeroes as String
     */
    public static String generate_zeroes(int total_number) {
        String to_return = new String(new char[total_number]).replace("\0", "0");
        return to_return;
    }

    /**
     * Converts binary array into decimal array. All numbers as strings
     * @param input_arr Input binary array
     * @return Decimal array from input array
     */
    public static String[] bin_array_to_dec_array(String[] input_arr) {
        for (int i=0; i<input_arr.length; i++) {
            input_arr[i] = binary_to_decimal(binary_to_12_bits(input_arr[i]));
        }
        return input_arr;
    }

    // http://stackoverflow.com/questions/1384947/java-find-txt-files-in-specified-folder
    public static File[] get_appropriate_files_in_same_folder( File input_file){ // excludes files starting with codebook
        File dir = new File(input_file.getParent());

        return dir.listFiles(new FilenameFilter() {
                 public boolean accept(File dir, String filename)
                      { return filename.endsWith(".csv") & !filename.startsWith("codebook") & !filename.startsWith("stats"); }
        } );

    }

    // http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
    public static <String> String[] concat(String[] first, String[] second) {
        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    static String[] get_all_files_data(File input_file) {
        String[] to_return = {};
        MainExecution.data_files = "";
        File[] appropriate_files = get_appropriate_files_in_same_folder(input_file);
        for (File f : appropriate_files) {
            MainExecution.data_files += f.getName() + ", ";
            System.out.println("File " + f.getName() + " successfully loaded");
            String[] raw_values_array = CSVUtils.parse_CSV(f.getAbsolutePath());
            to_return = concat(to_return, raw_values_array);
        }
        MainExecution.data_files = MainExecution.data_files.substring(0, MainExecution.data_files.length() -2);
        System.out.println("Data files: " + MainExecution.data_files);

        return to_return;
    }

    /**
     * Goes through input array and checks for values that are outside of 12 bit signed int range
     * Numbers outside of range get saturated values
     *
     * @param raw_input_array input array
     * @return input array with values out of range replaced by saturated values (2047 or -2048)
     */
    public static String[] fit_data_into_boundary(String[] raw_input_array) {
        for(int i=0; i<raw_input_array.length; i++) {
            int temp = Integer.valueOf(raw_input_array[i]);
            if (temp > 2047) raw_input_array[i] = "2047";
            if (temp < -2048) raw_input_array[i] = "-2048";
        }
        return raw_input_array;
    }

}
