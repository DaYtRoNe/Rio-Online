package gui;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.JPanel;

/**
 *
 * @author daytr
 */
public class RoundedPanel extends JPanel{

    public RoundedPanel() {
        init();
    }
    
    private void init() {
        this.putClientProperty(FlatClientProperties.STYLE, "arc:23");
    }
}
