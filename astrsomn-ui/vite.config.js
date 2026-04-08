import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': resolve(__dirname, 'src'),
        },
    },
    server: {
        port: 3000,
        proxy: {
            // 开发代理：浏览器请求 /api/xxx → 转发为后端 /xxx（后端业务路径不带 /api 前缀）
            '/api': {
                target: 'http://localhost:4481',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/api/, ''); }
            },
            '/v1': {
                target: 'http://localhost:4481',
                changeOrigin: true
            }
        }
    }
});
