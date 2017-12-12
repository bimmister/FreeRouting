package cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import board.BoardObserverAdaptor;
import board.TestLevel;
import designformats.specctra.DsnFile;
import gui.BoardFrame;
import gui.DesignFile;
import interactive.BoardHandling;

public class MainApplication {
	
	public static void main(String p_args[])
    {
		String design_file_name = "";
		String output_file_name = "";
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
        }
		
		if (design_file_name.isEmpty())
		{
			ErrorOut("No input filename was specified! Exiting...", 1);
		}
		if (output_file_name.isEmpty())
		{
			ErrorOut("No output filename was specified! Exiting...", 1);
		}

		BoardFrame.Option board_option = BoardFrame.Option.SINGLE_FRAME;
		DesignFile design_file = DesignFile.get_instance(design_file_name, false);
		boolean test_version_option = false;
		Locale current_locale = java.util.Locale.ENGLISH;
		
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
		
		bh.start_batch_autorouter();
		
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
		
		try
		{
			File output_file = new File(output_file_name);
			java.io.OutputStream output_stream = new FileOutputStream(output_file);
			if (!output_file.exists())
				output_file.createNewFile();
			bh.export_to_dsn_file(output_stream, "test", false);
			output_stream.close();
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
