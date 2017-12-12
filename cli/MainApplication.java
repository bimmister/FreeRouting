package cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import board.BoardObserverAdaptor;
import board.Layer;
import board.TestLevel;
import designformats.specctra.DsnFile;
import gui.BoardFrame;
import gui.DesignFile;
import interactive.BoardHandling;

public class MainApplication {
	
	public static void main(String p_args[])
    {
		/*
		 * Program parameters:
		 * -de, design file (dsn)
		 * -o, output file (ses)
		 * -od, output file (dsn)
		 * -t, autorouter timeout in seconds. Default 60
		 * -l, comma separated list of layers to route. Mandatory if referenced later
		 * -ld, preferred directions of layers. Same order as in -l. v for vertical, h for horizontal
		 * -v, Vias allowed? y/n, default y.
		 * -fo, Do fanout? y/n, default n.
		 * -ar, Do autoroute? y/n, default y.
		 * -pr, Do postroute? y/n, default y.
		 * -vc, Via cost, default 50
		 * -ppvc, Powerplane via cost, default 5
		 * -sp, Start pass, default 91
		 * -rsc, Ripup start cost, default 100
		 * -pdc, Preferred direction trace cost, CSV by layer, same order as -l. Default 1 for all
		 * -apdc, Against preferred direction trace cost, CSV by layer, same order as -l. Default 2 for all
		 */
		
		
		String design_file_name = "";
		String output_file_name = "";
		String output_file_name_dsn = "";
		List<String> layers_to_route = new ArrayList<String>();
		Map<String, String> layer_directions = new HashMap<String, String>();
		Map<String, Integer> layer_ids = new HashMap<String, Integer>();
		int timeout = 60;
		
		for (int i = 0; i < p_args.length; ++i)
        {
            if (p_args[i].startsWith("-de"))
            {
                if (p_args.length > i + 1 && !p_args[i + 1].startsWith("-"))
                {
                    design_file_name = p_args[i + 1];
                }
            }
            else if (p_args[i].startsWith("-od"))
            {
            	if (p_args.length > i+1 && !p_args[i+1].startsWith("-"))
            	{
            		output_file_name_dsn = p_args[i+1];
            	}
            }
            else if (p_args[i].startsWith("-o"))
            {
            	if (p_args.length > i+1 && !p_args[i+1].startsWith("-"))
            	{
            		output_file_name = p_args[i+1];
            	}
            }
            else if (p_args[i].startsWith("-t"))
            {
            	if (p_args.length > i+1 && !p_args[i+1].startsWith("-"))
            	{
            		String s_timeout = p_args[i+1];
            		timeout = Integer.parseInt(s_timeout);
            	}
            }
            else if (p_args[i].startsWith("-ld"))
            {
            	if (p_args.length > i+1 && !p_args[i+1].startsWith("-"))
            	{
            		String s_layer_directions = p_args[i+1];
            		String[] a_layer_directions = s_layer_directions.split(",");
            		for (int j = 0; j < a_layer_directions.length; j++)
            		{
            			layer_directions.put(layers_to_route.get(j), a_layer_directions[j]);
            		}
            	}
            }
            else if (p_args[i].startsWith("-l"))
            {
            	if (p_args.length > i+1 && !p_args[i+1].startsWith("-"))
            	{
            		String s_layers = p_args[i+1];
            		String[] a_layers = s_layers.split(",");
            		layers_to_route.addAll(Arrays.asList(a_layers));
            	}
            }
        }
		
		if (design_file_name.isEmpty())
		{
			ErrorOut("No input filename was specified! Exiting...", 1);
		}
		if (output_file_name.isEmpty())
		{
			ErrorOut("No output filename was specified! Exiting...", 1);
		}

		//Variable instantiation
		DesignFile design_file = DesignFile.get_instance(design_file_name, false);
		Locale current_locale = java.util.Locale.ENGLISH;
		
		//Design file parsing
		java.io.InputStream input_stream = design_file.get_input_stream();
        if (input_stream == null)
        {
        	ErrorOut("Could not read design file!", 1);
        }
		
		BoardHandling bh = new BoardHandling(current_locale);
		DsnFile.ReadResult read_result = bh.import_design(input_stream, new BoardObserverAdaptor(), new board.ItemIdNoGenerator(), TestLevel.RELEASE_VERSION);
		try {
			input_stream.close();
		} catch (IOException e1) {
			ErrorOut("Error while closing input stream!", 1);
		}
		
		if (read_result == DsnFile.ReadResult.ERROR)
			ErrorOut("Unspecified error while reading design file!", 1);
		else if (read_result == DsnFile.ReadResult.OUTLINE_MISSING)
			ErrorOut("Outline missing in design file!", 2);
		
		//Set the layer-specific autorouting settings
		for (Layer l: bh.get_routing_board().layer_structure.arr)
		{
			int curr_layer_no = bh.get_routing_board().layer_structure.get_no(l.name);
			if (layers_to_route.isEmpty() || layers_to_route.contains(l.name))
			{
				layer_ids.put(l.name, curr_layer_no);
				//Set if enabled at all
				bh.settings.autoroute_settings.set_layer_active(curr_layer_no, true);
				
				//Set the preferred direction
				if (layer_directions.containsKey(l.name))
				{
					bh.settings.autoroute_settings.set_preferred_direction_is_horizontal(curr_layer_no, layer_directions.get(l.name).equals("h"));
				}
			}
			else
			{
				bh.settings.autoroute_settings.set_layer_active(curr_layer_no, false);
			}
		}
		
		
		//Do the routing
		bh.start_batch_autorouter();
		
		//Wait until completed or timed out
		try {
			int cycles = 0; 
			do
			{
				TimeUnit.MILLISECONDS.sleep(500);
				cycles++;
				if ((cycles / 2) > timeout)
				{
					ErrorOut("Autorouting time out!", 3);
				}
			}
			while(!bh.has_autorouted);
		} catch (InterruptedException e) {
			ErrorOut("Interrupted..!", 0);
		}
		
		//Write the results to the output file
		try
		{
			File output_file = new File(output_file_name);
			File output_file_dsn = new File(output_file_name_dsn);
			java.io.OutputStream output_stream = new FileOutputStream(output_file);
			java.io.OutputStream dsn_output_stream = new FileOutputStream(output_file_dsn);
			if (!output_file.exists())
				output_file.createNewFile();
			if (!output_file_dsn.exists())
				output_file_dsn.createNewFile();
			bh.export_specctra_session_file("FreeRouted", output_stream);
			bh.export_to_dsn_file(dsn_output_stream, "test", false);
			output_stream.close();
			dsn_output_stream.close();
		}
		catch (Exception e)
		{
			ErrorOut("Error while writing to output!", 1);
		}
		
		System.out.println("DONE");
    }

	static private void ErrorOut(String message, int errorCode)
	{
		System.out.println(message);
		Runtime.getRuntime().exit(errorCode);
	}
}
