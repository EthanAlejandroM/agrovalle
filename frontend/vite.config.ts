import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // En local, /api se reenvía al backend de Spring Boot: el navegador solo habla con
    // localhost:5173 y no hace falta configurar CORS en el backend.
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
})
