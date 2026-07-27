# ADR 003: Command Pattern

## Context
The shell needs to parse user input and execute corresponding commands. Hardcoding `if-else` blocks for every command in the shell parser would violate the Open/Closed Principle.

## Decision
We utilize the Command Pattern. Every command implements the `Command` interface (`execute`, `getName`, `getDescription`) and registers itself dynamically with the `CommandRegistry`.

## Trade-offs and Consequences
- **Pros**: Adding new commands is trivial (just implement the interface and register). The Shell parser remains completely decoupled from command logic.
- **Cons**: Requires managing a central registry and initializing commands with their dependent Services.
