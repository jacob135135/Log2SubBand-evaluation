/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.swing.JOptionPane;
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
        String[] raw_values = answer.split(",[ ]*"); // Separates numbers by comma, removes extra spaces
        if (debug) System.out.println(answer);
        return raw_values;
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
                    data_parsed = comma_sep_values.split(",[ ]*"); // Separates numbers by comma, removes extra spaces
                    
                    if(debug) System.out.println(comma_sep_values);
                } catch (IndexOutOfBoundsException e) {} // No info about size, need to try and read until we get too far         
            }
            catch (IOException ex) {Logger.getLogger(Log2SubBand.class.getName()).log(Level.SEVERE, null, ex);}  // autogenerated   
        }
        return data_parsed;
    }
}
