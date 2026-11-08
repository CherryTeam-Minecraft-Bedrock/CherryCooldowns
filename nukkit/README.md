# CherryCooldowns

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Java 17](https://img.shields.io/badge/Java-17-orange)
![Platform](https://img.shields.io/badge/platform-Nukkit--MOT-green)
![Version](https://img.shields.io/badge/version-1.0.0-informational)

A lightweight item cooldown management plugin and Java API for **Nukkit-MOT** (Minecraft: Bedrock Edition) servers.

- **Item Interaction & Consumption Cooldowns** – automatically sets delay for consuming or right-clicking items (e.g. Ender Pearls, Golden Apples).
- **Persistent Storage** – registered item cooldown durations are automatically saved to `cooldowns.properties`.
- **Public Java API** – `CooldownsManager` and `Cooldown` for registering and modifying item cooldown durations dynamically from other plugins.
- **Configurable Message Format** – customizable action warning message with `%cooldown%` placeholder.

---

## Table of contents

- [Requirements](#requirements)
- [Installation](#installation)
- [For server owners](#for-server-owners)
  - [Configuration](#configuration)
  - [Permissions](#permissions)
- [For developers](#for-developers)
  - [Adding the plugin as a dependency](#adding-the-plugin-as-a-dependency)
  - [API overview](#api-overview)
  - [`CooldownsManager`](#cooldownsmanager)
  - [`Cooldown`](#cooldown)
  - [Examples](#examples)
  - [Building from source](#building-from-source)
- [License](#license)

---

## Requirements

| Requirement | Version | Notes |
|---|---|---|
| Java | 17+ | Required for running the plugin |
| [Nukkit-MOT](https://github.com/MemoriesOfTime/Nukkit-MOT) | `1.26.50-R1` (build target) | Plugin API `1.0.0` |

---

## Installation

1. Download `cooldowns-1.0.0.jar` from the [Releases](../../releases) page.
2. Place the `.jar` file into your server's `plugins/` directory.
3. Start or restart the server. The plugin will generate `plugins/CherryCooldowns/config.yml` and `plugins/CherryCooldowns/cooldowns.properties`.
4. Edit `config.yml` to adjust item cooldowns and messaging to your liking.

---

## For server owners

### Configuration

`plugins/CherryCooldowns/config.yml` (defaults shown):

```yaml
message_format: "&cHey! You can't use this item, wait: %cooldown%s"
cooldowns: 
  minecraft:ender_pearl: 10
  minecraft:golden_apple: 15
  minecraft:enchanted_golden_apple: 60
```

| Option | Type | Description |
|---|---|---|
| `message_format` | string | Message sent to a player when attempting to use an item while on cooldown. Supports `&` colour codes and `%cooldown%` placeholder. |
| `cooldowns.<item_namespace>` | integer | Cooldown duration in seconds for a given item namespace ID (e.g. `minecraft:ender_pearl`). |

### Permissions

| Permission | Default | Description |
|---|---|---|
| `cherry.permissions.cooldown.all` | `op` | Bypass item usage cooldowns. |

---

## For developers

### Adding the plugin as a dependency

Add `CherryCooldowns` to your `plugin.yml`:

```yaml
depend: ["CherryCooldowns"]
```

#### Maven dependency (local jar)

```xml
<dependency>
  <groupId>com.cherry</groupId>
  <artifactId>cooldowns</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

Install it into your local Maven repository:

```bash
mvn install:install-file -Dfile=cooldowns-1.0.0.jar \
  -DgroupId=com.cherry -DartifactId=cooldowns -Dversion=1.0.0 -Dpackaging=jar
```

---

### API overview

| Class | Package | Purpose |
|---|---|---|
| `CooldownsManager` | `com.cherry.cooldowns.api` | Singleton manager for registering, unregistering, reading, and updating item cooldowns. |
| `Cooldown` | `com.cherry.cooldowns.api` | Data holder representing a cooldown entry (`id` and `time` in seconds). |

---

### `CooldownsManager`

| Method | Description |
|---|---|
| `static CooldownsManager getInstance()` | Get the singleton instance. |
| `void loadDataFolder(String loadedFolder)` | Sets the target directory where `cooldowns.properties` is saved/loaded. |
| `void register(Cooldown cooldown)` | Registers a new cooldown and persists it to `cooldowns.properties`. |
| `void unregister(String rawId)` | Removes a registered cooldown and deletes it from `cooldowns.properties`. |
| `int getTime(String rawId)` | Gets the registered cooldown duration (in seconds) for the specified item namespace ID. |
| `void setTime(String rawId, int time)` | Updates the cooldown time (in seconds) for an existing registered item. |

---

### `Cooldown`

| Constructor / Method | Description |
|---|---|
| `Cooldown(String id)` | Creates a cooldown with default duration of 15 seconds. |
| `Cooldown(String id, int time)` | Creates a cooldown with specified time in seconds. |
| `Cooldown(Item item)` | Creates a cooldown using `item.getNamespaceId()` with default 15 seconds. |
| `Cooldown(Item item, int time)` | Creates a cooldown using `item.getNamespaceId()` with specified time in seconds. |
| `String getId()` | Gets normalized ID (spaces replaced with `_`). |
| `int getTime()` | Gets duration in seconds. |

---

### Examples

#### Registering a custom item cooldown dynamically

```java
import com.cherry.cooldowns.api.Cooldown;
import com.cherry.cooldowns.api.CooldownsManager;
import cn.nukkit.item.Item;

CooldownsManager manager = CooldownsManager.getInstance();

// Register by namespace string
manager.register(new Cooldown("minecraft:chorus_fruit", 20));

// Or register directly using a Nukkit Item instance
Item customItem = Item.get("minecraft:potion");
manager.register(new Cooldown(customItem, 30));
```

#### Updating or reading cooldown times at runtime

```java
CooldownsManager manager = CooldownsManager.getInstance();

// Check configured duration in seconds
int time = manager.getTime("minecraft:ender_pearl");

// Update existing cooldown duration
manager.setTime("minecraft:ender_pearl", 5);
```

---

### Building from source

Requirements: JDK 17+, Maven 3.8+.

```bash
git clone https://github.com/CherryTeam-Minecraft-Bedrock/CherryCooldowns.git
cd CherryCooldowns
mvn clean package
```

The output jar will be located at `target/cooldowns-1.0.0.jar`.

---

## License

Distributed under the **MIT License**. See [LICENSE](LICENSE) for details.

Author: **CherryTeam**  
Repository: [GitHub](https://github.com/CherryTeam-Minecraft-Bedrock/CherryCooldowns)