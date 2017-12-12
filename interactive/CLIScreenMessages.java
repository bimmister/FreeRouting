package interactive;

import javax.swing.JLabel;

import geometry.planar.FloatPoint;

public class CLIScreenMessages implements IScreenMessages {
	
	/** Creates a new instance of CLIScreenMessageFields */
    public CLIScreenMessages(java.util.Locale p_locale)
    {
        resources = java.util.ResourceBundle.getBundle("interactive.resources.ScreenMessages", p_locale);
        target_layer_string = resources.getString("target_layer") + " ";
        
        this.number_format =  java.text.NumberFormat.getInstance(p_locale);
        this.number_format.setMaximumFractionDigits(4);
    }

	@Override
	public void set_status_message(String p_message) {
		if (!this.write_protected)
        {
			System.out.println("Status: " + p_message);
        }
	}

	@Override
	public void set_layer(String p_layer_name) {
		if (!this.write_protected)
        {
			System.out.println("Set to layer: " + p_layer_name);
        }
	}

	@Override
	public void set_interactive_autoroute_info(int p_found, int p_not_found, int p_items_to_go) {
		Integer found = p_found;
        Integer failed = p_not_found;
        Integer items_to_go = p_items_to_go;
        System.out.println(resources.getString("to_route") + " " + items_to_go.toString());
        System.out.println(resources.getString("found") + " " + found.toString() + ", "
                + resources.getString("failed") + " " + failed.toString());
	}

	@Override
	public void set_batch_autoroute_info(int p_items_to_go, int p_routed, int p_ripped, int p_failed) {
		Integer ripped = p_ripped;
        Integer routed = p_routed;
        Integer items_to_go = p_items_to_go;
        Integer failed = p_failed;
        System.out.println( resources.getString("to_route") + " " + items_to_go.toString() + ", "
                + resources.getString("routed") + " " + routed.toString() + ", ");
        System.out.println(resources.getString("ripped") + " " + ripped.toString() + ", "
                + resources.getString("failed") + " " + failed.toString());
	}

	@Override
	public void set_batch_fanout_info(int p_pass_no, int p_components_to_go) {
		Integer components_to_go = p_components_to_go;
        Integer pass_no = new  Integer(p_pass_no);
        System.out.println(resources.getString("fanout_pass") + " " +  pass_no.toString() + ": ");
        System.out.println(resources.getString("still") + " " 
                + components_to_go.toString() + " " + resources.getString("components"));
	}

	@Override
	public void set_post_route_info(int p_via_count, double p_trace_length) {
		Integer via_count = p_via_count;
		System.out.println(resources.getString("via_count") + " " + via_count.toString());
		System.out.println(resources.getString("trace_length") + " " + this.number_format.format(p_trace_length));
	}

	@Override
	public void set_target_layer(String p_layer_name) {
		if (!(p_layer_name.equals(prev_target_layer_name) || this.write_protected))
        {
			System.out.println(target_layer_string +  p_layer_name);
            prev_target_layer_name = p_layer_name;
        }
	}

	@Override
	public void set_mouse_position(FloatPoint p_pos) {
	}

	@Override
	public void clear_add_field() {
	}

	@Override
	public void clear() {
	}

	@Override
	public void set_write_protected(boolean p_value) {
		write_protected = p_value;
	}

	private final java.util.ResourceBundle resources;
    private final String target_layer_string;
    static private final String empty_string = "            ";
    
    private String prev_target_layer_name = empty_string;
    private boolean write_protected = false;
    
    /** The number format for displaying the trace length */
    private final java.text.NumberFormat number_format;
}
