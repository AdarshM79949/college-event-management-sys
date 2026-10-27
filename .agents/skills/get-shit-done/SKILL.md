---
name: get-shit-done
description: >-
  Spec-driven development and execution framework based on gsd-build/get-shit-done. Enforces disciplined phase planning, lean context isolation, atomic verification, and reliable delivery.
---

# Get Shit Done (GSD) Execution Skill

GSD is an execution framework engineered to prevent context degradation and ensure systematic, high-velocity task completion.

## The GSD Execution Loop
1. **Discuss & Spec**:
   - Capture user requirements and lock the domain model.
   - Reference specifications like [College_Event_Management_Java_Build_Guide.md](file:///e:/college%20event%20managment%20sys/College_Event_Management_Java_Build_Guide.md).
2. **Plan**:
   - Decompose the implementation into atomic, verifiable phases (Database -> Entity -> Repository -> Service -> Controller -> UI).
3. **Execute**:
   - Build each layer with zero-assumption verification.
   - Ensure clean interfaces between layers.
4. **Verify**:
   - Check input validations, edge cases (seat limits, deadlines, waitlist promotion).
   - Test endpoints and verify UI responsiveness.
5. **Ship & Document**:
   - Provide clear walkthroughs and runnable instructions.
