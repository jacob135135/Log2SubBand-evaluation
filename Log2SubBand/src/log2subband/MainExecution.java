package log2subband;

import java.io.File;
import java.util.Map;
import static log2subband.Log2SubBand.parameters;
import menuUI.InputMenu;

/**
 * @author JAKUB
 */
public class MainExecution {
    public static boolean debug;
    static boolean is_bin_system;

    public static void main(String[] args) {
        InputMenu input_menu = new InputMenu(); // ALL INPUT OBTAINED FROM THERE
    }
    
    public static void main_execution(InputMenu input_menu) throws Exception {
        boolean run_all_files = input_menu.get_run_all_files();

        if (run_all_files) {
            File input_file = input_menu.get_input_file();
            File[] appropriate_files = MyUtils.get_appropriate_files_in_same_folder(input_file);
            for (File f : appropriate_files) {
                System.out.println(f);
                String cur_name = f.getName();
            }
        }
        System.exit(0);
        String[] raw_values_array = input_menu.get_input_data();
        is_bin_system = input_menu.is_binary_number_system();

        Map<String, String> data_info = CompressionUtils.GetDataInfo(raw_values_array);
        String bin_concat_input = data_info.get("bin_concat_input");
        String cs_input_string = data_info.get("cs_input");

        CompressionUtils.set_up_Huffman(input_menu.get_codebook_data(), raw_values_array);
        double huff_compr_rate = CompressionUtils.print_Huffman_compression_results(cs_input_string, bin_concat_input);

        if (!input_menu.get_run_all_parameters()) {
            parameters = input_menu.get_run_parameters();
            Log2SubBand.single_subband_compress(raw_values_array, cs_input_string,bin_concat_input);
        } else {
            Log2SubBand.all_permutations_subband_compress(raw_values_array, cs_input_string, bin_concat_input, huff_compr_rate);
        }
        finalise(cs_input_string, bin_concat_input, input_menu.get_open_exported());
    }
    /**
     * Initialises variables with values given by input menu.
     * This function reduces clutter in main_execution function
     * @param cs_input_string Initial data to compress separated by comma
     * @param bin_concat_input Initial data to compress concatenated (no spaces or commas)
     * @param open_exported Boolean whether export generated file should be opened
     */
    public static void finalise(String cs_input_string, String bin_concat_input, boolean open_exported) {
        CSVUtils.export_Huff_codebook(); // uses number_to_encoding_dict
        if(open_exported) MyUtils.open_file("compressed.csv");
        System.exit(0);
    }

}
