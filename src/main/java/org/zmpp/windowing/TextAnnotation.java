/*
 * Created on 2008/04/26
 * Copyright (c) 2005-2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.windowing;

import java.io.Serializable;


/**
 * An annotation to indicate how a sequence of characters should be printed.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class TextAnnotation implements Serializable {

  private static final long serialVersionUID = 343600790510649067L;

  // Font flags have the same bit layout as in the ScreenModel interface so
  // so the flags are compatible
  public static final char FONT_NORMAL  = 1;
  public static final char FONT_CHARACTER_GRAPHICS  = 3;
  public static final char FONT_FIXED   = 4;

  // Text styles have the same bit layout as in the ScreenModel interface
  // so the flags are compatible
  public static final int TEXTSTYLE_ROMAN          = 0;
  public static final int TEXTSTYLE_REVERSE_VIDEO  = 1;
  public static final int TEXTSTYLE_BOLD           = 2;
  public static final int TEXTSTYLE_ITALIC         = 4;
  public static final int TEXTSTYLE_FIXED          = 8;

  private char font;
  private int style;
  private int background;
  private int foreground;

  /**
   * Constructor.
   * @param font font number
   * @param style style number
   * @param background background color
   * @param foreground foreground color
   */
  public TextAnnotation(char font, int style, int background, int foreground) {
    this.font = font;
    this.style = style;
    this.background = background;
    this.foreground = foreground;
  }

  /**
   * Constructor.
   * @param font font number
   * @param style style number
   */
  public TextAnnotation(char font, int style) {
    this(font, style, ScreenModel.COLOR_DEFAULT, ScreenModel.COLOR_DEFAULT);
  }

  /**
   * Derives an annotation with a modified font based on this object.
   * @param newFont new font number
   * @return TextAnnotation with changed font
   */
  public TextAnnotation deriveFont(char newFont) {
    return new TextAnnotation(newFont, this.style, this.background,
                              this.foreground);
  }

  /**
   * Derives an annotation with a modified text style based on this object.
   * @param newStyle new style number
   * @return TextAnnotation with changed style
   */
  public TextAnnotation deriveStyle(int newStyle) {
    int finalStyle = style;
    if (newStyle == TextAnnotation.TEXTSTYLE_ROMAN) {
      finalStyle = newStyle;
    } else {
      finalStyle |= newStyle;
    }
    return new TextAnnotation(this.font, finalStyle, this.background,
                              this.foreground);
  }

  /**
   * Derives an annotation with a modified background color based
   * on this object.
   * @param newBackground new background color
   * @return TextAnnotation with changed foreground color
   */
  public TextAnnotation deriveBackground(int newBackground) {
    return new TextAnnotation(this.font, this.style, newBackground,
                              this.foreground);
  }

  /**
   * Derives an annotation with a modified foreground color based
   * on this object.
   * @param newForeground new foreground color
   * @return TextAnnotation with changed foreground color
   */
  public TextAnnotation deriveForeground(int newForeground) {
    return new TextAnnotation(this.font, this.style, this.background,
                              newForeground);
  }

  /**
   * Returns the font.
   * @return font number
   */
  public char getFont() { return font; }

  /**
   * Determines whether this annotation has a fixed style font.
   * @return true if fixed, false if variable font
   */
  public boolean isFixed() {
    return font == FONT_FIXED || (style & TEXTSTYLE_FIXED) == TEXTSTYLE_FIXED;
  }
  /**
   * Determines whether this annotation has a roman font.
   * @return true if roman, false otherwise
   */
  public boolean isRoman() { return style == TEXTSTYLE_ROMAN; }
  /**
   * Determines whether this annotation has a bold font.
   * @return true if bold, false otherwise
   */
  public boolean isBold() {
    return (style & TEXTSTYLE_BOLD) == TEXTSTYLE_BOLD;
  }
  /**
   * Determines whether this annotation has an italic font.
   * @return true if italic, false otherwise
   */
  public boolean isItalic() {
    return (style & TEXTSTYLE_ITALIC) == TEXTSTYLE_ITALIC;
  }
  /**
   * Determines whether the text is displayed as reverse video.
   * @return true if reverse video, false otherwise
   */
  public boolean isReverseVideo() {
    return (style & TEXTSTYLE_REVERSE_VIDEO) == TEXTSTYLE_REVERSE_VIDEO;
  }

  /**
   * Returns the background color.
   * @return background color
   */
  public int getBackground() { return background; }

  /**
   * Returns the foreground color.
   * @return foreground color
   */
  public int getForeground() { return foreground; }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "TextAnnotation, fixed: " + isFixed() + " bold: " + isBold() +
            " italic: " + isItalic() + " reverse: " + isReverseVideo() +
            " bg: " + background + " fg: " + foreground;
  }
}

