package eu.sblendorio.bbs.core;

import com.google.common.collect.ImmutableList;
import eu.sblendorio.bbs.tenants.petscii.Ossa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BBServerTest {

  @Test
  @DisplayName("when list is empty null will be returned")
  static void whenListIsEmpty_ThenNullMustBeFound() {
    BBServer sut = new BBServer();
    assertNull(sut.findTenant(Collections.emptyList(), "name"));
  }

  @Test
  @DisplayName("when tenant is present in the list then a value will be returned")
  static void findTenant() {
    BBServer sut = new BBServer();
    assertNotNull(sut.findTenant(ImmutableList.of(Ossa.class), "ossa"));
  }
}