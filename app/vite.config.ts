import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { TanStackRouterVite } from '@tanstack/router-plugin/vite'
import tailwindcss from '@tailwindcss/vite'
import path from "path";

export default defineConfig({
  plugins: [
    TanStackRouterVite({
      autoCodeSplitting: false,
      target: 'react',
      routesDirectory: './src/routes',
      generatedRouteTree: './src/route-tree.gen.ts',
      quoteStyle: 'single',
      semicolons: true,
      routeFileIgnorePrefix: '-',
    }), 
    react(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@ui': path.resolve(__dirname, 'src/shared/ui'),
      '@hooks': path.resolve(__dirname, 'src/shared/hooks'),
      '@lib': path.resolve(__dirname, 'src/shared/lib'),
      '@features': path.resolve(__dirname, 'src/features'),
      '@page': path.resolve(__dirname, 'src/page'),
    }
  }
});
