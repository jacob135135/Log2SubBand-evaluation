/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log2subband;

import java.util.Map;
import static log2subband.Log2SubBand.parameters;
import menuUI.InputMenu;

/**
 *
 * @author JAKUB
 */
public class MainExecution {
    
    public static void main(String[] args) {
        InputMenu input_menu = new InputMenu(); // ALL INPUT OBTAINED FROM THERE
    }
    
    public static void main_execution(InputMenu input_menu) throws Exception {
        String[] raw_values = input_menu.getInput_data();
        boolean open_exported = input_menu.getOpen_exported();
        parameters = input_menu.getRun_parameters();
        
        Log2SubBand.previous_least_significant_nibble = MyUtils.generate_zeroes(parameters[2]);
        Log2SubBand.previous_middle_nibble = MyUtils.generate_zeroes(parameters[1]);
        Log2SubBand.previous_most_significant_nibble = MyUtils.generate_zeroes(parameters[0]);

        Map<String, String> result = CompressionUtils.perform_log2_sub_band_compression(raw_values);
        String overall_compressed = result.get("overall_compressed");
        String overall_uncompressed = result.get("overall_uncompressed");
        String input_string = result.get("input");
        String output_string = result.get("output");

        CompressionUtils.print_log2subband_compression_results(input_string, overall_compressed, overall_uncompressed);

        String[] custom_codebook = input_menu.getCodebook_data();
        if (custom_codebook.length > 0) CompressionUtils.init_codebook_from_imported_codebook(custom_codebook);
        else HuffmanCode.init_ideal_huffman_dictionaries(raw_values);

        String[] input_array = input_string.split(",");
        String[] output_array = output_string.split(",");
        String[] binary_input = MyUtils.split_by_length(overall_uncompressed,12);
        String[] export_data = MyUtils.make_export_table(input_array, output_array, binary_input);
        CompressionUtils.print_Huffman_compression_results(input_array, overall_uncompressed);

        CSVUtils.export_codebook(); // uses number_to_encoding_dict
        CSVUtils.write_CSV("compressed", export_data);
        
        if(open_exported) MyUtils.open_file("compressed.csv");
        System.exit(0);
    }

}
