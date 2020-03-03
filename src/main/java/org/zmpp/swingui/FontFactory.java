/*
 * $Id: FontFactory.java,v 1.1 2006/02/25 18:27:37 weiju Exp $
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

import org.zmpp.vm.ScreenModel;

/**
 * The FontFactory creates and manages fonts that are requested by the screen
 * model. It manages instances of ScreenFont.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class FontFactory {

  private ScreenFont[][] fonts;
  
  public FontFactory() {

    fonts = new ScreenFont[4][16];
  }
  
  public void initialize(Font standardFontRoman, Font fixedFontRoman) {
    
    fonts[0][0] = new ScreenFont(standardFontRoman, 1, ScreenModel.TEXTSTYLE_ROMAN);
    fonts[3][0] = new ScreenFont(fixedFontRoman, 4, ScreenModel.TEXTSTYLE_ROMAN);
  }

  public ScreenFont getFont(int fontnum) {

    if (fontnum >= 1 && fontnum <= 4) return fonts[fontnum - 1][0];
    return null;
  }
  
  public ScreenFont getTextStyle(ScreenFont oldfont, int style) {
    
    return getStyle(oldfont.getNumber(), oldfont.getStyle(), style);
  }
  
  private ScreenFont getStyle(int fontnum, int oldstyle, int style) {
    
    if (style == ScreenModel.TEXTSTYLE_ROMAN) return fonts[fontnum - 1][0];
    else {
      
      int newstyle = oldstyle | style;
      if (fonts[fontnum - 1][newstyle] == null) {
        
        deriveFont(fontnum, newstyle);
      }
      return fonts[fontnum - 1][newstyle];
    }
  }
  
  private void deriveFont(int fontnum, int style) {
    
    int fontstyle = Font.PLAIN;
    boolean reverse = false;
    Font oldfont = fonts[fontnum - 1][0].getFont();
    
    // For fixed style fonts
    if (fontnum == 1 && (style & ScreenModel.TEXTSTYLE_FIXED) > 0) {
      
      oldfont = fonts[3][0].getFont();
    }
    
    if ((style & ScreenModel.TEXTSTYLE_BOLD) > 0) fontstyle |= Font.BOLD;
    if ((style & ScreenModel.TEXTSTYLE_ITALIC) > 0) fontstyle |= Font.ITALIC;
    if ((style & ScreenModel.TEXTSTYLE_REVERSE_VIDEO) > 0) reverse = true;
    Font newfont = oldfont.deriveFont(fontstyle);
    fonts[fontnum - 1][style] = new ScreenFont(newfont, fontnum, style, reverse);
  }
}
