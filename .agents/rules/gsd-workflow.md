# GSD (Get Shit Done) - Agent Execution Rules

## Meta-Prompting & Execution Guidelines
1. **Spec-Driven & State Conscious**:
   - Always plan systematically before building.
   - Maintain clear task boundaries and verify each phase independently.

2. **Phase Breakdown**:
   - **Phase 1: Architecture & Design Token Definition**: Set up core variables, typography, layout containers, and mock datasets.
   - **Phase 2: Core Components & Logic**: Build atomic UI components, state management, modal handlers, and data persistence.
   - **Phase 3: Interactive Workflows**: Implement role switching (Student / Organizer / Admin), filtering, search, booking, QR badge generation, check-in scanner.
   - **Phase 4: Polish & Edge Cases**: Validate responsive behavior, dark/light theme switching, toast alerts, error boundaries, and export utilities.

3. **Context Engineering**:
   - Keep codebase modular and self-contained.
   - Avoid redundant API loops; use clean state caches in LocalStorage with fallback seed data.
