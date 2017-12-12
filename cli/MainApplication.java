package cli;

import java.util.Locale;

import board.TestLevel;
import designformats.specctra.DsnFile;
import gui.BoardFrame;
import gui.DesignFile;
import interactive.BoardHandling;

public class MainApplication {
	
	public static void main(String p_args[])
    {
		String design_file_name = "";
		
		for (int i = 0; i < p_args.length; ++i)
        {
            if (p_args[i].startsWith("-de"))
            {
                if (p_args.length > i + 1 && !p_args[i + 1].startsWith("-"))
                {
                    design_file_name = p_args[i + 1];
                }
            }
        }
		
		if (design_file_name.isEmpty())
		{
			ErrorOut("No filename was specified! Exiting...", 1);
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
		DsnFile.ReadResult result = bh.import_design(input_stream, null, new board.ItemIdNoGenerator(), TestLevel.RELEASE_VERSION);
		System.out.println("DONE");
    }

	static private void ErrorOut(String message, int errorCode)
	{
		System.out.println(message);
		Runtime.getRuntime().exit(errorCode);
	}
}
