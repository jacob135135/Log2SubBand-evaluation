package log2subband;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import static log2subband.Log2SubBand.parameters;
import menuUI.InputMenu;

/**
 * @author JAKUB
 */
public class MainExecution {
    public static boolean debug;
    static boolean is_bin_system;
    static String running_setting;
    static String data_files = "";
    public static Boolean run_all_files;
    public static boolean DPCM_for_Huffman;
    public static boolean give_stats_for_every_file;
    public static boolean run_all_parameters;

    public static void main(String[] args) {
        InputMenu input_menu = new InputMenu(); // ALL INPUT OBTAINED FROM THERE
    }
    
    public static void main_execution(InputMenu input_menu) throws Exception {
        File input_file = input_menu.get_input_file();

        if (run_all_files && give_stats_for_every_file) {
            File[] appropriate_files = MyUtils.get_appropriate_files_in_same_folder(input_file);
            for (File f : appropriate_files) {
                if(debug) System.out.println(f);
                String cur_name = f.getName();
                data_files = f.getName();
                String[] raw_values_array = CSVUtils.parse_CSV(f.getAbsolutePath());
                raw_values_array = MyUtils.fit_data_into_boundary(raw_values_array);
                Map<String, String> data_info = CompressionUtils.GetDataInfo(raw_values_array);
                String bin_concat_input = data_info.get("bin_concat_input");
                String cs_input_string = data_info.get("cs_input");
                CompressionUtils.set_up_Huffman(input_menu.get_codebook_data(), raw_values_array);
                double huff_compr_rate = CompressionUtils.get_Huffman_CR(cs_input_string, bin_concat_input);
                if (!run_all_parameters) {
                    parameters = input_menu.get_run_parameters();
                    running_setting = "RUNNING: " + parameters[0] + "`" + parameters[1] + "`" + parameters[2] + "`" + parameters[3];
                    Log2SubBand.single_subband_compress(raw_values_array, cs_input_string,bin_concat_input, huff_compr_rate, cur_name);
                } else {
                    running_setting = "ALL PARAMETERS";
                    Log2SubBand.all_permutations_subband_compress(raw_values_array, cs_input_string, bin_concat_input, huff_compr_rate, cur_name);
                }
                CSVUtils.export_Huff_codebook(cur_name);
                if(input_menu.get_open_exported()) MyUtils.open_file("stats_" + cur_name+ ".csv");
            }
        }

        // If running all the files => the following will give stats about them as if it was 1 whole file
        // If running just a single file => only that file will be run
        
        System.out.println("START: (" + LocalDateTime.now() + ")" );

        String[] raw_values_array;
        String filename = input_file.getName();
        is_bin_system = input_menu.is_binary_number_system();

        System.out.println("Started getting data from files: (" + LocalDateTime.now() + ")" );
        if (run_all_files) {
            raw_values_array = MyUtils.get_all_files_data(input_file);
            filename = "overall";
        } else {
            data_files = input_menu.get_input_file().getName();
            raw_values_array = input_menu.get_input_data();
        }
        System.out.println("TOTAL DATA NUMBERS: " + raw_values_array.length);
        System.out.println("Got data from files: (" + LocalDateTime.now() + ")" );

        raw_values_array = MyUtils.fit_data_into_boundary(raw_values_array);
        Map<String, String> data_info = CompressionUtils.GetDataInfo(raw_values_array);
        System.out.println("Got basic data info from files: (" + LocalDateTime.now() + ")" );
        String bin_concat_input = data_info.get("bin_concat_input");
        String cs_input_string = data_info.get("cs_input");

        System.out.println("Starting to do Huffman: (" + LocalDateTime.now() + ")" );
        CompressionUtils.set_up_Huffman(input_menu.get_codebook_data(), raw_values_array);
        System.out.println("Starting to get Huffman CR: (" + LocalDateTime.now() + ")" );
        double huff_compr_rate = CompressionUtils.get_Huffman_CR(cs_input_string, bin_concat_input);

        if (!run_all_parameters) {
            parameters = input_menu.get_run_parameters();
            running_setting = "RUNNING: " + parameters[0] + "`" + parameters[1] + "`" + parameters[2] + "`" + parameters[3];
            System.out.println("Starting to run parameter: (" + LocalDateTime.now() + ")" );
            Log2SubBand.single_subband_compress(raw_values_array, cs_input_string,bin_concat_input, huff_compr_rate, filename);
            System.out.println("HEADER 00 count: " + Log2SubBand.header00_count);
            System.out.println("HEADER 01 count: " + Log2SubBand.header01_count);
            System.out.println("HEADER 10 count: " + Log2SubBand.header10_count);
            System.out.println("HEADER 11 count: " + Log2SubBand.header11_count);
        } else {
            running_setting = "ALL PARAMETERS";
            System.out.println("Starting to run all parameters: (" + LocalDateTime.now() + ")" );
            Log2SubBand.all_permutations_subband_compress(raw_values_array, cs_input_string, bin_concat_input, huff_compr_rate, filename);
        }
        CSVUtils.export_Huff_codebook(filename);
        System.out.println("Huffman CR: " + huff_compr_rate);
        if(input_menu.get_open_exported()) MyUtils.open_file("stats_" + filename+".csv");
        System.out.println("Running setting was " + running_setting);

        System.exit(0);
    }

}
