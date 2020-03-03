/*
 * $Id: LineEditorImpl.java,v 1.7 2006/05/30 17:54:44 weiju Exp $
 * 
 * Created on 2005/11/07
 * Copyright 2005-2006 by Wei-ju Wu
 *
 * This file is part of The Z-machine Preservation Project (ZMPP).
 *
 * ZMPP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZMPP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZMPP; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.zmpp.swingui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.vm.StoryFileHeader;

public class LineEditorImpl implements LineEditor, KeyListener, MouseListener,
MouseMotionListener {

  private boolean inputmode;
  private List<Short> editbuffer;
  private StoryFileHeader fileheader;
  private ZsciiEncoding encoding;
  
  private MouseEvent lastMouseEvent;
  
  public LineEditorImpl(StoryFileHeader fileheader, ZsciiEncoding encoding) {
  
    this.fileheader = fileheader;
    this.encoding = encoding;
    editbuffer = new LinkedList<Short>();
  }
  
  public void setInputMode(boolean flag, boolean flushbuffer) {
    
    synchronized (editbuffer) {
      
      inputmode = flag;
      if (flushbuffer) {
        editbuffer.clear();
      }
      editbuffer.notifyAll();
    }
  }

  public void cancelInput() {
  
    synchronized (editbuffer) {
  
      editbuffer.add(ZsciiEncoding.NULL);
      inputmode = false;
      editbuffer.notifyAll();
    }
  }
  
  public short nextZsciiChar() {
    
    short zsciiChar = 0;
    synchronized (editbuffer) {
      
      while (editbuffer.size() == 0) {

        try {
          
          editbuffer.wait();
          
        } catch (Exception ex) { }
      }
      zsciiChar = editbuffer.remove(0);
      editbuffer.notifyAll();
    }
    return zsciiChar;
  }
  
  public boolean isInputMode() {
    
    synchronized (editbuffer) {
      return inputmode;
    }
  }

  /**
   * {@inheritDoc}
   */
  public MouseEvent getLastMouseEvent() {
    
    return lastMouseEvent;
  }
  
  public void keyPressed(KeyEvent e) {

    switch (e.getKeyCode()) {
      case KeyEvent.VK_BACK_SPACE:
      case KeyEvent.VK_DELETE:
        addToBuffer(ZsciiEncoding.DELETE);
        break;
      case KeyEvent.VK_SPACE:
        addToBuffer((short) ' ');
        break;
    }
  }
  
  public void keyTyped(KeyEvent e) {
  
    char c = e.getKeyChar();
    
    if (encoding.isConvertableToZscii(c)
        && !handledInKeyPressed(c)) {
      
      addToBuffer(encoding.getZsciiChar(c));
    }
  }
  
  public void keyReleased(KeyEvent e) {
    
    switch (e.getKeyCode()) {
      case KeyEvent.VK_UP:
        addToBuffer(ZsciiEncoding.CURSOR_UP);
        break;
      case KeyEvent.VK_DOWN:
        addToBuffer(ZsciiEncoding.CURSOR_DOWN);
        break;
      case KeyEvent.VK_LEFT:
        addToBuffer(ZsciiEncoding.CURSOR_LEFT);
        break;
      case KeyEvent.VK_RIGHT:
        addToBuffer(ZsciiEncoding.CURSOR_RIGHT);
        break;
      case KeyEvent.VK_ESCAPE:
        addToBuffer(ZsciiEncoding.ESCAPE);
        break;
    }
  }

  private void addToBuffer(short zsciiChar) {
    
    if (isInputMode()) {
      
      synchronized (editbuffer) {
      
        editbuffer.add(zsciiChar);
        editbuffer.notifyAll();
      }
    }
  }
  
  private boolean handledInKeyPressed(char c) {
    
    return c == ' ' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE;
  }
  
  public void mouseClicked(MouseEvent e) {
    
    //System.out.printf("mouseClicked(),  use mouse: %b, x: %d y: %d\n", useMouse(),
    //    e.getX(), e.getY());
    // Only if mouse is used
    if (useMouse()) {
      
      fileheader.setMouseCoordinates(e.getX() + 1, e.getY() + 1);
      
      // Store single clicks and double clicks with different codes
      addToBuffer((short) ((e.getClickCount() == 1) ?
          ZsciiEncoding.MOUSE_SINGLE_CLICK :
          ZsciiEncoding.MOUSE_DOUBLE_CLICK)); 
    }
    lastMouseEvent = e;
  }
  
  /**
   * Returns true if the game should recognize the mouse.
   * 
   * @return the mouse
   */
  private boolean useMouse() {
  
    return fileheader.isEnabled(StoryFileHeader.Attribute.USE_MOUSE)
      || fileheader.getVersion() == 6;
  }
  
  public void mouseEntered(MouseEvent e) {
    
    lastMouseEvent = e;
  }
  public void mouseExited(MouseEvent e) {
    
    lastMouseEvent = e;
  }
  public void mousePressed(MouseEvent e) { 
    
    lastMouseEvent = e;
  }
  public void mouseReleased(MouseEvent e) {
    
    lastMouseEvent = e;
  }
  
  public void mouseMoved(MouseEvent e) {
    
    lastMouseEvent = e;
  }
  
  public void mouseDragged(MouseEvent e) {
    
    lastMouseEvent = e;
  }  
}
