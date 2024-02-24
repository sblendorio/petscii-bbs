/*
 * Created on 2006/02/22
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

import org.zmpp.media.DrawingArea;

/**
 * Screen model 6 interface.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface ScreenModel6 extends ScreenModel, DrawingArea {

  /**
   * Restricts the mouse pointer to the specified window.
   *
   * @param window the window
   */
  void setMouseWindow(int window);

  /**
   * Returns the specified window.
   *
   * @param window the window
   * @return the window
   */
  Window6 getWindow(int window);

  /**
   * Returns the currently selected window.
   *
   * @return the currently selected window
   */
  Window6 getSelectedWindow();

  /**
   * Instructs the screen model to set the width of the current string
   * to the header.
   *
   * @param zchars the z character array
   */
  void setTextWidthInUnits(char[] zchars);

  /**
   * Reads the current mouse data into the specified array.
   *
   * @param array the array address
   */
  void readMouse(int array);
}
