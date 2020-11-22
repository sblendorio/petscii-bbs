package eu.sblendorio.bbs.core;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetsciiThreadTest {

  @Test
  @DisplayName("change a name of the client must change the name. (regression test - the previous code will generate a NPE)")
  void changeClientName() {
    BbsThread client = spy(BbsThread.class);
    client.clientName = "originalName";
    BbsThread.clients = ImmutableMap.of(1L, client);
    BbsThread.changeClientName("originalName", "modifiedName");
    assertEquals("modifiedName", BbsThread.clients.get(1L).getClientName());
  }

  @Test
  @DisplayName("When clients list is empty, then null must be returned")
  void whenClienListIsEmpty_ThenNullMustBeReturned() {
    Optional<BbsThread> test = BbsThread.getClientByName("test");
    assertFalse(test.isPresent());
  }

  @Test
  @DisplayName("When a client name is not found in the clients list, then null must be returned")
  void whenClientIsFoundByName_ThenItsIntanceMustBeReturned() {
    BbsThread client = mock(BbsThread.class);
    String clientNameToSearch = "test";
    String clientNameIntoList = "listClientName";
    when(client.getClientName()).thenReturn(clientNameIntoList);
    BbsThread.clients = ImmutableMap.of(1L, client);
    Optional<BbsThread> test = BbsThread.getClientByName(clientNameToSearch);
    assertFalse(test.isPresent());
  }

  @Test
  @DisplayName("When a client name is found in the clients list, then the client must be returned")
  void whenClientIsNotFoundByName_ThenNullMustBeReturned() {
    BbsThread client = mock(BbsThread.class);
    String clientName = "test";
    when(client.getClientName()).thenReturn(clientName);
    BbsThread.clients = ImmutableMap.of(1L, client);
    Optional<BbsThread> test = BbsThread.getClientByName(clientName);
    assertEquals(client, test.orElse(null));
  }

  @Test
  @DisplayName("clients map implementation must not accept null values")
  void clientsMapImplementationMustNotAcceptNullValues() {
    BbsThread.clients = BbsThread.defaultClientsMapImplementation();
    assertThrows(NullPointerException.class, () -> BbsThread.clients.put(1L, null));
  }
}