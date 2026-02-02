Website (frontend)

This module contains a Vite + React frontend application.

Structure:
- index.html        - app entry (includes Bootstrap CDN)
- package.json      - npm scripts and dependencies
- vite.config.js    - Vite configuration
- src/
  - main.jsx        - React entry
  - App.jsx         - Main app with search box and product table
  - components/
    - ProductTable.jsx

Notes:
- The project root uses Gradle for JVM modules. The `website` folder is frontend-only and does not use Kotlin/Gradle tooling.
- Use `npm install` then `npm run dev` to start the dev server in the `website` directory.
