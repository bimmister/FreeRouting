package interactive;

import javax.swing.JLabel;

public interface IScreenMessages {
    
    /**
     * Sets the message in the status field.
     */
    public void set_status_message(String p_message);
    
    /**
     * Sets the displayed layer number on the screen.
     */
    public void set_layer(String p_layer_name);
    
    public void set_interactive_autoroute_info(int p_found, int p_not_found, int p_items_to_go);

    public void set_batch_autoroute_info(int p_items_to_go, int p_routed, int p_ripped, int p_failed);
    
    public void set_batch_fanout_info(int p_pass_no, int p_components_to_go);
    
    public void set_post_route_info( int p_via_count, double p_trace_length);
    
    /**
     * Sets the displayed layer of the nearest target item
     * in interactive routing.
     */
    public void set_target_layer(String p_layer_name);
    
    public void set_mouse_position(geometry.planar.FloatPoint p_pos);
    
    /**
     * Clears the additional field, which is among others used to
     * display the layer of the nearest target item.
     */
    public void clear_add_field();
    
    /**
     * Clears the status field and the additional field.
     */
    public void clear();
    
    /**
     * As long as write_protected is set to true, the set functions in this class will do nothing.
     */
    public void set_write_protected(boolean p_value);
}
