# Contributing to Wolffsohn Interactive Projects

Thank you for your interest in contributing to BorderlessMC and other Wolffsohn Interactive projects.

These guidelines are intended to keep contributions easy to review, technically consistent, and legally clear.

## Contribution License

By submitting a contribution to a Wolffsohn Interactive project, you agree that your contribution may be distributed under that project's license.

For BorderlessMC, contributions are licensed under the [PolyForm Shield License 1.0.0](LICENSE.md).

Do not submit code, assets, or other material that you do not have the right to contribute.

If your contribution includes third-party material, clearly identify the source and license in the pull request. Contributions that introduce incompatible or unclear licensing may be rejected.

## Original Work

Contributions should be your own work or material you are legally permitted to submit.

Do not copy code from another project without preserving any license, copyright, attribution, or other obligations that apply to it.

If you adapted an implementation from another project, say so in the pull request and link to the original source.

## Development Expectations

Before submitting a pull request:

- Build and test the project version or versions affected by your change.
- Keep changes focused on one issue or feature whenever practical.
- Avoid unrelated formatting or refactoring in the same pull request.
- Preserve behavior on supported loaders and Minecraft versions unless the change is specifically intended to alter that behavior.
- Document non-obvious implementation details where they would otherwise be difficult to understand.

For BorderlessMC, changes affecting fullscreen behavior should be tested on the relevant supported Minecraft version and loader whenever practical.

## Code Style

BorderlessMC generally follows conventional Java style with a few project preferences:

- Use 4 spaces for indentation, not tabs.
- Keep lines reasonably short and readable.
- Prefer clear names over shortened or ambiguous names.
- Avoid deeply nested logic when a small helper method would make the code easier to follow.
- Keep methods focused on one responsibility when practical.
- Use comments to explain why something unusual is necessary, not to restate obvious code.
- Avoid unnecessary dependencies.
- Match the style of the surrounding code when editing an existing file.

Do not rewrite working platform-specific code simply to make it look more uniform if the existing implementation is required for correct behavior.

## Mixins and Platform-Specific Code

Mixin changes should be narrowly scoped and easy to reason about.

When adding or modifying a Mixin:

- Target the smallest practical behavior.
- Avoid broad injections when a more specific injection point is available.
- Keep version-specific behavior in the appropriate Minecraft-version module when necessary.
- Explain unusual injection choices in code comments or the pull request.
- Test startup and the affected feature after changing a Mixin.

Native windowing or fullscreen changes should be treated carefully. SDL, GLFW, and platform-specific behavior can differ between Minecraft versions and operating systems.

## Pull Requests

A pull request should include:

- A short explanation of what changed.
- Why the change is needed.
- Which Minecraft versions or loaders are affected.
- How the change was tested.
- Links to related issues when applicable.

Small pull requests are usually easier to review than large changes covering multiple unrelated areas.

Maintainers may ask for changes before merging and may close contributions that do not fit the project's direction, duplicate existing work, introduce licensing concerns, or cannot be maintained reliably.

## Compatibility

BorderlessMC supports multiple Minecraft versions and loaders from a shared project structure.

When changing shared code, consider whether the change affects:

- Fabric
- Quilt
- NeoForge
- Forge
- Windows
- Linux
- macOS
- Sodium or other supported configuration integrations

Not every contribution needs to be tested on every combination, but contributors should identify what they actually tested.

## Issues and Feature Requests

Before opening an issue:

- Check whether the problem or request has already been reported.
- Include the Minecraft version, loader, BorderlessMC version, operating system, and relevant logs for bugs.
- Describe what you expected to happen and what actually happened.
- Keep one issue focused on one problem whenever possible.

## Community Standards

Participation in Wolffsohn Interactive projects is subject to the [Wolffsohn Interactive Code of Conduct](CODE_OF_CONDUCT.md).

Constructive disagreement is welcome. Personal attacks and harassment are not.
