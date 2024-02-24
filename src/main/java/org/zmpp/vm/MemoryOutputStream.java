/*
 * Created on 11/23/2005
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
package org.zmpp.vm;

import java.util.ArrayList;
import java.util.List;

import org.zmpp.io.OutputStream;
import static org.zmpp.base.MemoryUtil.toUnsigned16;

/**
 * This class implements output stream 3. This stream writes to dynamic
 * memory. The stream contains a table address stack in order to
 * support nested selections.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class MemoryOutputStream implements OutputStream {

  /**
   * Maximum nesting depth for this stream.
   */
  private static final int MAX_NESTING_DEPTH = 16;

  /**
   * Table position representation.
   */
  static class TablePosition {
    int tableAddress;
    int bytesWritten;

    /**
     * Constructor.
     * @param tableAddress address of the table
     */
    TablePosition(int tableAddress) {
      this.tableAddress = tableAddress;
    }
  }

  private Machine machine;

  /** Support nested selections. */
  private List<TablePosition> tableStack;

  /**
   * Constructor.
   * @param machine the machine object
   */
  public MemoryOutputStream(Machine machine) {
    tableStack = new ArrayList<TablePosition>();
    this.machine = machine;
  }

  /** {@inheritDoc} */
  public void print(final char zsciiChar) {
    final TablePosition tablePos = tableStack.get(tableStack.size() - 1);
    final int position = tablePos.tableAddress + 2 + tablePos.bytesWritten;
    machine.writeUnsigned8(position, zsciiChar);
    tablePos.bytesWritten++;
  }

  /** {@inheritDoc} */
  public void flush() {
    // intentionally left empty
  }

  /**
   * {@inheritDoc}
   */
  public void close() {
    // intentionally left empty
  }

  /**
   * {@inheritDoc}
   */
  public void select(final boolean flag) {
    if (!flag && tableStack.size() > 0) {
      // Write the total number of written bytes to the first word
      // of the table
      final TablePosition tablePos = tableStack.remove(tableStack.size() - 1);
      machine.writeUnsigned16(tablePos.tableAddress,
                              toUnsigned16(tablePos.bytesWritten));

      if (machine.getVersion() == 6) {
        writeTextWidthInUnits(tablePos);
      }
    }
  }

  /**
   * Writes the text width in units.
   * @param tablepos table position
   */
  private void writeTextWidthInUnits(TablePosition tablepos) {
    int numwords = tablepos.bytesWritten;
    char[] data = new char[numwords];

    for (int i = 0; i < numwords; i++) {
      data[i] = (char) machine.readUnsigned8(tablepos.tableAddress + i + 2);
    }
    machine.getScreen6().setTextWidthInUnits(data);
  }

  /**
   * Selects this memory stream.
   *
   * @param tableAddress the table address
   * @param tableWidth the table width
   */
  public void select(final int tableAddress, final int tableWidth) {
    //this.tableWidth = tableWidth;
    if (tableStack.size() < MAX_NESTING_DEPTH) {
      tableStack.add(new TablePosition(tableAddress));
    } else {
      machine.halt("maximum nesting depth (16) for stream 3 exceeded");
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSelected() {
    return !tableStack.isEmpty();
  }
}
