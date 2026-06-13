This project is about a framework which allows you to create BBS-like services, accessible mainly by vintage computers. It's essence is extending basic classes to build you own service.

The services created will be accessible by TELNET on the given ports. Supported charset/protocols are ASCII, PETSCII, Minitel(Teletel) and Prestel.

Instructions are available in README.md file, in that file there are suggestions about the best ways to build and run BBS services.

## Build & Run

- Build: `mvn clean package` (produces `target/petscii-bbs.jar`)
- Run: `java -jar target/petscii-bbs.jar --bbs <TenantName>:<port> [-t timeout] [-s servicePort]`
- Java 21+ required

## High-Level Architecture

### Entry Point

`eu.sblendorio.bbs.core.BBServer` is the main class. It parses CLI arguments, discovers all tenant classes via classpath reflection (Guava `ClassPath`), and for each `--bbs` endpoint creates a virtual-thread dispatcher that listens on a TCP port and spawns a new `BbsThread` instance per incoming connection.

### Package Structure

```
eu.sblendorio.bbs
├── core/           Framework: base classes, I/O, protocol handlers, utilities
├── tenants/
│   ├── petscii/    PETSCII (Commodore 64) BBS services
│   ├── ascii/      ASCII/Telnet BBS services
│   ├── minitel/    French Minitel services
│   ├── prestel/    British Prestel services
│   └── mixed/      Shared base classes and utilities used across protocols
└── games/          Game AI logic (Connect Four, Tic-Tac-Toe, adventure bridge)
```

### Core Framework (`core/`)

**BbsThread** (abstract, extends `Thread`) - The central base class that all tenants extend. Manages socket lifecycle, client tracking, keep-alive, and provides helpers for I/O, HTTP requests, and file operations. Subclasses must implement `doLoop()` (business logic), `buildIO()` (protocol-specific I/O), `cls()` (clear screen), and `getTerminalType()`.

**BbsInputOutput** (abstract, extends `Reader`) - Protocol-agnostic I/O abstraction wrapping the socket. Handles character reading/writing, quote mode, local echo, and HTML cleaning. Each protocol provides a concrete implementation.

**Protocol-specific thread + I/O pairs:**

| Protocol | Thread Class | I/O Class | Screen |
|----------|-------------|-----------|--------|
| PETSCII  | `PetsciiThread` | `PetsciiInputOutput` | 40x25 |
| ASCII    | `AsciiThread` | `AsciiInputOutput` | 40x24 (configurable) |
| Minitel  | `MinitelThread` | `MinitelInputOutput` | 40x24 |
| Prestel  | `PrestelThread` | `PrestelInputOutput` | 40x24 |

Each pair handles charset encoding, newline conventions, control characters, and backspace behavior specific to that terminal protocol.

**Key utilities in core:**
- `HtmlUtils` - HTML-to-text conversion with diacritics normalization for retro terminals
- `BlockGraphicsPetscii` / `BlockGraphicsMinitel` - Block-character graphics rendering, QR code support
- `XModem` - X-Modem file transfer protocol implementation
- `PetsciiKeys`, `AsciiKeys`, `PetsciiColors` - Protocol-specific constants

### Tenant Pattern

All BBS services ("tenants") are concrete subclasses of one of the protocol thread classes. They are discovered automatically at startup via reflection (classes annotated with `@Hidden` are excluded).

**Creating a tenant:** extend `PetsciiThread`, `AsciiThread`, `MinitelThread`, or `PrestelThread` and implement `doLoop()`. The framework handles connection management, keep-alive, and socket lifecycle.

**Common tenant categories:**
- **Content proxies** - Many tenants extend `WordpressProxyPetscii`/`WordpressProxyAscii` to fetch and render blog/news content from WordPress REST APIs
- **Menus** - Landing pages (e.g. `Menu64`, `MenuApple1`) that list available services and use `launch()` to switch to child tenants
- **Games** - Interactive games (Connect Four, Tic-Tac-Toe, Z-Machine text adventures via `ZorkMachine`)
- **Chat/Social** - Multi-user chat, IRC client, ChatGPT integration
- **Browsers** - `InternetBrowser` renders web pages as text for retro terminals

**Nested tenant launching:** a tenant can call `launch(new OtherTenant())` to transfer control. The framework saves the parent's state (I/O, keep-alive), runs the child's `doLoop()`, and restores the parent on return.

### Connection Lifecycle

```
BBServer.main()
  -> Discover tenant classes via classpath scan
  -> For each --bbs endpoint, start a virtual-thread dispatcher
      -> ServerSocket.accept() loop
          -> Instantiate tenant (no-arg constructor)
          -> buildIO(socket) - create protocol-specific I/O
          -> initBbs() - optional per-tenant initialization
          -> Start KeepAliveThread
          -> doLoop() - tenant business logic runs until exit
          -> Cleanup: close socket, remove from client map
```

### Key Dependencies

- **Jsoup** - HTML parsing for content proxy tenants
- **ZXing** - QR code generation for block graphics
- **Apache Commons** (Lang, IO, CLI) - Utilities and CLI argument parsing
- **Guava** - Classpath scanning for tenant discovery
- **Jackson** - JSON parsing for API integrations
- **Log4j2** - Logging
