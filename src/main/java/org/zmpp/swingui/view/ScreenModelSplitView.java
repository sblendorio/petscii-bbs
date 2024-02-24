package org.zmpp.swingui.view;

import org.zmpp.ExecutionControl;
import org.zmpp.vm.MachineRunState;
import org.zmpp.windowing.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.logging.Logger;

public class ScreenModelSplitView extends JLayeredPane implements ScreenModelListener {
    private static final int MARGIN_HORIZONTAL = 5;
    private static final int MARGIN_VERTICAL = 5;
    private static final Logger LOG = Logger.getLogger("org.zmpp.ui");

    private int editStart;

    private ExecutionControl executionControl;

    private BufferedScreenModel screenModel;

    private MachineRunState currentRunState;

    private JViewport lowerViewport;

    private final TextWindowView lower = new TextWindowView(this);
    private final TextGridView upper = new TextGridView(this);
    private MainViewListener listener;
    private final ScreenModelLayout layout = new ScreenModelLayout();
    private final FontSelector fontSelector = new FontSelector();

    private final DisplaySettings displaySettings;

    private Timer currentTimer;

    public ScreenModelSplitView(DisplaySettings displaySettings) {
        this.displaySettings = displaySettings;
        initLayout();
        createUpperView();
        createLowerView();
        split(0);
    }

    public int getNumUpperRows() {
        return this.upper.getNumRows();
    }

    public int getDefaultBackground() {
        return this.displaySettings.getDefaultBackground();
    }

    public int getDefaultForeground() {
        return this.displaySettings.getDefaultForeground();
    }

    public BufferedScreenModel getScreenModel() {
        return this.screenModel;
    }

    private void initLayout() {
        setOpaque(true);
        setPreferredSize(new Dimension(640, 480));
        this.fontSelector.setFixedFont(this.displaySettings.getFixedFont());
        this.fontSelector.setStandardFont(this.displaySettings.getStdFont());
        this.layout.setFontSelector(this.fontSelector);
        setLayout(this.layout);
    }

    private void createUpperView() {
        Border upperBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

        this.upper.setBorder(upperBorder);
        add(this.upper, JLayeredPane.PALETTE_LAYER);
    }

    private void createLowerView() {
        this.lower.setEditable(true);
        this.lower.setEnabled(true);
        this.lower.setBackground(getBackgroundColor(getDefaultBackground()));
        this.lower.setForeground(getForegroundColor(getDefaultForeground()));
        this.lowerViewport = new JViewport();
        this.lowerViewport.setView(this.lower);
        this.lowerViewport.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ScreenModelSplitView.this.viewSizeChanged();
            }
        });
        this.lower.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                ScreenModelSplitView.this.viewSizeChanged();
            }
        });

        Border lowerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

        this.lower.setBorder(lowerBorder);
        add(this.lowerViewport, JLayeredPane.DEFAULT_LAYER);
        installLowerHandlers();
    }

    private void installLowerHandlers() {
        ScreenModelViewInputHandler inputHandler = new ScreenModelViewInputHandler(this);
        this.lower.addKeyListener(inputHandler);
        this.lower.getCaret().addChangeListener(inputHandler);
    }

    private Color getBackgroundColor(int screenModelColor) {
        return (this.executionControl != null) ? ColorTranslator.getInstance().translate(screenModelColor, this.executionControl.getDefaultBackground()) : ColorTranslator.getInstance().translate(screenModelColor);
    }

    private Color getForegroundColor(int screenModelColor) {
        return (this.executionControl != null) ? ColorTranslator.getInstance().translate(screenModelColor, this.executionControl.getDefaultForeground()) : ColorTranslator.getInstance().translate(screenModelColor);
    }

    TextWindowView getLower() {
        return this.lower;
    }

    int getEditStart() {
        return this.editStart;
    }

    private void updateEditStart() {
        LOG.info("# OF LEFTOVER CHARS: " + getNumLeftOverChars());
        this.editStart = this.lower.getDocument().getLength() - getNumLeftOverChars();
    }

    private int getNumLeftOverChars() {
        return this.currentRunState.getNumLeftOverChars();
    }

    boolean isReadChar() {
        return this.currentRunState != null && this.currentRunState.isReadChar();
    }

    boolean isReadLine() {
        return this.currentRunState != null && this.currentRunState.isReadLine();
    }

    ExecutionControl getExecutionControl() {
        return this.executionControl;
    }

    private void stopCurrentTimer() {
        if (this.currentTimer != null) {
            this.currentTimer.stop();
            this.currentTimer = null;
        }
    }

    protected String getCurrentInput() {
        Document doc = this.lower.getDocument();
        String input = null;
        try {
            input = doc.getText(this.editStart, doc.getLength() - this.editStart);
        } catch (Exception ex) {
            LOG.throwing("Document", "getText", ex);
        }
        return input;
    }

    private void startNewInterruptTimer(final MachineRunState runState) {
        this.currentTimer = new Timer(runState.getTime() * 100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("TIMED METHOD, active window: " + ScreenModelSplitView.this.screenModel.getActiveWindow());
                String currentInput = ScreenModelSplitView.this.getCurrentInput();
                if (currentInput != null) {
                    ScreenModelSplitView.this.executionControl.setTextToInputBuffer(currentInput);
                }

                ScreenModelSplitView.this.screenModel.setBufferMode(false);
                char result = ScreenModelSplitView.this.executionControl.callInterrupt(runState.getRoutine());

                System.out.println("RESULT OF TIMED: " + result);
                if (result == '\001') {
                    ScreenModelSplitView.this.currentTimer.stop();
                    ScreenModelSplitView.this.pressEnterKey();
                } else if (result == '\000') {
                    ScreenModelSplitView.this.updateEditStart();
                }
                ScreenModelSplitView.this.screenModel.setBufferMode(true);
            }
        });
        this.currentTimer.start();
    }

    private void pressEnterKey() {
        KeyEvent enterKeyEvent = new KeyEvent(this.lower, 1, System.currentTimeMillis(), 0, 10, '\0');

        for (KeyListener l : this.lower.getKeyListeners()) {
            l.keyReleased(enterKeyEvent);
        }
    }

    public void setCurrentRunState(MachineRunState runState) {
        stopCurrentTimer();
        if (runState.getRoutine() > '\000') {
            LOG.info("readchar: " + runState.isReadChar() + " time: " + runState.getTime() + " routine: " + runState.getRoutine());
            startNewInterruptTimer(runState);
        }
        this.currentRunState = runState;
        viewCursor(runState.isWaitingForInput());
    }

    public void initUI(BufferedScreenModel screenModel, ExecutionControl control) {
        this.executionControl = control;
        this.executionControl.setDefaultColors(getDefaultBackground(), getDefaultForeground());

        this.screenModel = screenModel;
        screenModel.addScreenModelListener(this);
        setSizes();
        this.lower.setCurrentStyle(screenModel.getBottomAnnotation());
    }

    private void setSizes() {
        int componentWidth = getWidth();
        int componentHeight = getHeight();
        int charWidth = getFixedFontWidth();
        int charHeight = getFixedFontHeight();
        int numCharsPerRow = componentWidth / charWidth;
        int numRows = componentHeight / charHeight;
        this.screenModel.setNumCharsPerRow(numCharsPerRow);
        LOG.info("Char width: " + charWidth + " component width: " + componentWidth + " # chars/row: " + numCharsPerRow + " char height: " + charHeight + " # rows: " + numRows);

        this.upper.setGridSize(numRows, numCharsPerRow);
        this.executionControl.resizeScreen(numRows, numCharsPerRow);
    }

    public void addMainViewListener(MainViewListener l) {
        this.listener = l;
    }

    public void addMouseWheelListener(MouseWheelListener l) {
        this.lower.addMouseWheelListener(l);
    }

    public void scroll(int value) {
        this.lower.setLocation(0, value);
        validate();
        repaint();
    }

    private void split(int numRowsUpper) {
        this.layout.setNumRowsUpper(numRowsUpper);

        if (this.executionControl != null && this.executionControl.getVersion() == 3 && this.upper != null && this.screenModel != null) {
            clearUpper();
        }
    }

    private void viewSizeChanged() {
        this.listener.viewDimensionsChanged(this.lower.getHeight(), this.lowerViewport.getHeight(), this.lower.getY());
    }

    public int getFixedFontWidth() {
        return this.upper.getGraphics().getFontMetrics(getRomanFixedFont()).charWidth('0');
    }

    public int getFixedFontHeight() {
        return this.upper.getGraphics().getFontMetrics(getRomanFixedFont()).getHeight();
    }

    protected Font getRomanFixedFont() {
        return this.fontSelector.getFont('\004', 0);
    }

    protected Font getRomanStdFont() {
        return this.fontSelector.getFont('\001', 0);
    }

    protected Font getFont(TextAnnotation annotation) {
        return this.fontSelector.getFont(annotation);
    }

    public void screenModelUpdated(ScreenModel screenModel) {
        List<AnnotatedText> text = this.screenModel.getLowerBuffer();
        for (AnnotatedText segment : text) {
            this.lower.append(segment);
        }

        this.lower.setCurrentStyle(screenModel.getBottomAnnotation());
    }

    public void topWindowUpdated(int cursorx, int cursory, AnnotatedCharacter c) {
        this.upper.setCharacter(cursory, cursorx, c);
        repaint();
    }

    public void screenSplit(int linesUpperWindow) {
        split(linesUpperWindow);
    }

    public void topWindowCursorMoving(int line, int column) {
        if (this.currentRunState != null && this.currentRunState.isReadChar() && this.screenModel.getActiveWindow() == 1) {
            this.upper.viewCursor(false);
        }
    }

    public void windowErased(int window) {
        if (window == -1) {
            clearAll();
        } else if (window == 0) {
            this.lower.clear(this.screenModel.getBackground(), this.screenModel.getForeground());
        } else if (window == 1) {
            clearUpper();
        } else {
            throw new UnsupportedOperationException("No support for erasing window: " + window);
        }
    }

    private void clearUpper() {
        this.upper.clear(this.screenModel.getBackground());
    }

    private void clearAll() {
        this.lower.clear(this.screenModel.getBackground(), this.screenModel.getForeground());
        clearUpper();
    }

    private void viewCursor(final boolean flag) {
        runInUIThread(new Runnable() {
            public void run() {
                if (ScreenModelSplitView.this.screenModel.getActiveWindow() == 0) {
                    ScreenModelSplitView.this.viewCursorLower(flag);
                } else if (ScreenModelSplitView.this.screenModel.getActiveWindow() == 1) {
                    ScreenModelSplitView.this.upper.viewCursor(flag);
                }
            }
        });
    }

    private void viewCursorLower(boolean flag) {
        if (flag) {

            updateEditStart();
            this.lower.setCaretPosition(getEditStart() + getNumLeftOverChars());
            this.lower.requestFocusInWindow();
        }
    }

    private void runInUIThread(Runnable runnable) {
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


    public interface MainViewListener {
        void viewDimensionsChanged(int param1Int1, int param1Int2, int param1Int3);
    }
}
