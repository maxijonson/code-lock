# CodeLock

CodeLock is a Minecraft plugin (and my first plugin made ever!). It allows players to lock various entities with a code lock. Inspired by Rust (game), the code can only be entered once and then never again. This makes it easier to lock things without having to worry about entering the code every time.

### Features

#### Enter and forget

Enter the code once when setting the lock and never do it again! The lock will still lock other players who did not enter the code, but you won't even notice it is there.

#### Guest codes (TODO)

Set another code that can be given to your friends so they can have access to the entity. However, guests may not change, unlock or remove the code lock. At any time, the guest code can be removed.

### Usage

#### Locking an entity

1. Craft the code lock (TODO: Recipe)
2. With code lock in hand, look at the entity, crouch and right click to place the lock.
3. Look at the lock, crouch and right click to set the code

### Motivation

I had just created my first Minecraft server on a Raspberry Pi 4B using Spigot and wanted to add a plugin that would lock things with a code lock. However, I found it annoying and redundant to enter the code every time, which discouraged the use of the code lock on things I would frequently use. Since I am a huge Rust player, I wanted something similar to their code lock. Thus, CodeLock was born!