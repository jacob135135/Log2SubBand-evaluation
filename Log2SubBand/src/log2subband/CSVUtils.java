/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static log2subband.HuffmanCode.number_to_encoding_dict;
import static log2subband.MainExecution.debug;
import static log2subband.MyUtils.CSstring_to_array;
import static log2subband.MyUtils.append_spaces;

/**
 *
 * @author JAKUB
 */
public class CSVUtils {
    
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
     * Uses <code>number_to_encoding_dict</code> mapping of numbers and their encodings and creates csv file with
     * 2 columns, one for numbers and the other for their respective encodings
     */
    public static void export_Huff_codebook(String name) {
        String to_export = "Original," + append_spaces("Encoded", 14);
        for (Map.Entry<String, String> entrySet : number_to_encoding_dict.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            to_export += "\n" + key + "," + value;
        }
        String[] export = to_export.split(",");
        write_CSV("../test files/" + name + "_codebook", export);
    }
    
}
