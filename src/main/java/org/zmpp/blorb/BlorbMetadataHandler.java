/*
 * $Id: BlorbMetadataHandler.java,v 1.6 2006/05/05 17:52:07 weiju Exp $
 * 
 * Created on 2006/03/04
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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.zmpp.base.MemoryReadAccess;
import org.zmpp.iff.Chunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.media.InformMetadata;
import org.zmpp.media.StoryMetadata;

/**
 * This class parses the metadata chunk in the Blorb file and converts
 * it into a Treaty of Babel metadata object. 
 * 
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BlorbMetadataHandler extends DefaultHandler {

  private StoryMetadata story;
  private StringBuilder buffer;
  private boolean processAux;
    
  public BlorbMetadataHandler(FormChunk formchunk) {
 
    extractMetadata(formchunk);    
  }
  
  public InformMetadata getMetadata() {
    
    return (story == null) ? null : new InformMetadata(story);
  }
  
  private void extractMetadata(final FormChunk formchunk) {

    final Chunk chunk = formchunk.getSubChunk("IFmd".getBytes());
    if (chunk != null) {

      final MemoryReadAccess memaccess = chunk.getMemoryAccess();
      
      /*
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < chunk.getSize(); i++) {
      
        buffer.append((char) chunk.getMemoryAccess().readUnsignedByte(i + Chunk.CHUNK_HEADER_LENGTH));
      }
      System.out.println(buffer.toString());
      */
      
      final MemoryAccessInputStream meminput =
        new MemoryAccessInputStream(memaccess, Chunk.CHUNK_HEADER_LENGTH,
          chunk.getSize() + Chunk.CHUNK_HEADER_LENGTH);
      
      try {
        
        final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(meminput, this);
        
      } catch (Exception ex) {
        
        ex.printStackTrace();
      }
    }
  }
    
  // **********************************************************************
  // **** Parsing meta data
  // *********************************
  
  public void startElement(final String uri, final String localName,
      final String qname, final Attributes attributes) {
    
    if ("story".equals(qname)) {

      story = new StoryMetadata();
    }
    if ("title".equals(qname)) {
      
      buffer = new StringBuilder();
    }
    if ("headline".equals(qname)) {
      
      buffer = new StringBuilder();
    }
    if ("author".equals(qname)) {
      
      buffer = new StringBuilder();
    }
    if ("genre".equals(qname)) {
      
      buffer = new StringBuilder();
    }
    if ("description".equals(qname)) {
      
      buffer = new StringBuilder();
    }
    if (isPublishYear(qname)) {
      
      buffer = new StringBuilder();
    }
    if ("auxiliary".equals(qname)) {
      
      processAux = true;
    }
    if ("coverpicture".equals(qname)) {
      
      buffer = new StringBuilder();
    }
    if ("group".equals(qname)) {
      
      buffer = new StringBuilder();
    }
  }

  public void endElement(final String uri, final String localName,
      final String qname) {

    if ("title".equals(qname)) {
      
      story.setTitle(buffer.toString());
    }
    if ("headline".equals(qname)) {
      
      story.setHeadline(buffer.toString());
    }
    if ("author".equals(qname)) {
      
      story.setAuthor(buffer.toString());
    }
    if ("genre".equals(qname)) {
      
      story.setGenre(buffer.toString());
    }
    if ("description".equals(qname) && !processAux) {
      
      story.setDescription(buffer.toString());
    }
    if (isPublishYear(qname)) {
      
      story.setYear(buffer.toString());
    }
    if ("group".equals(qname)) {
      
      story.setGroup(buffer.toString());
    }
    if ("coverpicture".equals(qname)) {
      
      final String val = buffer.toString().trim();
      try {
        
        story.setCoverPicture(Integer.parseInt(val));
        
      } catch (NumberFormatException ex) {
        
        System.err.println("NumberFormatException in cover picture: " + val);
      }
    }
    if ("auxiliary".equals(qname)) {
      
      processAux = false;
    }
    
    if ("br".equals(qname) && buffer != null) {
      
      buffer.append("\n");
    }
  }  
  
  public void characters(final char[] ch, final int start, final int length) {
    
    if (buffer != null) {
      
      final StringBuilder partbuilder = new StringBuilder();
      for (int i = start; i < start + length; i++) {

        partbuilder.append(ch[i]);
      }
      buffer.append(partbuilder.toString().trim());
    }
  }
  
  /**
   * Unfortunately, year was renamed to firstpublished between the preview
   * metadata version of Inform 7 and the Treaty of Babel version, so
   * we handle both here.
   * 
   * @param str the qname
   * @return true if matches, false, otherwise
   */
  private boolean isPublishYear(String str) {
    
    return "year".equals(str) || "firstpublished".equals(str);
  }
}
