import { readFileSync } from 'node:fs'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const pkg = JSON.parse(
  readFileSync(resolve(__dirname, 'package.json'), 'utf-8')
) as { name: string; version: string }

export default defineConfig({
  plugins: [vue()],
  define: {
    'import.meta.env.VITE_APP_PKG_NAME': JSON.stringify(pkg.name),
    'import.meta.env.VITE_APP_PKG_VERSION': JSON.stringify(pkg.version)
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  build: {
    outDir: resolve(__dirname, '../astrsomn-server/src/main/resources/static'),
    emptyOutDir: true
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:4481',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/v1': {
        target: 'http://localhost:4481',
        changeOrigin: true
      }
    }
  }
})