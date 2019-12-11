/*
 * $Id: TextCursorImpl.java,v 1.5 2006/05/12 21:36:00 weiju Exp $
 * 
 * Created on 2006/01/23
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

import org.zmpp.vm.TextCursor;

/**
 * This class is the default implementation of the TextCursor interface.
 * It holds the cursor position in the current unit measure, which is
 * in V6 and characters in all other versions. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class TextCursorImpl implements TextCursor {

  /**
   * The current line.
   */
  private int line;
  
  /**
   * The current column.
   */
  private int column;
  
  /**
   * The window this cursor is contained in.
   */
  private CursorWindow window;
  
  /**
   * Constructor.
   */
  public TextCursorImpl(CursorWindow window) {
  
    this.line = 1;
    this.column = 1;
    this.window = window;
  }
  
  /**
   * {@inheritDoc}
   */
  public int getLine() { return line; }    

  /**
   * {@inheritDoc}
   */
  public int getColumn() { return column; }
  
  /**
   * {@inheritDoc}
   */
  public void setPosition(int line, int column) {
   
    setLine(line);
    setColumn(column);
    window.updateCursorCoordinates();
  }
  
  /**
   * {@inheritDoc}
   */
  public void setLine(int line) {
    
    this.line = (line > 0) ? line : 1;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setColumn(int column) {
    
    this.column = (column > 0) ? column : 1;
  }  
}
