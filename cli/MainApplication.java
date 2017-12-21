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
		 * -t, autoroutcer timeout in seconds. Default 60. 0 for infinite.
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
		 * 
		 * Error Codes:
		 * 1, Unknown
		 * 2, Invalid input
		 * 3, File read/write errors
		 * 4, Design errors
		 * 5, Timeout
		 */
		
		try
		{
			String design_file_name = "";
			String output_file_name = "";
			String output_file_name_dsn = "";
			List<String> layers_to_route = new ArrayList<String>();
			Map<String, String> layer_directions = new HashMap<String, String>();
			Map<String, Integer> layer_ids = new HashMap<String, Integer>();
			Map<String, Double> layer_pref_dir_cost = new HashMap<String, Double>();
			Map<String, Double> against_layer_pref_dir_cost = new HashMap<String, Double>();
			int timeout = 60;
			boolean vias_allowed = true;
			boolean do_fanout = false;
			boolean do_autorouting = true;
			boolean do_postrouting = true;
			int via_cost = 50;
			int ppvia_cost = 5;
			int start_pass = 91;
			int ripup_start_cost = 100;
			
			Map<String, String> arguments = new HashMap<String, String>();
			for (int i = 0; i < p_args.length; ++i)
			{
				if (p_args[i].startsWith("-") && !p_args[i+1].startsWith("-"))
				{
					arguments.put(p_args[i].toLowerCase(), p_args[i+1]);
				}
			}
			
			if (arguments.containsKey("-de"))
			{
				design_file_name = arguments.get("-de");
                if (!design_file_name.toLowerCase().endsWith(".dsn"))
                {
                	ErrorOut("Wrong input file format at -de", 2);
                }
			}
			else
			{
				ErrorOut("Input design file must be specified at -de", 2);
			}
			
			if (arguments.containsKey("-o"))
			{
				output_file_name = arguments.get("-o");
        		if (!output_file_name.toLowerCase().endsWith(".ses"))
        		{
        			ErrorOut("Wrong ses output file format at -o", 2);
        		}
			}
			else
			{
				ErrorOut("Output session file must be specified at -o", 2);
			}
			
			if (arguments.containsKey("-od"))
			{
				output_file_name_dsn = arguments.get("-od");
        		if (!output_file_name_dsn.toLowerCase().endsWith(".dsn"))
        		{
        			ErrorOut("Wrong dsn output file format at -od", 2);
        		}
			}
			
			if (arguments.containsKey("-t"))
			{
				String s_timeout = arguments.get("-t");
        		try
        		{
        			timeout = Integer.parseInt(s_timeout);
        		}
        		catch (NumberFormatException ex)
        		{
        			ErrorOut("Timeout parameter invalid at -t", 2);
        		}
        		if (timeout < 0)
        		{
        			ErrorOut("Timeout parameter invalid at -t, must be >= 0", 2);
        		}
			}
			
			if (arguments.containsKey("-l"))
			{
        		String s_layers = arguments.get("-l");
        		String[] a_layers = s_layers.split(",");
        		layers_to_route.addAll(Arrays.asList(a_layers));
			}
			
			if (arguments.containsKey("-ld"))
			{
				String s_layer_directions = arguments.get("-ld");
        		String[] a_layer_directions = s_layer_directions.split(",");
        		if (a_layer_directions.length != layers_to_route.size())
        		{
        			ErrorOut("Length mismatch between layers and layer directions (-l and -ld)", 2);
        		}
        		for (int j = 0; j < a_layer_directions.length; j++)
        		{
        			if (!(a_layer_directions[j].toLowerCase().equals("h") || a_layer_directions[j].toLowerCase().equals("v")))
        			{
        				ErrorOut("Layer directions must be either 'h' or 'v' at -ld", 2);
        			}
        			layer_directions.put(layers_to_route.get(j), a_layer_directions[j].toLowerCase());
        		}
			}
			
			if (arguments.containsKey("-v"))
			{
				String s_vias = arguments.get("-v").toLowerCase();
				if (!(s_vias.equals("y") || s_vias.equals("n")))
				{
					ErrorOut("Vias allowed can only be specified as 'y' or 'n' at -v", 2);
				}
        		vias_allowed = !s_vias.equals("n");
			}
			
			if (arguments.containsKey("-fo"))
			{
				String s_fanout = arguments.get("-fo").toLowerCase();
				if (!(s_fanout.equals("y") || s_fanout.equals("n")))
				{
					ErrorOut("Do fanout can only be specified as 'y' or 'n' at -fo", 2);
				}
				do_fanout = s_fanout.equals("y");
			}
			
			if (arguments.containsKey("-ar"))
			{
				String s_autoroute = arguments.get("-ar").toLowerCase();
				if (!(s_autoroute.equals("y") || s_autoroute.equals("n")))
				{
					ErrorOut("Do autorouting can only be specified as 'y' or 'n' at -ar", 2);
				}
				do_autorouting = !s_autoroute.equals("n");
			}
			
			if (arguments.containsKey("-pr"))
			{
				String s_postroute = arguments.get("-ar").toLowerCase();
				if (!(s_postroute.equals("y") || s_postroute.equals("n")))
				{
					ErrorOut("Do postrouting can only be specified as 'y' or 'n' at -pr", 2);
				}
				do_postrouting = !s_postroute.equals("n");
			}
			
			if (arguments.containsKey("-vc"))
			{
				String s_viacost = arguments.get("-vc").toLowerCase();
				try
        		{
					via_cost = Integer.parseInt(s_viacost);
        		}
        		catch (NumberFormatException ex)
        		{
        			ErrorOut("Via cost parameter invalid at -vc", 2);
        		}
        		if (timeout < 0)
        		{
        			ErrorOut("Via cost parameter invalid at -t, must be >= 0", 2);
        		}
			}
			
			if (arguments.containsKey("-ppvc"))
			{
				String s_ppviacost = arguments.get("-ppvc").toLowerCase();
				try
        		{
					ppvia_cost = Integer.parseInt(s_ppviacost);
        		}
        		catch (NumberFormatException ex)
        		{
        			ErrorOut("Powerplane via cost parameter invalid at -ppvc", 2);
        		}
        		if (timeout < 0)
        		{
        			ErrorOut("Powerplane via cost parameter invalid at -ppvc, must be >= 0", 2);
        		}
			}
			
			if (arguments.containsKey("-sp"))
			{
				String s_startpass = arguments.get("-sp").toLowerCase();
				try
        		{
					start_pass = Integer.parseInt(s_startpass);
        		}
        		catch (NumberFormatException ex)
        		{
        			ErrorOut("StartPass parameter invalid at -sp", 2);
        		}
        		if (timeout < 0)
        		{
        			ErrorOut("StartPass parameter invalid at -sp. Must be between 0 <= sp < 100", 2);
        		}
			}
			
			if (arguments.containsKey("-rsc"))
			{
				String s_ripupstartcost = arguments.get("-rsc").toLowerCase();
				try
        		{
					ripup_start_cost = Integer.parseInt(s_ripupstartcost);
        		}
        		catch (NumberFormatException ex)
        		{
        			ErrorOut("Ripup start cost parameter invalid at -rsc", 2);
        		}
        		if (timeout < 0)
        		{
        			ErrorOut("Ripup start cost parameter invalid at -rsc. Must be between 0 >= 0", 2);
        		}
			}
			
			if (arguments.containsKey("-pdc"))
			{
				String s_pref_dir = arguments.get("-pdc");
        		String[] a_pref_dir = s_pref_dir.split(",");
        		if (a_pref_dir.length != layers_to_route.size())
        		{
        			ErrorOut("Length mismatch between layers and layer preferred direction cost (-l and -pdc)", 2);
        		}
        		for (int j = 0; j < a_pref_dir.length; j++)
        		{
        			try
        			{
        				double cost = Double.parseDouble(a_pref_dir[j]);
        				if (cost < 0)
        				{
        					ErrorOut("Invalid parameter at -pdc. Values must be >= 0", 2);
        				}
        				layer_pref_dir_cost.put(layers_to_route.get(j), cost);
        			}
        			catch (NumberFormatException ex)
        			{
        				ErrorOut("Invalid parameter at -pdc", 2, ex);
        			}
        		}
			}
			
			if (arguments.containsKey("-apdc"))
			{
				String s_against_pref_dir = arguments.get("-apdc");
        		String[] a_against_pref_dir = s_against_pref_dir.split(",");
        		if (a_against_pref_dir.length != layers_to_route.size())
        		{
        			ErrorOut("Length mismatch between layers and against layer preferred direction cost (-l and -apdc)", 2);
        		}
        		for (int j = 0; j < a_against_pref_dir.length; j++)
        		{
        			try
        			{
        				double cost = Double.parseDouble(a_against_pref_dir[j]);
        				if (cost < 0)
        				{
        					ErrorOut("Invalid parameter at -apdc. Values must be >= 0", 2);
        				}
        				against_layer_pref_dir_cost.put(layers_to_route.get(j), cost);
        			}
        			catch (NumberFormatException ex)
        			{
        				ErrorOut("Invalid parameter at -apdc", 2, ex);
        			}
        		}
			}
	
			//Variable instantiation
			DesignFile design_file = DesignFile.get_instance(design_file_name, false);
			Locale current_locale = java.util.Locale.ENGLISH;
			
			//Design file parsing
			java.io.InputStream input_stream = design_file.get_input_stream();
	        if (input_stream == null)
	        {
	        	ErrorOut("Could not read design input file!", 3);
	        }
			
			BoardHandling bh = new BoardHandling(current_locale);
			DsnFile.ReadResult read_result = bh.import_design(input_stream, new BoardObserverAdaptor(), new board.ItemIdNoGenerator(), TestLevel.RELEASE_VERSION);
			try {
				input_stream.close();
			} catch (IOException e1) {
				ErrorOut("Error while closing input stream to design input file!", 3);
			}
			
			if (read_result == DsnFile.ReadResult.ERROR)
				ErrorOut("Unspecified error while reading design input file!", 3);
			else if (read_result == DsnFile.ReadResult.OUTLINE_MISSING)
				ErrorOut("Outline missing in design file!", 4);
			
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
					if (layer_pref_dir_cost.containsKey(l.name))
					{
						bh.settings.autoroute_settings.set_preferred_direction_trace_costs(curr_layer_no, layer_pref_dir_cost.get(l.name));
					}
					if (against_layer_pref_dir_cost.containsKey(l.name))
					{
						bh.settings.autoroute_settings.set_against_preferred_direction_trace_costs(curr_layer_no, against_layer_pref_dir_cost.get(l.name));
					}
				}
				else
				{
					bh.settings.autoroute_settings.set_layer_active(curr_layer_no, false);
				}
			}
			
			//Set global autorouter settings
			bh.settings.autoroute_settings.set_vias_allowed(vias_allowed);
			bh.settings.autoroute_settings.set_with_fanout(do_fanout);
			bh.settings.autoroute_settings.set_with_autoroute(do_autorouting);
			bh.settings.autoroute_settings.set_with_postroute(do_postrouting);
			bh.settings.autoroute_settings.set_via_costs(via_cost);
			bh.settings.autoroute_settings.set_plane_via_costs(ppvia_cost);
			bh.settings.autoroute_settings.set_pass_no(start_pass);
			bh.settings.autoroute_settings.set_start_ripup_costs(ripup_start_cost);
			
			//Do the routing
			bh.start_batch_autorouter();
			
			//Wait until completed or timed out
			try {
				int cycles = 0; 
				if (timeout > 0)
				{
					do
					{
						TimeUnit.MILLISECONDS.sleep(500);
						cycles++;
						if ((cycles / 2) > timeout)
						{
							ErrorOut("Autorouting time out!", 5);
						}
					}
					while(!bh.has_autorouted);
				}
			} catch (InterruptedException ex) {
				ErrorOut("Interrupted..!", 0, ex);
			}
			
			//Write the results to the output file
			try
			{
				if (!output_file_name.isEmpty())
				{
					File output_file = new File(output_file_name);
					java.io.OutputStream output_stream = new FileOutputStream(output_file);
					if (!output_file.exists())
						output_file.createNewFile();
					bh.export_specctra_session_file("FreeRouted", output_stream);
					output_stream.close();
				}
				if (!output_file_name_dsn.isEmpty())
				{
					File output_file_dsn = new File(output_file_name_dsn);
					java.io.OutputStream dsn_output_stream = new FileOutputStream(output_file_dsn);
					if (!output_file_dsn.exists())
						output_file_dsn.createNewFile();
					bh.export_to_dsn_file(dsn_output_stream, "test", false);
					dsn_output_stream.close();
				}
			}
			catch (Exception ex)
			{
				ErrorOut("Error while writing to output!", 3, ex);
			}
			
			System.out.println("DONE");
		}
		catch (Exception ex)
		{
			ErrorOut("Unknown error! Exiting...", 1, ex);
		}
    }
	
	static private void ErrorOut(String message,  int errorCode)
	{
		System.out.println(message + " Code " + String.valueOf(errorCode));
		Runtime.getRuntime().exit(errorCode);
	}

	static private void ErrorOut(String message, int errorCode, Exception ex)
	{
		System.out.println(message + " Code " + String.valueOf(errorCode));
		System.out.println("Message: " + ex.getMessage());
		Runtime.getRuntime().exit(errorCode);
	}
}
