package org.zmpp.swingui.app;

import org.zmpp.blorb.BlorbImage;
import org.zmpp.media.InformMetadata;
import org.zmpp.media.Resources;
import org.zmpp.media.StoryMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameInfoDialog extends JDialog {
    private static final int STD_WIDTH = 400;
    private static final long serialVersionUID = 1L;

    public GameInfoDialog(JFrame owner, Resources resources) {
        super(owner, Main.getMessage("caption.about") + " " + resources.getMetadata().getStoryInfo().getTitle());

        getContentPane().add(createInfoPanel(resources));
        getContentPane().add(createButtonPanel(), "South");
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel createPicturePanel(Resources resources, int coverartnum) {
        JPanel picpanel = new JPanel();
        if (coverartnum > 0) {
            BlorbImage blorbImage = (BlorbImage) resources.getImages().getResource(coverartnum);

            Main.AwtImage awtImage = (Main.AwtImage) blorbImage.getImage();
            JLabel label = new PictureLabel(awtImage.getImage());
            label.setPreferredSize(new Dimension(400, 400));
            picpanel.add(label);
        }
        return picpanel;
    }

    private JComponent createInfoPanel(Resources resources) {
        StoryMetadata storyinfo = resources.getMetadata().getStoryInfo();
        Box infopanel = Box.createVerticalBox();
        infopanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        JComponent panel = infopanel;


        int coverartnum = getCoverartNum(resources);
        if (coverartnum > 0 && resources.getImages().getNumResources() > 0) {
            Box wholepanel = Box.createHorizontalBox();
            wholepanel.add(createPicturePanel(resources, coverartnum));
            wholepanel.add(infopanel);
            panel = wholepanel;
        }

        infopanel.setAlignmentX(0.0F);
        infopanel.setPreferredSize(new Dimension(400, 400));

        List<JLabel> labels = new ArrayList<JLabel>();
        labels.add(new JLabel(storyinfo.getTitle()));

        if (storyinfo.getHeadline() != null) {
            labels.add(new JLabel(storyinfo.getHeadline()));
        }

        labels.add(new JLabel(storyinfo.getAuthor() + " (" + storyinfo.getYear() + ")"));


        for (JLabel label : labels) {
            infopanel.add(label);
            label.setAlignmentX(0.0F);


            label.setFont(label.getFont().deriveFont(1));
        }

        infopanel.add(Box.createVerticalStrut(6));

        JTextArea descarea = new JTextArea(storyinfo.getDescription());
        descarea.setLineWrap(true);
        descarea.setWrapStyleWord(true);
        descarea.setEditable(false);
        Insets margins = new Insets(3, 3, 3, 3);
        descarea.setMargin(margins);
        descarea.setFont(labels.get(0).getFont().deriveFont(0));

        JScrollPane spane = new JScrollPane(descarea);
        spane.setHorizontalScrollBarPolicy(31);
        spane.setPreferredSize(new Dimension(400, 200));
        spane.setAlignmentX(0.0F);
        infopanel.add(spane);
        return panel;
    }


    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(2));
        JButton okButton = new JButton(Main.getMessage("caption.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameInfoDialog.this.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(okButton);
        buttonPanel.add(okButton);
        return buttonPanel;
    }

    private int getCoverartNum(Resources resources) {
        int coverartnum = resources.getCoverArtNum();
        InformMetadata metadata = resources.getMetadata();


        if (coverartnum <= 0) {
            coverartnum = metadata.getStoryInfo().getCoverPicture();
        }
        return coverartnum;
    }
}