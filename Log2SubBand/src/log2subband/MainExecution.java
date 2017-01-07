package log2subband;

import java.util.Map;
import static log2subband.Log2SubBand.parameters;
import menuUI.InputMenu;

/**
 * @author JAKUB
 */
public class MainExecution {
    public static boolean debug;

    public static void main(String[] args) {
        InputMenu input_menu = new InputMenu(); // ALL INPUT OBTAINED FROM THERE
    }
    
    public static void main_execution(InputMenu input_menu) throws Exception {
        String[] raw_values_array = input_menu.getInput_data();
        boolean is_bin_system = input_menu.is_binary_number_system();

        Map<String, String> data_info = CompressionUtils.GetDataInfo(raw_values_array, is_bin_system);
        String bin_concat_input = data_info.get("bin_concat_input");
        String cs_input_string = data_info.get("cs_input");

        CompressionUtils.setUpHuffman(input_menu.getCodebook_data(), raw_values_array, is_bin_system);

        Boolean run_all_parameters = input_menu.getRun_all_parameters();
        if (!run_all_parameters) {
            parameters = input_menu.getRun_parameters();
            Log2SubBand.single_subband_compress(raw_values_array, cs_input_string,bin_concat_input, is_bin_system);
        } else {
            Log2SubBand.all_permutations_subband_compress(raw_values_array, cs_input_string, bin_concat_input, is_bin_system);
        }
        finalise(cs_input_string, bin_concat_input, input_menu.getOpen_exported());
    }
    /**
     * Initialises variables with values given by input menu.
     * This function reduces clutter in main_execution function
     * @param cs_input_string Initial data to compress separated by comma
     * @param bin_concat_input Initial data to compress concatenated (no spaces or commas)
     * @param open_exported Boolean whether export generated file should be opened
     */
    public static void finalise(String cs_input_string, String bin_concat_input, boolean open_exported) {
        CompressionUtils.print_Huffman_compression_results(cs_input_string, bin_concat_input);
        CSVUtils.export_codebook(); // uses number_to_encoding_dict
        if(open_exported) MyUtils.open_file("compressed.csv");
        System.exit(0);
    }

}
