import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default ({ mode }) => {
  // Load .env files and process.env for the current mode
  const env = loadEnv(mode, process.cwd(), '')
  // Use VITE_API_URL if present, otherwise default to localhost:8080
  const apiUrl = env.VITE_API_URL || process.env.VITE_API_URL || 'http://localhost:8080'

  return defineConfig({
    plugins: [react()],
    root: '.',
    // Expose a resolved value for import.meta.env.VITE_API_URL at build time
    define: {
      'import.meta.env.VITE_API_URL': JSON.stringify(apiUrl)
    },
    server: {
      port: 5173,
      proxy: {
        // Proxy API calls to the backend during development using the same URL
        '/api': {
          target: apiUrl,
          changeOrigin: true,
          secure: false
        }
      }
    }
  })
}
