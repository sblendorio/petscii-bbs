package org.zmpp.swingui.app;

import org.zmpp.swingui.view.DisplaySettings;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesDialog extends JDialog implements ActionListener {
    private static final ColorItem[] colors = new ColorItem[]{new ColorItem(-1000, ""), new ColorItem(2, getMessage("caption.black")), new ColorItem(3, getMessage("caption.red")), new ColorItem(4, getMessage("caption.green")), new ColorItem(5, getMessage("caption.yellow")), new ColorItem(6, getMessage("caption.blue")), new ColorItem(7, getMessage("caption.magenta")), new ColorItem(8, getMessage("caption.cyan")), new ColorItem(9, getMessage("caption.white")), new ColorItem(10, getMessage("caption.darkgray"))};
    private static final long serialVersionUID = 1L;
    private final JSpinner stdfontSpinner;
    private final JSpinner fixedfontSpinner;
    private final JComboBox foregroundCB;
    private final JComboBox backgroundCB;
    private final JCheckBox antialiasCB;
    private final Preferences preferences;
    private final DisplaySettings settings;
    private final String stdFontName;
    private final String fixedFontName;

    public PreferencesDialog(JFrame parent, Preferences preferences, DisplaySettings settings) {
        super(parent, getMessage("dialog.prefs.title"), true);
        this.preferences = preferences;
        this.settings = settings;


        GridLayout grid = new GridLayout(5, 2);
        grid.setVgap(3);
        grid.setHgap(3);

        JPanel mainpanel = new JPanel(grid);

        JLabel stdfontLabel = new JLabel(getMessage("caption.stdfont.size"));
        mainpanel.add(stdfontLabel);
        this.stdfontSpinner = new JSpinner();
        this.stdfontSpinner.setValue(Integer.valueOf(settings.getStdFont().getSize()));
        mainpanel.add(this.stdfontSpinner);

        JLabel fixedfontLabel = new JLabel(getMessage("caption.fixedfont.size"));
        mainpanel.add(fixedfontLabel);
        this.fixedfontSpinner = new JSpinner();
        this.fixedfontSpinner.setValue(Integer.valueOf(settings.getFixedFont().getSize()));
        mainpanel.add(this.fixedfontSpinner);

        JLabel backgroundLabel = new JLabel(getMessage("caption.default.background"));

        mainpanel.add(backgroundLabel);
        this.backgroundCB = new JComboBox<ColorItem>(colors);
        mainpanel.add(this.backgroundCB);
        preselect(this.backgroundCB, settings.getDefaultBackground());

        JLabel foregroundLabel = new JLabel(getMessage("caption.default.foreground"));

        mainpanel.add(foregroundLabel);
        this.foregroundCB = new JComboBox<ColorItem>(colors);
        mainpanel.add(this.foregroundCB);
        preselect(this.foregroundCB, settings.getDefaultForeground());

        JLabel antialiasLabel = new JLabel(getMessage("caption.antialias"));
        mainpanel.add(antialiasLabel);
        this.antialiasCB = new JCheckBox();
        this.antialiasCB.setSelected(settings.getAntialias());
        mainpanel.add(this.antialiasCB);

        Box lowpanel = new Box(1);
        lowpanel.add(new JSeparator());
        JPanel buttonpanel = new JPanel(new FlowLayout(2));
        lowpanel.add(buttonpanel);

        JButton okbutton = new JButton(getMessage("caption.ok"));
        JButton cancelbutton = new JButton(getMessage("caption.cancel"));
        getRootPane().setDefaultButton(okbutton);
        buttonpanel.add(okbutton);
        buttonpanel.add(cancelbutton);
        okbutton.addActionListener(this);
        cancelbutton.addActionListener(this);

        getContentPane().add(mainpanel, "North");
        getContentPane().add(new JLabel(getMessage("caption.restarttochange")), "Center");

        getContentPane().add(lowpanel, "South");

        Border border = BorderFactory.createEmptyBorder(5, 5, 5, 3);
        ((JPanel) getContentPane()).setBorder(border);
        ((BorderLayout) getContentPane().getLayout()).setVgap(5);

        pack();

        this.stdFontName = settings.getStdFont().getFontName();
        this.fixedFontName = settings.getFixedFont().getFontName();
    }

    private static String getMessage(String key) {
        return Main.getMessage(key);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(getMessage("caption.ok"))) {


            int stdfontsize = Integer.valueOf(this.stdfontSpinner.getValue().toString()).intValue();
            int fixedfontsize = Integer.valueOf(this.fixedfontSpinner.getValue().toString()).intValue();
            int bgcolor = ((ColorItem) this.backgroundCB.getSelectedItem()).color;
            int fgcolor = ((ColorItem) this.foregroundCB.getSelectedItem()).color;
            boolean antialias = this.antialiasCB.isSelected();

            this.preferences.put("stdfontsize", String.valueOf(stdfontsize));
            this.preferences.put("fixedfontsize", String.valueOf(fixedfontsize));
            this.preferences.put("defaultbackground", String.valueOf(bgcolor));
            this.preferences.put("defaultforeground", String.valueOf(fgcolor));
            this.preferences.put("antialias", antialias ? "true" : "false");
            this.preferences.put("stdfontname", this.stdFontName);
            this.preferences.put("fixedfontname", this.fixedFontName);
            Font stdFont = new Font("Times", 0, stdfontsize);

            Font fixedFont = new Font("Courier New", 0, fixedfontsize);
            this.settings.setSettings(stdFont, fixedFont, bgcolor, fgcolor, antialias);
            try {
                this.preferences.flush();
            } catch (BackingStoreException ex) {
                ex.printStackTrace();
            }
        }
        dispose();
    }

    private void preselect(JComboBox combobox, int value) {
        for (int i = 0; i < colors.length; i++) {
            if ((colors[i]).color == value) {
                combobox.setSelectedItem(colors[i]);
                break;
            }
        }
    }

    static class ColorItem {
        int color;
        String name;

        public ColorItem(int colornum, String name) {
            this.color = colornum;
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}
