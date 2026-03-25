# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
npm install       # Install dependencies
npm run dev       # Start dev server (http://localhost:3000)
npm run build     # Production build
npm run lint      # ESLint check
```

> **Note:** A `next.config.js` with `turbopack.root: __dirname` is required to avoid Turbopack workspace-root inference errors in this monorepo. It already exists — do not remove it.

## Route Structure

Two route groups with separate layouts:

- `app/(auth)/` — Unauthenticated pages (no header/footer)
  - `/login` — ID/password form → redirects to `/verify`
  - `/verify` — Phone OTP verification with 3-minute countdown
- `app/(main)/` — Authenticated pages (Header + Footer shell)
  - `/` — Dashboard with stat cards and system notices
  - `/users` — User list with search/pagination
  - `/users/[id]` — User detail view

**Auth flow is currently frontend-only (no real API calls).** Login simply checks that both fields are non-empty, then navigates to `/verify`.

## Component Architecture

Three-tier atomic design under `components/`:

```
atoms/       Card, Input, Button, Select, Badge
molecules/   Form, Navbar, ConfirmModal, Modal, Table, Pagination
layout/      Container, Header, Footer, ListLayout
```

All exports are re-exported from `components/index.ts` — import from `@/components` (not deep paths).

**`ListLayout`** is the standard template for list pages. It composes Select + Input + Button search bar, a generic `Table<T>`, and `Pagination` from a single prop interface. Use it for any new list page.

**`Table<T>`** is generic — define `TableColumn<T>[]` with a `render` function per column. `rowKey` and `onRowClick` are required for interactive rows.

## Navigation / Menus

Navigation items are hardcoded in `components/layout/Header/Header.tsx` as `menuItems: NavigationItem[]`. Adding a new top-level menu or sub-menu requires editing that array directly. The `NavigationItem` type (in `types/index.ts`) supports nested `subItems`.

## Types & Path Aliases

- `types/index.ts` — shared domain types (`User`, `NavigationItem`)
- `@/*` maps to the project root (e.g., `@/types`, `@/components`)
- `strict: false` in tsconfig — type assertions are lenient

## Data

All data is currently hardcoded mock arrays inside each page component. There is no API client or data-fetching layer yet.