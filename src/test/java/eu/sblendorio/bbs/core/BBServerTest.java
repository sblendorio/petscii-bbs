package eu.sblendorio.bbs.core;

import com.google.common.collect.ImmutableList;
import eu.sblendorio.bbs.tenants.Ossa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BBServerTest {

  @Test
  @DisplayName("when list is empty null will be returned")
  void whenListIsEmpty_ThenNullMustBeFound() {
    assertNull(BBServer.findTenant(Collections.emptyList(), "name"));
  }

  @Test
  @DisplayName("when tenant is present in the list then a value will be returned")
  void findTenant() {
    PetsciiThread p = new Ossa();
    assertNotNull(BBServer.findTenant(ImmutableList.of(p.getClass()), "ossa"));
  }
}