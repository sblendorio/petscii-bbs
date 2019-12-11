/*
 * $Id: GameDataImpl.java,v 1.5 2006/04/12 18:00:17 weiju Exp $
 * 
 * Created on 2005/10/10
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
package org.zmpp.vm;

import org.zmpp.base.DefaultMemoryAccess;
import org.zmpp.base.MemoryAccess;
import org.zmpp.encoding.AccentTable;
import org.zmpp.encoding.AlphabetTable;
import org.zmpp.encoding.AlphabetTableV1;
import org.zmpp.encoding.AlphabetTableV2;
import org.zmpp.encoding.CustomAccentTable;
import org.zmpp.encoding.CustomAlphabetTable;
import org.zmpp.encoding.DefaultAccentTable;
import org.zmpp.encoding.DefaultAlphabetTable;
import org.zmpp.encoding.DefaultZCharDecoder;
import org.zmpp.encoding.DefaultZCharTranslator;
import org.zmpp.encoding.ZCharDecoder;
import org.zmpp.encoding.ZCharEncoder;
import org.zmpp.encoding.ZCharTranslator;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.encoding.ZsciiString;
import org.zmpp.media.Resources;

/**
 * This class implements the GameData interface.
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class GameDataImpl implements GameData {

  /**
   * The file header.
   */
  private StoryFileHeader fileheader;
  
  /**
   * The MemoryAccess object.
   */
  private MemoryAccess memaccess;
  
  /**
   * The dictionary.
   */
  private Dictionary dictionary;
  
  /**
   * The object tree.
   */
  private ObjectTree objectTree;
  
  /**
   * The ZSCII encoding object.
   */
  private ZsciiEncoding encoding;
  
  /**
   * The z char decoder.
   */
  private ZCharDecoder decoder;
  
  /**
   * The z char encoder.
   */
  private ZCharEncoder encoder;
  
  /**
   * The alphabet table.
   */
  private AlphabetTable alphabetTable;
  
  /**
   * The media resources.
   */
  private Resources resources;
  
  /**
   * The original story file data.
   */
  private byte[] storyfileData;
  
  private int checksum;  
  
  /**
   * Constructor.
   * 
   * @param storyfile the story file as a byte array
   * @param resources the media resources
   */
  public GameDataImpl(byte[] storyfile, Resources resources) {

    storyfileData = storyfile;
    this.resources = resources;
    reset();
  }
  
  /**
   * {@inheritDoc}
   */
  public final void reset() {

    // Make a copy and initialize from the copy
    final byte[] data = new byte[storyfileData.length];
    System.arraycopy(storyfileData, 0, data, 0, storyfileData.length);
    
    memaccess = new DefaultMemoryAccess(data);
    fileheader = new DefaultStoryFileHeader(memaccess);
    
    checksum = calculateChecksum();
    
    // Install the whole character code system here
    initEncodingSystem();
    
    // The object tree and dictionaries depend on the code system
    if (fileheader.getVersion() <= 3) {
      
      objectTree = new ClassicObjectTree(memaccess,
          fileheader.getObjectTableAddress(), decoder);
      
    } else {
      
      objectTree = new ModernObjectTree(memaccess,
          fileheader.getObjectTableAddress(), decoder);
    }
    
    final DictionarySizes sizes = (fileheader.getVersion() <= 3) ?
        new DictionarySizesV1ToV3() : new DictionarySizesV4ToV8();
    dictionary = new DefaultDictionary(memaccess,
        fileheader.getDictionaryAddress(), decoder, sizes);
  }
  
  private void initEncodingSystem() {
    
    final AccentTable accentTable = (fileheader.getCustomAccentTable() == 0) ?
        new DefaultAccentTable() :
        new CustomAccentTable(memaccess, fileheader.getCustomAccentTable());
    encoding = new ZsciiEncoding(accentTable);

    // Configure the alphabet table
    if (fileheader.getCustomAlphabetTable() == 0) {
            
      if (fileheader.getVersion() == 1) {
        
        alphabetTable = new AlphabetTableV1();
        
      } else if (fileheader.getVersion() == 2) {
        
        alphabetTable = new AlphabetTableV2();
      
      } else {
        
        alphabetTable = new DefaultAlphabetTable();
      }
      
    } else {
      
      alphabetTable = new CustomAlphabetTable(memaccess,
          fileheader.getCustomAlphabetTable());
    }
    
    final ZCharTranslator translator =
      new DefaultZCharTranslator(alphabetTable);
        
    final Abbreviations abbreviations = new Abbreviations(memaccess,
        fileheader.getAbbreviationsAddress());
    decoder = new DefaultZCharDecoder(encoding, translator, abbreviations);
    encoder = new ZCharEncoder(translator);
    ZsciiString.initialize(encoding);
  }
    
  
  /**
   * {@inheritDoc}
   */
  public MemoryAccess getMemoryAccess() {
    
    return memaccess;
  }

  /**
   * {@inheritDoc}
   */
  public StoryFileHeader getStoryFileHeader() {
    
    return fileheader;
  }
  
  /**
   * {@inheritDoc}
   */
  public Dictionary getDictionary() {
    
    return dictionary;
  }
  
  /**
   * {@inheritDoc}
   */
  public ObjectTree getObjectTree() {
    
    return objectTree;
  }
  
  /**
   * {@inheritDoc}
   */
  public AlphabetTable getAlphabetTable() {

    return alphabetTable;
  }
  
  /**
   * {@inheritDoc}
   */
  public ZCharDecoder getZCharDecoder() {
   
    return decoder;
  }
  
  /**
   * {@inheritDoc}
   */
  public ZCharEncoder getZCharEncoder() {
    
    return encoder;
  }
  
  /**
   * {@inheritDoc}
   */
  public ZsciiEncoding getZsciiEncoding() {
    
    return encoding;
  }
  
  /**
   * {@inheritDoc}
   */
  public Resources getResources() {
    
    return resources;
  }

  /**
   * Calculates the checksum of the file.
   * 
   * @param fileheader the file header
   * @return the check sum
   */
  private int calculateChecksum() {
    
    final int filelen = fileheader.getFileLength();
    int sum = 0;
    
    for (int i = 0x40; i < filelen; i++) {
    
      sum += getMemoryAccess().readUnsignedByte(i);
    }
    return (sum & 0xffff);
  }
  
  public int getCalculatedChecksum() {
    
    return checksum;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasValidChecksum() {
    
    return getStoryFileHeader().getChecksum() == checksum;
  }
}
