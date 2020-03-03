/*
 * $Id: WindowArea.java,v 1.5 2006/05/16 18:35:23 weiju Exp $
 * 
 * Created on 2006/02/23
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

import java.awt.Color;

import org.zmpp.vm.Window6;

/**
 * This class holds the knowledge about a window's position and sizes.
 * The setter methods all take 1-based coordinates, which is the standard
 * in the Z-machine, whereas the getter methods return the coordinates
 * according to the Java graphics model, which is 0 based.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class WindowArea {

  private int left;
  private int top;
  private int width;
  private int height;
  private int marginLeft;
  private int marginRight;
  
  /**
   * Constructor.
   */
  public WindowArea() {
    
    this.left = 1;
    this.top = 1;
  }
  
  public int getLeft() { return left; }
  public int getTop() { return top; }
  public int getWidth() { return width; }
  public int getHeight() { return height; }
  
  public void setPosition(int left, int top) {
    
    this.left = left;
    this.top = top;
  }
  
  public void setSize(int width, int height) {
    
    this.width = width;
    this.height = height;
  }
  
  public void setMargins(int left, int right) {
    
    this.marginLeft = left;
    this.marginRight = right;
  }
  
  public int getStartX() {
    
    return (left - 1) + marginLeft;
  }
  
  public int getStartY() {
    
    return top - 1;
  }
  
  public int getOutputWidth() {
    
    return width - (marginLeft + marginRight);
  }
  
  public int getOutputHeight() {
    
    return height;
  }

  public void clip(Canvas canvas) {
    
    canvas.setClip(getStartX(), getStartY(), getOutputWidth(),
                   getOutputHeight());
  }
  
  public void fill(Canvas canvas, Color color) {
    
    clip(canvas);
    canvas.fillRect(color, getStartX(), getStartY(),
                    getOutputWidth(), getOutputHeight());
  }
  
  public int getProperty(int propertynum) {
    
    switch (propertynum) {
    
      case Window6.PROPERTY_Y_COORD: return top;
      case Window6.PROPERTY_X_COORD: return left;
      case Window6.PROPERTY_Y_SIZE: return height;
      case Window6.PROPERTY_X_SIZE: return width;
      case Window6.PROPERTY_LEFT_MARGIN: return marginLeft;
      case Window6.PROPERTY_RIGHT_MARGIN: return marginRight;
      default: return 0;
    }
  }
}
