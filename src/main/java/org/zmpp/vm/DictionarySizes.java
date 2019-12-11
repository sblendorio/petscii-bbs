/*
 * $Id: DictionarySizes.java,v 1.1 2006/01/20 00:59:34 weiju Exp $
 * 
 * Created on 2006/01/19
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

/**
 * Z-code compilers seem to truncate dictionary string pretty
 * sloppy (i.e. multibyte sequences such as A2 escape) so that in
 * dictionary entries, the end bit does not always exist. Unfortunately,
 * the entry size given in the dictionary header is not reliable either.
 * Therefore we need to provide a size to the dictionary that is taken
 * from the Standard Specification Document. The specification specifies
 * both the number of bytes and the number of maximum characters
 * per entry which we access here. By defining a dictionary
 * size object, we avoid keep dictionary classes clean of version
 * dependency. 
 *  
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface DictionarySizes {

  /**
   * The number of bytes for an entry.
   * 
   * @return the number of bytes for an entry
   */
  int getNumEntryBytes();
  
  /**
   * The maximum number of characters for an entry.
   * 
   * @return the maximum number of characters
   */
  int getMaxEntryChars();
}
