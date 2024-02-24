package org.zmpp.swingui.app;

import org.zmpp.io.IOSystem;
import org.zmpp.media.Resources;
import org.zmpp.media.StoryMetadata;
import org.zmpp.swingui.view.DisplaySettings;
import org.zmpp.swingui.view.FileSaveGameDataStore;
import org.zmpp.swingui.view.GameLifeCycleListener;
import org.zmpp.swingui.view.ScreenModelView;
import org.zmpp.vm.MachineFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.prefs.Preferences;

public class ZmppFrame extends JFrame implements GameLifeCycleListener, IOSystem {
    public static final String STD_FONT_NAME = "Times";
    public static final String FIXED_FONT_NAME = "Courier New";
    public static final int STD_FONT_SIZE = 14;
    public static final int FIXED_FONT_SIZE = 14;
    public static final int DEFAULT_FOREGROUND = 2;
    public static final int DEFAULT_BACKGROUND = 9;
    private final JMenuBar menubar = new JMenuBar();

    private JMenu fileMenu;

    private JMenu helpMenu;

    private JMenuItem aboutGameItem;
    private ScreenModelView screenModelView;
    private final DisplaySettings displaySettings;
    private final Preferences preferences;

    public ZmppFrame() {
        super(Main.APP_NAME);
        this.preferences = Preferences.userNodeForPackage(ZmppFrame.class);
        this.displaySettings = createDisplaySettings(this.preferences);
        setDefaultCloseOperation(3);
        setupUI();
        pack();
    }

    private static String getMessage(String property) {
        return Main.getMessage(property);
    }

    public static void openStoryFile() {
        openStoryFile(null);
    }

    private static void openStoryFile(ZmppFrame frame) {
        if (frame != null) frame.dispose();
        try {
            runInEventDispatchThread(new Runnable() {
                public void run() {
                    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));

                    fileChooser.setDialogTitle(ZmppFrame.getMessage("dialog.open.msg"));
                    if (fileChooser.showOpenDialog(null) == 0) {
                        final File storyfile = fileChooser.getSelectedFile();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ZmppFrame.runStoryFile(storyfile);
                            }
                        });
                    }
                }
            });
        } catch (Exception ignore) {
        }
    }

    private static void runStoryFile(File storyFile) {
        ZmppFrame zmppFrame = new ZmppFrame();
        zmppFrame.setVisible(true);
        try {
            MachineFactory.MachineInitStruct initStruct = new MachineFactory.MachineInitStruct();
            if (storyFile.getName().endsWith("zblorb")) {
                initStruct.blorbFile = new FileInputStream(storyFile);
            } else {
                initStruct.storyFile = new FileInputStream(storyFile);
            }
            initStruct.nativeImageFactory = new Main.AwtImageFactory();
            initStruct.saveGameDataStore = new FileSaveGameDataStore(zmppFrame);
            initStruct.ioSystem = zmppFrame;
            zmppFrame.getScreenModelView().startGame(initStruct);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void runInEventDispatchThread(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public ScreenModelView getScreenModelView() {
        return this.screenModelView;
    }

    private void setupUI() {
        this.screenModelView = new ScreenModelView(this.displaySettings);
        this.screenModelView.addGameLoadedListener(this);
        getContentPane().add(this.screenModelView);
        setJMenuBar(this.menubar);
        createFileMenu();
        setupMenuBar();
        addAboutGameMenuItem();
    }

    public void gameInitialized() {
        StoryMetadata storyinfo = getStoryInfo();
        if (storyinfo != null) {
            setTitle(getMessage("app.name") + " - " + storyinfo.getTitle() + " (" + storyinfo.getAuthor() + ")");
        }

        this.aboutGameItem.setEnabled((storyinfo != null));
    }

    private void createFileMenu() {
        this.fileMenu = new JMenu(getMessage("menu.file.name"));
        this.fileMenu.setMnemonic(getMessage("menu.file.mnemonic").charAt(0));
        this.menubar.add(this.fileMenu);
        JMenuItem openItem = new JMenuItem(getMessage("menu.file.open.name"));
        this.fileMenu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZmppFrame.openStoryFile(ZmppFrame.this);
            }
        });
    }

    private void addAboutGameMenuItem() {
        this.aboutGameItem = new JMenuItem(getMessage("menu.help.aboutgame.name"));
        this.helpMenu.add(this.aboutGameItem);
        this.aboutGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZmppFrame.this.aboutGame();
            }
        });
        this.aboutGameItem.setEnabled(false);
    }

    private void setupMenuBar() {
        JMenuItem exitItem = new JMenuItem(getMessage("menu.file.quit.name"));
        exitItem.setMnemonic(getMessage("menu.file.quit.mnemonic").charAt(0));
        this.fileMenu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZmppFrame.this.quit();
            }
        });
        JMenu editMenu = new JMenu(getMessage("menu.edit.name"));
        this.menubar.add(editMenu);
        editMenu.setMnemonic(getMessage("menu.edit.mnemonic").charAt(0));
        JMenuItem preferencesItem = new JMenuItem(getMessage("menu.edit.prefs.name"));

        preferencesItem.setMnemonic(getMessage("menu.edit.prefs.mnemonic").charAt(0));
        editMenu.add(preferencesItem);
        preferencesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZmppFrame.this.editPreferences();
            }
        });
        this.helpMenu = new JMenu(getMessage("menu.help.name"));
        this.menubar.add(this.helpMenu);
        this.helpMenu.setMnemonic(getMessage("menu.help.mnemonic").charAt(0));

        JMenuItem aboutItem = new JMenuItem(getMessage("menu.help.about.name"));
        aboutItem.setMnemonic(getMessage("menu.help.about.mnemonic").charAt(0));
        this.helpMenu.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZmppFrame.this.about();
            }
        });
    }

    private StoryMetadata getStoryInfo() {
        Resources resources = this.screenModelView.getMachine().getResources();
        if (resources != null && resources.getMetadata() != null) {
            return resources.getMetadata().getStoryInfo();
        }
        return null;
    }

    public Writer getTranscriptWriter() {
        File currentdir = new File(System.getProperty("user.dir"));
        JFileChooser fileChooser = new JFileChooser(currentdir);
        fileChooser.setDialogTitle(getMessage("dialog.settranscript.title"));
        if (fileChooser.showSaveDialog(this) == 0) {
            try {
                return new FileWriter(fileChooser.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public Reader getInputStreamReader() {
        File currentdir = new File(System.getProperty("user.dir"));
        JFileChooser fileChooser = new JFileChooser(currentdir);
        fileChooser.setDialogTitle(getMessage("dialog.setinput.title"));
        if (fileChooser.showOpenDialog(this) == 0) {
            try {
                return new FileReader(fileChooser.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void about() {
        JOptionPane.showMessageDialog(this, getMessage("app.name") + getMessage("dialog.about.msg"), getMessage("dialog.about.title"), 1);
    }

    public void aboutGame() {
        GameInfoDialog dialog = new GameInfoDialog(this, this.screenModelView.getMachine().getResources());

        dialog.setVisible(true);
    }

    public void quit() {
        System.exit(0);
    }

    public void editPreferences() {
        PreferencesDialog dialog = new PreferencesDialog(this, this.preferences, this.displaySettings);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private DisplaySettings createDisplaySettings(Preferences preferences) {
        String stdFontName = preferences.get("stdfontname", "Times");
        int stdFontSize = preferences.getInt("stdfontsize", 14);
        String fixedFontName = preferences.get("fixedfontname", "Courier New");

        int fixedFontSize = preferences.getInt("fixedfontsize", 14);

        int defaultforeground = preferences.getInt("defaultforeground", 2);

        int defaultbackground = preferences.getInt("defaultbackground", 9);

        boolean antialias = preferences.getBoolean("antialias", true);

        return new DisplaySettings(new Font(stdFontName, 0, stdFontSize), new Font(fixedFontName, 0, fixedFontSize), defaultbackground, defaultforeground, antialias);
    }
}


/* Location:              /Users/sblendorio/Downloads/zmpp-1.5-preview1-bin/zmpp-1.5-preview1.jar!/org/zmpp/swingui/app/ZmppFrame.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */