import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import { visualizer } from 'rollup-plugin-visualizer'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const isProd = mode === 'production'
  
  return {
    plugins: [
      vue(),
      // Bundle visualizer for analyzing bundle size
      isProd && visualizer({
        open: false,
        gzipSize: true,
        brotliSize: true,
        filename: 'dist/stats.html'
      })
    ],
    resolve: {
      alias: {
        '@': resolve(__dirname, './src'),
        '#': resolve(__dirname, './types')
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/variables" as *;`,
        }
      },
      // CSS 代码分割
      codeSplit: true
    },
    build: {
      // 启用 CSS 代码分割
      cssCodeSplit: true,
      // 启用 source map
      sourcemap: !isProd,
      // 压缩选项
      minify: isProd ? 'terser' : false,
      terserOptions: {
        compress: {
          drop_console: isProd,
          drop_debugger: isProd,
          pure_funcs: isProd ? ['console.log', 'console.info'] : []
        },
        mangle: {
          safari10: true
        }
      },
      // 代码分割配置
      rollupOptions: {
        output: {
          // 入口文件命名
          entryFileNames: 'js/[name]-[hash].js',
          // 代码分割后的文件命名
          chunkFileNames: 'js/[name]-[hash].js',
          // 资源文件命名
          assetFileNames: (assetInfo) => {
            const info = assetInfo.name.split('.')
            const ext = info[info.length - 1]
            if (/\.(css|scss|sass)$/.test(assetInfo.name)) {
              return 'css/[name]-[hash][extname]'
            }
            if (/\.(png|jpe?g|gif|svg|webp|ico)$/.test(assetInfo.name)) {
              return 'images/[name]-[hash][extname]'
            }
            if (/\.(woff2?|eot|ttf|otf)$/.test(assetInfo.name)) {
              return 'fonts/[name]-[hash][extname]'
            }
            return 'assets/[name]-[hash][extname]'
          },
          // 手动代码分割
          manualChunks: {
            // Vue 核心库
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            // UI 组件库
            'ui-vendor': ['element-plus', '@element-plus/icons-vue'],
            // 工具库
            'utils-vendor': ['axios', 'dayjs', 'uuid']
          }
        }
      },
      // 构建报告
      reportCompressedSize: true,
      // 块大小警告限制
      chunkSizeWarningLimit: 1000
    },
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'pinia',
        'element-plus',
        '@element-plus/icons-vue',
        'axios',
        'dayjs'
      ],
      exclude: []
    },
    server: {
      port: 3003,
      open: true,
      cors: true,
      hmr: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '/api')
        }
      }
    },
    preview: {
      port: 5000,
      open: true
    }
  }
})
