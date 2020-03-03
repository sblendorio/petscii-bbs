/*
 * $Id: BlorbImage.java,v 1.1 2006/05/12 21:58:57 weiju Exp $
 * 
 * Created on 2006/05/09
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
package org.zmpp.blorb;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * This class contains informations related to Blorb images and their
 * scaling. Scaling information is optional and probably only relevant
 * to V6 games. BlorbImage also calculates the correct image size,
 * according to the specification made in the Blorb standard specification.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BlorbImage {

  /**
   * This class represents a ratio.
   */
  protected static class Ratio {
    
    private int numerator;
    private int denominator;
    
    public Ratio(int numerator, int denominator) {
     
      this.numerator = numerator;
      this.denominator = denominator;
    }
    
    public int getNumerator() { return numerator; }
    public int getDenominator() { return denominator; }
    public float getValue() { return (float) numerator / denominator; }
    public boolean isDefined() {
      return !(numerator == 0 && denominator == 0);
    }
    public String toString() { return numerator + "/" + denominator; }
  }
  
  protected static class Resolution {
    
    private int width;
    private int height;
    
    public Resolution(int width, int height) {
  
      this.width = width;
      this.height = height;
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String toString() { return width + "x" + height; }
  }
  
  /**
   * This class represents resolution information.
   */
  protected static class ResolutionInfo {
    
    private Resolution standard;
    private Resolution minimum;
    private Resolution maximum;    
    
    public ResolutionInfo(Resolution std, Resolution min, Resolution max) {
      
      standard = std;
      minimum = min;
      maximum = max;
    }
    
    public Resolution getStandard() { return standard; }
    public Resolution getMinimum() { return minimum; }
    public Resolution getMaximum() { return maximum; }
    public float computeERF(int screenwidth, int screenheight) {
      
      return Math.min(screenwidth / standard.getWidth(),
          screenheight / standard.getHeight());
    }
    
    public String toString() {
      
      return "Std: " + standard.toString() + " Min: " + minimum.toString() +
        " Max: " + maximum.toString();
    }
  }
  
  protected static class ScaleInfo {
   
    private ResolutionInfo resolutionInfo;
    private Ratio standard;
    private Ratio minimum;
    private Ratio maximum;
   
    public ScaleInfo(ResolutionInfo resinfo, Ratio std, Ratio min, Ratio max) {
      
      this.resolutionInfo = resinfo;
      this.standard = std;
      this.minimum = min;
      this.maximum = max;
    }
    public ResolutionInfo getResolutionInfo() { return resolutionInfo; }
    public Ratio getStdRatio() { return standard; }
    public Ratio getMinRatio() { return minimum; }
    public Ratio getMaxRatio() { return maximum; }
    public float computeScaleRatio(int screenwidth, int screenheight) {
            
      float value = resolutionInfo.computeERF(screenwidth, screenheight)
        * standard.getValue();
      
      if (minimum.isDefined() && value < minimum.getValue()) {
        
        value = minimum.getValue();
      }
      if (maximum.isDefined() && value > maximum.getValue()) {
        
        value = maximum.getValue();
      }
      return value;
    }
    
    public String toString() {
      
      return String.format("std: %s, min: %s, max: %s\n",
          standard.toString(), minimum.toString(), maximum.toString());
    }
  }
  
  private BufferedImage image;
  private Resolution resolution;
  private ScaleInfo scaleinfo;
  
  public BlorbImage(BufferedImage image) {
    
    this.image = image;
  }
  
  public BlorbImage(int width, int height) {
    
    resolution = new Resolution(width, height);
  }
  
  public BufferedImage getImage() { return image; }
  public ScaleInfo getScaleInfo() { return scaleinfo; }
  
  public Dimension getSize(int screenwidth, int screenheight) {
    
    if (scaleinfo != null) {
      
      float ratio = scaleinfo.computeScaleRatio(screenwidth, screenheight);
      if (image != null) {
        
        return new Dimension((int) (image.getWidth() * ratio),
          (int) (image.getHeight() * ratio));
        
      } else {
        
        return new Dimension((int) (resolution.getWidth() * ratio),
            (int) (resolution.getHeight() * ratio));
      }
      
    } else {
     
      if (image != null) {
        return new Dimension(image.getWidth(), image.getHeight());
      } else {
        
        return new Dimension(resolution.getWidth(), resolution.getHeight());
      }
    }    
  }
  
  protected void setScaleInfo(ScaleInfo scaleinfo) {
    
    this.scaleinfo = scaleinfo;
  }
}
