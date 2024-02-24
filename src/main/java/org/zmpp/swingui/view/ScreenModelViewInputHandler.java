package org.zmpp.swingui.view;

import org.zmpp.ExecutionControl;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

public class ScreenModelViewInputHandler implements KeyListener, ChangeListener {
    private static final Logger LOG = Logger.getLogger("org.zmpp.ui");
    private final ScreenModelSplitView view;
    private long lastConsumed;
    private final long TYPING_THRESHOLD = 200L;

    public ScreenModelViewInputHandler(ScreenModelSplitView view) {
        this.view = view;
    }

    public void keyTyped(KeyEvent e) {
        handleKeyEvent(e);
    }

    public void keyPressed(KeyEvent e) {
        handleKeyEvent(e);
    }

    public void keyReleased(KeyEvent e) {
        handleKeyEvent(e);
    }

    private void handleKeyEvent(KeyEvent e) {
        if (this.view.getScreenModel().getActiveWindow() == 0) {
            preventBottomWindowKeyActionIfNeeded(e);

        } else if (isReadChar()) {
            resumeWithInput(String.valueOf(e.getKeyChar()));
            consumeKeyEvent(e);
        }
    }

    private void consumeKeyEvent(KeyEvent e) {
        if (e != null) {
            e.consume();
            this.lastConsumed = e.getWhen();
        }
    }

    private boolean wasConsumed(KeyEvent e) {
        return (Math.abs(e.getWhen() - this.lastConsumed) < this.TYPING_THRESHOLD);
    }

    private void preventBottomWindowKeyActionIfNeeded(KeyEvent e) {
        if (wasConsumed(e)) {
            consumeKeyEvent(e);

            return;
        }
        if (isReadChar()) {
            resumeWithInput(String.valueOf(e.getKeyChar()));
            consumeKeyEvent(e);

            return;
        }
        if (e.getKeyCode() == 38) {
            consumeKeyEvent(e);
        }
        if (e.getKeyCode() == 40) {
            consumeKeyEvent(e);
        }
        setCaretToEditMarkIfNeeded(e);
        if (e.getKeyCode() == 10) {
            consumeKeyEvent(e);
            handleEnterKey(e);
        }
        if (atOrBeforeEditStart() && e.getKeyCode() == 8) {
            consumeKeyEvent(e);
        }
    }

    private void setCaretToEditMarkIfNeeded(KeyEvent e) {
        if (getLowerCaretPosition() <= getEditStart() && isPrintable(e)) {
            setLowerCaretPosition(getLowerDocument().getLength());
        }
    }

    private boolean isPrintable(KeyEvent e) {
        return (e.getKeyChar() != Character.MAX_VALUE && !isCommandDown(e));
    }

    private boolean isCommandDown(KeyEvent e) {
        int modifiers = e.getModifiers();
        boolean appleCmd = ((modifiers & 0x4) == 4);
        return (appleCmd || e.isControlDown());
    }

    private boolean atOrBeforeEditStart() {
        return (getLowerCaretPosition() <= getEditStart());
    }

    private void handleEnterKey(KeyEvent e) {
        if (isReadLine()) {
            Document doc = getLowerDocument();
            try {
                String input = this.view.getCurrentInput();
                LOG.info("ENTER PRESSED, input: [" + input + "]");
                doc.insertString(doc.getLength(), "\n", null);
                resumeWithInput(input);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
    }

    private ExecutionControl getExecutionControl() {
        return this.view.getExecutionControl();
    }

    private void resumeWithInput(String input) {
        this.view.setCurrentRunState(getExecutionControl().resumeWithInput(input));
    }

    private int getEditStart() {
        return this.view.getEditStart();
    }

    private boolean isReadChar() {
        return this.view.isReadChar();
    }

    private boolean isReadLine() {
        return this.view.isReadLine();
    }

    private Document getLowerDocument() {
        return this.view.getLower().getDocument();
    }

    private int getLowerCaretPosition() {
        return this.view.getLower().getCaretPosition();
    }

    private void setLowerCaretPosition(int position) {
        this.view.getLower().setCaretPosition(position);
    }
}
