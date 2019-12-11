/*
 * $Id: ScreenFont.java,v 1.1 2006/02/25 18:27:37 weiju Exp $
 * 
 * Created on 2006/02/25
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

import java.awt.Font;

/**
 * ScreenFont encapsulates all aspects of a font in the Z-machine, namely
 * the number, the style and the concrete font. This takes further load off
 * the central screen model classes.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ScreenFont {

  private int number;
  private int style;
  private Font font;
  private boolean reverseVideo;
  
  public ScreenFont(Font font, int number, int style) {
  
    this(font, number, style, false);
  }
  
  public ScreenFont(Font font, int number, int style, boolean reverseVideo) {
    
    this.font = font;
    this.number = number;
    this.style = style;
    this.reverseVideo = reverseVideo;
  }
  
  public Font getFont() { return font; }
  public int getNumber() { return number; }
  public int getStyle() { return style; }
  public boolean isReverseVideo() { return reverseVideo; }
}
