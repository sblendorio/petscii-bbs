package org.zmpp.swingui.view;

import org.zmpp.ExecutionControl;
import org.zmpp.base.Memory;
import org.zmpp.vm.InvalidStoryException;
import org.zmpp.vm.Machine;
import org.zmpp.vm.MachineFactory;
import org.zmpp.vm.MachineRunState;
import org.zmpp.windowing.BufferedScreenModel;
import org.zmpp.windowing.ScreenModel;
import org.zmpp.windowing.StatusLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ScreenModelView extends JComponent implements AdjustmentListener, ScreenModelSplitView.MainViewListener, MouseWheelListener, BufferedScreenModel.StatusLineListener {
    private static final Logger LOG = Logger.getLogger("org.zmpp");
    private final ScreenModelSplitView mainView;
    private final BufferedScreenModel screenModel = new BufferedScreenModel();
    private final JScrollBar scrollbar;
    private ExecutionControl executionControl;
    private final Set<GameLifeCycleListener> lifeCycleListeners = new HashSet<GameLifeCycleListener>();

    private JPanel statusPanel;
    private final JLabel objectDescLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel(" ");

    public ScreenModelView(DisplaySettings displaySettings) {
        this.mainView = new ScreenModelSplitView(displaySettings);
        setLayout(new BorderLayout());
        this.mainView.setPreferredSize(new Dimension(640, 480));
        add(this.mainView, "Center");
        this.scrollbar = new JScrollBar();
        this.scrollbar.addAdjustmentListener(this);
        this.scrollbar.addMouseWheelListener(this);
        this.mainView.addMouseWheelListener(this);
        add(this.scrollbar, "East");
        this.mainView.addMainViewListener(this);

        this.screenModel.addStatusLineListener(this);
        add(createStatusPanel(), "North");
    }

    public void addGameLoadedListener(GameLifeCycleListener l) {
        this.lifeCycleListeners.add(l);
    }

    public Machine getMachine() {
        return this.executionControl.getMachine();
    }

    private JPanel createStatusPanel() {
        this.statusPanel = new JPanel(new GridLayout(1, 2));
        JPanel leftPanel = new JPanel(new FlowLayout(0));
        JPanel rightPanel = new JPanel(new FlowLayout(2));
        this.statusPanel.add(leftPanel);
        this.statusPanel.add(rightPanel);
        leftPanel.add(this.objectDescLabel);
        rightPanel.add(this.statusLabel);
        this.statusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return this.statusPanel;
    }

    public void viewDimensionsChanged(int viewHeight, int viewportHeight, int currentViewPos) {
        this.scrollbar.setMinimum(0);
        this.scrollbar.setMaximum(viewHeight);
        this.scrollbar.setValue(mapViewPosToScrollPos(currentViewPos));
        this.scrollbar.setVisibleAmount(viewportHeight);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getValueIsAdjusting()) {
            scrollToScrollbarPos();
        }
    }

    private int mapScrollPosToViewPos(int scrollPos) {
        return -scrollPos;
    }

    private int mapViewPosToScrollPos(int viewPos) {
        return -viewPos;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        int units = e.getUnitsToScroll();
        this.scrollbar.setValue(this.scrollbar.getValue() + units);
        scrollToScrollbarPos();
    }

    private void scrollToScrollbarPos() {
        this.mainView.scroll(mapScrollPosToViewPos(this.scrollbar.getValue()));
    }

    public void statusLineUpdated(String objectDescription, String status) {
        this.objectDescLabel.setText(objectDescription);
        this.statusLabel.setText(status);
    }

    public void startGame(MachineFactory.MachineInitStruct initStruct) throws IOException, InvalidStoryException {
        initStruct.screenModel = this.screenModel;
        initStruct.statusLine = this.screenModel;

        if (isVisible()) {
            this.executionControl = new ExecutionControl(initStruct);
            initUI(initStruct);
            notifyGameInitialized();
            MachineRunState runState = this.executionControl.run();
            LOG.info("PAUSING WITH STATE: " + runState);
            this.mainView.setCurrentRunState(runState);
        }
    }

    private void notifyGameInitialized() {
        for (GameLifeCycleListener l : this.lifeCycleListeners) {
            l.gameInitialized();
        }
    }

    private void initUI(MachineFactory.MachineInitStruct initStruct) {
        ((BufferedScreenModel) initStruct.screenModel).init(this.executionControl.getMachine(), this.executionControl.getZsciiEncoding());


        int version = this.executionControl.getVersion();
        this.statusPanel.setVisible((version <= 3));
        this.mainView.initUI(this.screenModel, this.executionControl);
    }
}
