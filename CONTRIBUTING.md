# Contributing to Code Lock

(Thanks to [this](https://gist.github.com/briandk/3d2e8b3ec8daf5a27a62) template used to inspire this file!)

## Introduction

Thanks for contributing! Since this project was not initially meant to become anything more than a personnal one, I did not plan to spend much time developping it. That is why contributions from actual and active Minecraft plugin developers are always welcome! This file will inform you about some guidelines I expect you to follow to maintain consistency and preserve the initial idea of the plugin. You can contribute to this project in many ways, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

There aren't much guidelines as this project is relatively small. However, you might want to read it at least quickly to find out if your contribution fits this project!

## Contributing flow (based on [Github Flow](https://guides.github.com/introduction/flow/index.html))

Pull requests are the best way to propose changes to the codebase. Here's the general flow of how this should be done:

1. Fork the repo and create your branch from `master`.
2. Make your changes
3. If you're making changes to the code make sure the plugin builds (Maven "package" command is successful), the plugin loads successfully on a Spigot server and works as intended.
4. Remove any unecessary files, such as IDE preferences, through `.gitignore` if they haven't been added already.
5. Remove any console logging (`System.out.println` and other) that are solely for development purposes
6. Issue a pull request.

## Any contributions you make will be under the ISC Software License

In short, when you submit code changes, your submissions are understood to be under the same [ISC License](http://choosealicense.com/licenses/isc/) that covers the project. Feel free to contact the maintainers if that's a concern.

## Report bugs using Github's [issues](https://github.com/maxijonson/code-lock/issues)

We use GitHub issues to track public bugs. Report a bug by [opening a new issue](https://github.com/maxijonson/code-lock/issues/new/choose); it's that easy!

## Write bug reports with detail, background, and sample code

Please follow the issue template for bugs provided when you choose your [issue type](https://github.com/maxijonson/code-lock/issues/new/choose).

**Great Bug Reports** tend to have:

- A quick summary and/or background
- Steps to reproduce
  - Be specific!
  - Give sample code if you can.
- What you expected would happen
- What actually happens
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work)

People _love_ thorough bug reports. I'm not even kidding.

## Preserve the initial purpose of the plugin

You are welcome to submit changes, but the your changes must not diverge from the initial purpose of the plugin which is designed around Rust's code lock. This mean that, **by default**, the plugin should at least respect these mechanisms:

- Enter the code once and never again (until the code is changed, of course). This is probably the most important feature and any changes to this "process" is likely to be refused.
- To change the code lock or remove it, the lock must first be in the unlocked state. This is simply to reflect the process that Rust uses.

If you're having doubts about your change, feel free to open an issue first so we can discuss about whether or not the change is relevant to this plugin.

## Use a Consistent Coding Style

- 4 spaces for indentation rather than tabs
- Line size TBD. I use the default VS Code formatter (Java Red Hat extension) and just CTRL + Alt + F to format my files.
- Prefer the [return early](https://medium.com/swlh/return-early-pattern-3d18a41bba8) pattern rather than a conditionnal hell, when possible.
  - Use `if` statements to return (exit) early when a condition is **NOT** met instead of doing code when the condition **IS** met. In short, your code should look like blocks rather than a big arrow 😜
  - Avoid `else` statements when possible, especially for large conditionnal blocks. Usually, using the early exits alone will greatly reduce the amount of `else` blocks.

## Write efficient code

Minecraft plugins can add weight to a server's ressource consumption. Odds are this plugin won't be the only one running on the server, which means it has a responsability to minimize the performance impact. Although this aspect is not easily measurable, certain coding conventions can reduce the computing footprint of the plugin, espcially in event listeners.

In no way am I pretending that the original plugin code is the most efficient. If you find computing improvements, I would be glad to be corrected! Java is not my main language, so I may not use the most "up-to-date" techniques and a second pair of eyes never hurts!

### Using early exits

Using early exits as described above (Use a consistent coding style) to do simple checks before continuing into bigger computations. Most [events](src\main\java\io\github\maxijonson\events) in this plugin use this technique to avoid unecessary lookups, notably the `Data.getInstance().getLockedBlock` method which can be an expensive lookup to do too frequently.

### Break from loops early

Break from loops early when appropriate. The [CommandManager](src\main\java\io\github\maxijonson\commands\CommandManager.java) uses this technique when searching for the appropriate command to run.

### Improve lookup times

Improve lookup times by choosing the right data structure. The [Data](src\main\java\io\github\maxijonson\data\Data.java) instance which uses `HashMap`s to store `PlayerData` and `LockedBlock`s. Note the structure of the `blocks` in this instance is not flat. Blocks are stored per world, then per chunk and finally per `LockedBlock` ID. This should (technically?) be quicker than having a simple `HashMap` keyed by `LockedBlock` ID. I chose this structure because I think it should improve lookup times by gradually eliminating LockedBlock candidates when looking for one in a specific chunk of a specific world.

### More...

Remember that not all servers run on big tech servers, some run on a Raspberry Pi! Although huge plugins may not be appropriate for a Pi, this plugin definitely should.

## License

By contributing, you agree that your contributions will be licensed under its ISC License.
