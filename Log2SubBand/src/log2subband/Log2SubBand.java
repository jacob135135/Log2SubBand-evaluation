/*
 * Based on table on page 112.
 * Algorithm should (hopefully) be semantically same (work the same) as one on pages 141 & 142
 *
 * Current implementation assumptions:
 *  Range of numbers "allowed" is from 0 to 999 (inclusive)
 *  Result will have 12 bits -> [4,4,4]
 */
package log2subband;

import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane; // Needed for input box at the beginning

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
        if so encode all nibbles (nibble in binary = digit in decimal) and append after header, header = 11;
        else if middle nibble changed -> encode middle & least significant bit, header = 10;
        else if least significant bit changed -> encode least significant bit, header = 01;
        else send header = 00 (This means number is same as the previous number)
     * @param input_number String 
     * @return String compressed binary number as string (e.g. "11000100010001") this is to simplify operations on the "number"
     */
    public static String get_compressed_data(String input_number) {    
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
                else {return_value = "01" + decimal_to_binary(digit_pos2);}
            } else {return_value = "10" + decimal_to_binary(digit_pos1) + decimal_to_binary(digit_pos2);}
        } else { return_value = "11" + decimal_to_binary(digit_pos0) + decimal_to_binary(digit_pos1) + decimal_to_binary(digit_pos2);}
        previous_digit_pos0 = digit_pos0;
        previous_digit_pos1 = digit_pos1;
        previous_digit_pos2 = digit_pos2;
        return return_value;
    }
        
    /**
     * Prompts the user to enter an input.
     * The input numbers are then separated by comma and extra spaces are removed
     * @return raw_values Array of integers given by user
     */
    public static String[] request_input() {
        String answer = JOptionPane.showInputDialog(null, "Type in numbers separated by comma.");
        String[] raw_values = answer.split(",[ ]*"); // Separates numbers by comma, removes extra spaces
        if (debug) System.out.println(answer);
        return raw_values;
    }
    
    /**
     * Converts number in character representation(e.g. '1') into binary String representation (e.g. "0001") 
     * COnverted number has 4 digits and only supports positive numbers
     * @param nibble Character representation of number to convert to binary
     * @return String representation of binary number using 4 bits
     */
    public static String decimal_to_binary(char nibble) {
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
     * Uses Log2SubBand algorithm for decoding.
     * Decodes String of zeroes and ones into comma separated String of numbers
     * @param encoded String to decode
     * @return Decoded numbers separated by comma
     */
    public static String decode_string(String encoded) {
        String remaining_string = encoded;
        String current_number = "";
        String[] results;
        String decoded_string = "";
        
        
        while (!remaining_string.isEmpty()) {
            if (debug) System.out.print("remaining: " + remaining_string + "(" + remaining_string.length() + ")\n");
            results = decode_substring(remaining_string, current_number);
            current_number = results[0];
            decoded_string += "," + current_number;
            if (debug) System.out.println("Current number: " + current_number + "\n");
            remaining_string = results[1];
        }
        decoded_string = decoded_string.substring(1); // Remove trailing comma
        return decoded_string;
    }
    
    public static String[] decode_substring(String encoded_substring, String current_number) {
        String decoded_substring = current_number;
        String header = encoded_substring.substring(0,2);
        if (debug) System.out.print("Header: " + header + "\n");
        String remaining_string = encoded_substring.substring(2); // Removing header from string
        
        switch (header) {
            case "00":  decoded_substring = current_number;
                break;
            case "01":  decoded_substring = current_number.substring(0,2) + get_decimal_digit(remaining_string.substring(0,4));
                        remaining_string = remaining_string.substring(4);
                break;
            case "10":  decoded_substring = current_number.substring(0,1) + get_decimal_digit(remaining_string.substring(0,4))
                                       + get_decimal_digit(remaining_string.substring(4,8));
                        remaining_string = remaining_string.substring(8);
                break;
            case "11":  decoded_substring = get_decimal_digit(remaining_string.substring(0,4))
                                       + get_decimal_digit(remaining_string.substring(4,8))
                                       + get_decimal_digit(remaining_string.substring(8,12));
                        remaining_string = remaining_string.substring(12);
                break;
        }
        if (debug) System.out.println("Current decoded: " + decoded_substring + "\n");
        String stuff_to_return[] = {decoded_substring,remaining_string};
        return stuff_to_return;
    }
    
    public static String get_decimal_digit(String binary) {
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
    
    public static Double compression_percentage(String overall_compressed, String overall_uncompressed) {
        return Math.round(1000.0 * overall_compressed.length()/overall_uncompressed.length())/10.0;
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
     * Attempts to parse a CSV file.
     * !!! Currently no check if file is a valid csv file
     * @param file_path Path to a file to parse
     * @return
     * @throws java.io.FileNotFoundException
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
                            if (value.length() > 0) { // Need to skip empty cells
                                comma_sep_values += value + ",";
                            }
                        }
                    }
                    comma_sep_values = comma_sep_values.substring(0, comma_sep_values.length()-1); // remove last comma
                    data_parsed = comma_sep_values.split(",[ ]*"); // Separates numbers by comma, removes extra spaces
                    
                    if(debug) System.out.println(comma_sep_values);
                } catch (IndexOutOfBoundsException e) {} // No info about size, need to try and read until we get too far         
            }
            catch (IOException ex) {Logger.getLogger(Log2SubBand.class.getName()).log(Level.SEVERE, null, ex);}  // autogenerated   
        }
        return data_parsed;
    }
    
    /**
     * Provides a panel with 2 options - manual data entry or importing a csv file
     * 
     * @return String selected option 
     */
    
    public static boolean data_entry_option_prompt() {
        return false;
    }
    
    // @TODO ask user if they want to enter data manually or inport a file instead
    public static void main(String[] args) {
        String overall_compressed = "";
        String overall_uncompressed = "";
        String current_compressed;
        String[] raw_values;
        String input = "";
        
        String[] options = new String[2];
        options[0] = "Manual entry";
        options[1] = "CSV file import";
        String message = "Would you like to enter data manually or import a csv file with data?";
        int dialog_result = JOptionPane.showOptionDialog(null,message,"Title", 0,JOptionPane.QUESTION_MESSAGE,null,options,null);

        if(dialog_result == 0) {
            raw_values = request_input();
        } else {
            String csv_file = request_file();
            raw_values = parse_CSV(csv_file);
        }
        
        for (String raw_value : raw_values) {
            raw_value = prepend_zeroes_if_needed(raw_value);
            input += "," + raw_value;
            
            if (debug) System.out.println("Raw value: " + raw_value);
            current_compressed = get_compressed_data(raw_value);
            if (debug) System.out.println("Current compressed data: " + current_compressed);
            overall_compressed += current_compressed;
            
            for(char c : raw_value.toCharArray()) overall_uncompressed += decimal_to_binary(c);
        }
        input = input.substring(1);
        
        System.out.println("Input:   " + input);
        System.out.println("Compressed data:   " + overall_compressed);
        System.out.println("Total compressed length = " + overall_compressed.length());
        System.out.println("Uncompressed data: " + overall_uncompressed);
        System.out.println("Total uncompressed length = " + overall_uncompressed.length());
        double compression_rate = compression_percentage(overall_compressed, overall_uncompressed);
        System.out.println("Overall compression rate: " + compression_rate + "%");
        System.out.println("Decompressed data: " + decode_string(overall_compressed));
        
    }
    
}
