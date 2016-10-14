/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static log2subband.HuffmanCode.number_to_encoding_dict;
import static log2subband.Log2SubBand.debug;

/**
 *
 * @author Jakub
 */
public class MyUtils {
    
    /**
     * Prompts the user to enter an input.
     * The input numbers are then separated by comma and extra spaces are removed
     * @return raw_values Array of integers given by user
     */
    public static String[] request_input() {
        String answer = JOptionPane.showInputDialog(null, "Type in numbers separated by comma.");
        String[] raw_values =  CSstring_to_array(answer);
        return raw_values;
    }
    
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
     * Converts number in character representation(e.g. '1') into binary String representation (e.g. "0001") 
     * Converted number has 4 digits and only supports positive numbers
     * @param nibble Character representation of number to convert to binary
     * @return String representation of binary number using 4 bits
     */
    public static String dec_to_bin_nibble(char nibble) {
        String result_string = "0000"; // case '0'
        switch (nibble) {
            case '1':  result_string = "0001"; break;
            case '2':  result_string = "0010"; break;
            case '3':  result_string = "0011"; break;
            case '4':  result_string = "0100"; break;
            case '5':  result_string = "0101"; break;
            case '6':  result_string = "0110"; break;
            case '7':  result_string = "0111"; break;
            case '8':  result_string = "1000"; break;
            case '9':  result_string = "1001"; break;
        }
        return result_string;
    }
    
    /**
     * Prepends values lower than 100 by up to two zeroes to make sure number
     * is in "3-digit number representation" (e.g. 3 -> 003)
     * @param raw_value number
     * @return 
     */
    public static String prepend_zeroes_if_needed(String raw_value) {
        if (raw_value.length() == 1) raw_value = "00" + raw_value;
        else if (raw_value.length() == 2) raw_value = "0" + raw_value;
        return raw_value;
    }
    
    /**
     * Takes in binary 4-digit number as a String and returns decimal digit in String type
     * !!! Works only for numbers inclusively between 0000 and 1001 in binary; or 0 and 9 both in decimal 
     * @param binary Four digit binary number (e.g. 0011)
     * @return String decimal number 
     */
    public static String bin_nibble_to_dec(String binary) {
        String decoded_digit = "";
        switch (binary) {
            case "0000":  decoded_digit = "0"; break;
            case "0001":  decoded_digit = "1"; break;
            case "0010":  decoded_digit = "2"; break;
            case "0011":  decoded_digit = "3"; break;
            case "0100":  decoded_digit = "4"; break;
            case "0101":  decoded_digit = "5"; break;
            case "0110":  decoded_digit = "6"; break;
            case "0111":  decoded_digit = "7"; break;
            case "1000":  decoded_digit = "8"; break;
            case "1001":  decoded_digit = "9"; break;
        }
        return decoded_digit;
    }
    
    /**
     * Provides UI element for user to select a file.
     * @return String absolute path of the file selected by user
     */
    public static String request_file() {
        JButton open_btn = new JButton();
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File("../test files"));
        fc.setDialogTitle("Please select a file");
        if (fc.showOpenDialog(open_btn) == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile().getAbsolutePath();
        else return "";
    }
    
    /**
     * Provides a panel with 2 options - manual data entry or importing a csv file
     * @return int 0 if user selected manual data entry; 1 when user selected import from a file 
     */
    public static int data_entry_option_prompt() {
        String[] options = new String[2];
        options[0] = "Manual entry";
        options[1] = "CSV file import";
        String message = "Would you like to enter data manually or import a csv file with data?";
        int dialog_result = JOptionPane.showOptionDialog(null,message,"Title", 0,JOptionPane.QUESTION_MESSAGE,null,options,null);  
        return dialog_result;
    }
    
    /**
     * Gets data from user. If user chose manual input, it will prompt for data.
     * Otherwise if user chose csv inport, it will prompt csv import and return parsed csv.
     * @return String array of user input
     */
    public static String[] get_data_from_user() {
        if(data_entry_option_prompt() == 0) return request_input();
        else return MyUtils.parse_CSV(request_file());
    }

    /**
     * Attempts to parse a CSV file.
     * !!! Currently no check if file is a valid csv file
     * @param file_path Path to a file to parse
     * @return String[] of data from a file
     */
    public static String[] parse_CSV (String file_path){
        CSVReader reader = null;
        try {reader = new CSVReader(new FileReader(file_path));}
        catch (FileNotFoundException ex) {Logger.getLogger(Log2SubBand.class.getName()).log(Level.SEVERE, null, ex);} // autogenerated
        String[] data_parsed = null;
        if (reader instanceof CSVReader) {
            String comma_sep_values = "";
            try {List<String[]> my_data = reader.readAll();
                try {
                    for (String[] line : my_data) {
                        for (String value : line) {
                            if (value.length() > 0) comma_sep_values += value + ",";
                        }
                    }
                    comma_sep_values = comma_sep_values.substring(0, comma_sep_values.length()-1); // remove last comma
                    data_parsed = CSstring_to_array(comma_sep_values);
                    
                    if(debug) System.out.println(comma_sep_values);
                } catch (IndexOutOfBoundsException e) {} // No info about size, need to try and read until we get too far         
            }
            catch (IOException ex) {Logger.getLogger(Log2SubBand.class.getName()).log(Level.SEVERE, null, ex);}  // autogenerated   
        }
        return data_parsed;
    }

   /**
    * Creates/overwrites a CSV file with name <code>filename</code>.csv and writes inputted data to it
    * Newlines are achieved by using \n.
    * WARNING Overwrites existing files !!!
    * @param filename Name of file to create/overwrite
    * @param data String array of data to write to file
    */
    public static void write_CSV(String filename, String[] data) {
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(filename + ".csv"), ',', CSVWriter.NO_ESCAPE_CHARACTER);
            writer.writeNext(data);
            writer.close();
        System.out.print("File successfully created\n");
        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print("Something went wrong. File not created\n");
        }
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
     * @param original array of original data
     * @param encoded array of encoded data
     * @param binary_input array of original data in binary
     * @return String[] of CSV table data
     */
    public static String[] make_export_table(String[] original, String[] encoded, String[] binary_input) {
        String result_string = "Original," + append_spaces("Encoded", 14) + append_spaces(",Binary", 14) + ",Huffman*";
        for (int i=0; i<original.length; i++) {
            result_string += "\n" + append_spaces(original[i], 8) + "," + append_spaces(encoded[i],14) + "," + binary_input[i];
            String orig_string = original[i];
            result_string += "," + get_huffman_encoding(orig_string);
        }
        String[] result = result_string.split(",");
        return result;
    }

    /**
     * After <code>huffman_best_compression(String string_to_encode) </code> is run,
       this method can be used to return Huffman encoding of <code>to_encode</code>
       as a concatenation of Huffman encodings of all symbols (characters) in <code>to_encode</code>
     * @param to_encode String to get Huffman encoding of
     * @return Concatenation of Huffman encodings of all symbols (characters) in <code>to_encode</code>
     */
    public static String get_huffman_encoding(String to_encode) {
        String result = number_to_encoding_dict.get(to_encode);
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
    public static String[] split_by (String input, int length) {
        return input.split("(?<=\\G.{" + length + "})"); //http://stackoverflow.com/questions/3760152/split-string-to-equal-length-substrings-in-java
    }

    public static Double compression_rate(String overall_compressed, String overall_uncompressed) {
        return Math.round(100.0 * overall_uncompressed.length()/overall_compressed.length())/100.0;
    }

    /**
     * Prints compression results
     * @param input_string original string to encode
     * @param overall_compressed full compressed string [1,0]*
     * @param overall_uncompressed original string compressed and then decompressed (ideally same as input string)
     */
    public static void print_compression_results(String input_string, String overall_compressed, String overall_uncompressed) {
        System.out.println("Input:   " + input_string);
        System.out.println("Compressed data:   " + overall_compressed);
        System.out.println("Total compressed length = " + overall_compressed.length());
        System.out.println("Uncompressed data: " + overall_uncompressed);
        System.out.println("Total uncompressed length = " + overall_uncompressed.length());
        double compression_rate = compression_rate(overall_compressed, overall_uncompressed);
        System.out.println("Original/Compressed: " + compression_rate);
        System.out.println("Decompressed data: " + Log2SubBand.log2_sub_band_decode_string(overall_compressed));
    }

    /**
     * Compressed inputted array of numbers, saving data through the process
     * @param raw_values String[] of numbers to compress
     * @return Map<String, String> to_return  (almost like an associative array), to get values:
     *  <br><b>to_return.get("overall_compressed");</b> Binary concatenated string of all compressed values in given array
        <br><b>to_return.get("overall_uncompressed");</b> Binary concatenated string of all compressed values in given array
        <br><b>to_return.get("input");</b> Comma separated String of values in inputted <code>raw_values</code>
        <br><b>to_return.get("output");</b> Comma separated String of compressed values (i.e. overall_compressed with commas in between)
     */
    public static Map<String, String> perform_log2_sub_band_compression(String[] raw_values) {
        String ovrl_compr, ovrl_uncompr, input, output;
        ovrl_compr = ovrl_uncompr = input = output = "";

        for (String raw_value : raw_values) {
                input += "," + raw_value;
                raw_value = prepend_zeroes_if_needed(raw_value);

                String current_compressed = Log2SubBand.log2_sub_band_compress_number(raw_value);
                ovrl_compr += current_compressed;
                output += "," + current_compressed;
                if (debug) System.out.println("Current compressed data: " + current_compressed);

                for(char c : raw_value.toCharArray()) ovrl_uncompr += dec_to_bin_nibble(c);
        }

        Map<String, String> to_return = new HashMap<>();
        to_return.put("overall_compressed", ovrl_compr);
        to_return.put("overall_uncompressed", ovrl_uncompr);
        to_return.put("input", input.substring(1));
        to_return.put("output", output.substring(1));

        return to_return;
    }
}
