package log2subband;

import java.util.Map;
import static log2subband.Log2SubBand.parameters;
import menuUI.InputMenu;

/**
 * @author JAKUB
 */
public class MainExecution {
    public static boolean debug;
    static boolean open_exported;
    static boolean run_all_parameters;
    
    public static void main(String[] args) {
        InputMenu input_menu = new InputMenu(); // ALL INPUT OBTAINED FROM THERE
    }
    
    public static void main_execution(InputMenu input_menu) throws Exception {
        set_up(input_menu);
        String[] raw_values_array = input_menu.getInput_data();
        boolean is_bin_system = input_menu.is_binary_number_system();

        String[] custom_codebook = input_menu.getCodebook_data();
        if (custom_codebook.length > 0) CompressionUtils.init_codebook_from_imported_codebook(custom_codebook);
        else HuffmanCode.init_ideal_huffman_dictionaries(raw_values_array, is_bin_system);

        // @TODO add support for running all parameters and getting useful data, ideally parameter => CR for log2subband
        //if(run_all_parameters) { I NEED TO RUN ALL PARAMETERS AND GET SUCCINT STATS }

        Map<String, String> result = CompressionUtils.perform_log2_sub_band(raw_values_array, is_bin_system);
        String bin_concat_input = result.get("bin_concat_input");
        String cs_input_string = result.get("cs_input");
        CompressionUtils.print_log2subband_results(cs_input_string, result.get("compr"), bin_concat_input);

        String[] export_data = MyUtils.make_export_table(cs_input_string, result.get("cs_output"));
        CompressionUtils.print_Huffman_compression_results(cs_input_string, bin_concat_input);

        CSVUtils.export_codebook(); // uses number_to_encoding_dict
        CSVUtils.write_CSV("compressed", export_data);
        
        if(open_exported) MyUtils.open_file("compressed.csv");
        System.exit(0);
    }

    /**
     * Initialises variables with values given by input menu.
     * This function reduces clutter in main_execution function
     * @param input_menu
     */
    public static void set_up(InputMenu input_menu) {
        open_exported = input_menu.getOpen_exported();
        parameters = input_menu.getRun_parameters(); // makes no difference if run all parameters is true
        run_all_parameters = input_menu.getRun_all_parameters();
        Log2SubBand.update_previous_bands(parameters);
    }

}
