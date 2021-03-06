# Code Lock

CodeLock is a Minecraft plugin (and my first plugin made ever!). It allows players to lock various entities with a code lock. Inspired by Rust (game), the code can only be entered once and then never again. This makes it easier to lock things without having to worry about entering the code every time.

## Motivation

I had just created my first Minecraft server on a Raspberry Pi 4B using Spigot and wanted to add a plugin that would lock things with a code lock. However, I found it annoying and redundant to enter the code every time, which discouraged the use of the code lock on things I would frequently use. Since I am a huge Rust player, I wanted something similar to their code lock. Thus, CodeLock was born! In fact, this plugin was intentionally designed to work just like it would in Rust.

## Beta

Code Lock is currently in beta. The plugin is working as expected for now, but some bugs might not have been discovered yet. I recommend using it on smaller servers, like a survival server with your friends! However, you should not **depend** on the Code Lock's reliability just yet! If you encounter problems with a Code Lock and can't remove it for any reason, there is always the `/codelock cleardata` command to wipe **EVERY** code lock.

Please report any bugs or suggestions to the project's [issues](https://github.com/maxijonson/code-lock/issues). Thanks for your contribution!

## Installation

You can download the plugin's jar file in the [release](https://github.com/maxijonson/code-lock/releases) page of this project, but if you wish to create it manually for the Github project, you may follow the "developement" section below.

## Updating

When updating the plugin, you may or may not need to clear all the Code Lock data.

If you're updating to a **patch** version (e.g: going from `1.0.0` to `1.0.1`), then the data should still be compatible and you might not need to clear data unless specified or if you're experiencing issues after the patch.

If you're updating to a **minor** or a **major** (e.g: going from `1.0.0` to `1.1.0` or `2.0.0`), it is recommended to clear the data. This is because the data model of the plugin could have changed from one version to the other, which would make the data loading fail. You can always try to not clear the data and see if you get any errors when loading the plugin. If you do, then you may need to clear the data.

## Pre-requisites

Code Lock was developed on `Spigot 1.16.5` API, so versions below or above 1.16 could potentially not be compatible.

## Features

Here are the key features of the plugin, other than just "locking" stuff!

### Craftable

The lock has a custom crafting recipe that allows everyone to craft them, providing they have the resources. Use the following recipe to craft it.

![CodeLock Recipe](recipe.png)

_Any button can be used in the middle!_

### Enter and forget

Enter the code once when setting the lock and never do it again! The lock will still lock other players who did not enter the code, but you won't even notice it is there. The only time you'll have to re-enter the code is if it changes.

### Player damage protection

Locked entities automatically have protection against _direct_ damage from unauthorized players. This means that fire and explosions will still destroy the entity.

_Note: although the entity itself is protected, any other blocks around it are not. This means that a locked door can be bypassed by simply breaking another block to get through (or even break the block under the door!). That said, a grief plugin should be used in conjunction with this plugin!_

### Guest codes

You can set a guest code for your trusted but not-so-trusted friends. Guests can interact with the entity, but may not lock/unlock it or change the code.

## Usage

Use the `/codelock usage` command to learn about the different ways you can use the code lock.

It is also worth mentioning that the only way to change a lock code is to unlock it first (just like in Rust). Keep this in mind, as this could allow sneaky players to change the code before you!

## Development

If you wish to work on this plugin, you can follow the steps below. Note that the plugin was tested on a Spigot server, but from what I learned from the forums, Spigot is built on the Bukkit API which is also used by other kind of servers like Paper. That said, the plugin should technically work on any Bukkit server, but feel free to correct me if I'm wrong as I do not have much experience with Minecraft plugin development.

1. Clone the repo
2. Do some modifications
3. Run the "package" command (Maven) to build the plugin's jar
4. Place the resulting jar in the `plugins` folder on your Spigot server

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)

## TODO

Here's a list of things I wish to add in the future. They may or may not be implemented, but would reinforce the plugin's accessibility and be more appealing to other server owners.

### Doors

Code locks on doors works. However, when not authorized and interacting with it opens them for a fraction of a millisecond (not really, but super briefly). It is fast enough for the user to stay locked out, but not fast enough not to notice it. It would be nice if the door would not open at all and do like chest doesn't open (or make sound).

### Configuration

As stated multiple times, the whole plugin is designed around Rust's code lock. The first goal of the plugin development was to replicate it. Now that it has been done, it could be interesting for the users to tweak some of this "design". Simple examples of this would be to set a custom code length other than 4 or use the `LockedBlock.BLOCKTYPE_WHITELIST` (which has nothing in at the moment) and whitelist block types which can't be locked normally.

### Code Lock Cracker

This is just an idea off the top of my head and is more of a COULD rather than a SHOULD. An item which would be significantly costly to make but would allow players to place the cracker on a locked entity, wait a minute or two and break the code lock.

### i18n

Currently, everything is in English. It's common for plugins to be offered in other languages. To achieve this, any English string **that can be displayed to the user** (via message) should be stored in variables that can be dynamically changed.
