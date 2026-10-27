---
name: graphify
description: >-
  Architectural intelligence and code graph querying guide. Uses graph analysis to map dependencies, model relationships, controller mappings, and route flows across the codebase without grepping raw files.
---

# Graphify Architectural Intelligence

Graphify parses the repository structure and creates an indexed knowledge graph of types, functions, controllers, database models, and route mappings.

## Core Capabilities
1. **Dependency Mapping**:
   - Trace controllers -> services -> repositories -> entities.
   - Detect circular dependencies and architectural violations.
2. **Targeted Subgraph Querying**:
   - Query specific domain subgraphs (e.g., `events`, `registrations`, `security`, `venues`).
   - Read `GRAPH_REPORT.md` for high-level module architecture overview.
3. **OpenCode Integration**:
   - The plugin at [.opencode/plugins/graphify.js](file:///e:/college%20event%20managment%20sys/.opencode/plugins/graphify.js) automatically injects graph awareness before bash tool runs.

## Usage Guidelines
- When analyzing dependencies in the Java Spring Boot backend:
  - Controller Layer: [EventController](file:///e:/college%20event%20managment%20sys/src/main/java/com/college/eventmanagement/controller/EventController.java), [RegistrationController](file:///e:/college%20event%20managment%20sys/src/main/java/com/college/eventmanagement/controller/RegistrationController.java)
  - Service Layer: [EventServiceImpl](file:///e:/college%20event%20managment%20sys/src/main/java/com/college/eventmanagement/service/impl/EventServiceImpl.java), [RegistrationServiceImpl](file:///e:/college%20event%20managment%20sys/src/main/java/com/college/eventmanagement/service/impl/RegistrationServiceImpl.java)
  - Data Layer: [EventRepository](file:///e:/college%20event%20managment%20sys/src/main/java/com/college/eventmanagement/repository/EventRepository.java), [RegistrationRepository](file:///e:/college%20event%20managment%20sys/src/main/java/com/college/eventmanagement/repository/RegistrationRepository.java)
