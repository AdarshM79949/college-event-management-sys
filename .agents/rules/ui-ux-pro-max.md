# UI UX Pro Max - Design Intelligence Rules

These rules enforce modern, top-tier aesthetic design for all frontend web components and pages.

## Core Design Principles
1. **Visual Hierarchy & Typography**:
   - Never use generic system defaults alone. Use curated Google Fonts (`Inter`, `Plus Jakarta Sans`, `Outfit`, `Space Grotesk`).
   - Use modular scale: `h1` (2.5rem - 3.5rem, bold/black), `h2` (1.75rem - 2.25rem), `h3` (1.25rem - 1.5rem), body (0.95rem - 1rem, line-height 1.6), caption (0.75rem - 0.85rem).
   - Crisp letter-spacing on uppercase badges and section tags (`letter-spacing: 0.08em`).

2. **Color Palette & Contrast**:
   - Dark Theme Palette: Deep slate/zinc backdrop (`#090d16`, `#0f172a`), translucent surface cards (`rgba(30, 41, 59, 0.7)`), glowing primary accents (Electric Indigo `#6366f1`, Neon Violet `#a855f7`, Vivid Cyan `#06b6d4`, Emerald `#10b981`).
   - Light Theme Palette: Crisp off-white (`#f8fafc`, `#f1f5f9`), frosted glass cards (`rgba(255, 255, 255, 0.85)`), deep text contrast (`#0f172a`).
   - Never use raw `#ff0000` or `#00ff00`. Use refined semantics: Emerald `#10b981` (Success), Amber `#f59e0b` (Warning), Rose `#f43f5e` (Danger), Indigo `#6366f1` (Info).

3. **Glassmorphism & Surface Elevation**:
   - Backdrop filter blur: `backdrop-filter: blur(12px) saturate(160%)`.
   - Subtle borders: `border: 1px solid rgba(255, 255, 255, 0.1)` on dark, `border: 1px solid rgba(0, 0, 0, 0.08)` on light.
   - Multi-layer drop shadows for depth: `box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.3)`.

4. **Micro-Interactions & Fluid Motion**:
   - Smooth transitions: `transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1)`.
   - Interactive hover cards: Subtle `translateY(-4px)` with shadow bloom on hover.
   - Button ripple / pulse effects and glowing focus rings (`box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.35)`).

5. **Accessibility & Responsive Layouts**:
   - Fluid grid layouts with CSS Grid / Flexbox (`grid-template-columns: repeat(auto-fill, minmax(320px, 1fr))`).
   - High contrast ratios (WCAG AA compliant).
   - Clean, descriptive aria labels and keyboard navigability.
