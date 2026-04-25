/// <reference types="vite/client" />

/**
 * 在 vite.config 的 define 中自 package.json 注入，见 src/config/appMeta.ts
 * 发版后请把下方示例版本与 package.json 的 "version" 对齐（当前：0.0.1）
 */
interface ImportMetaEnv {
  readonly VITE_APP_PKG_NAME: string
  /** 应用版本，与根目录 package.json 的 "version" 相同（如 0.0.1） */
  readonly VITE_APP_PKG_VERSION: string
}

import 'vue-router';

declare module 'vue-router' {
  interface RouteMeta {
    title?: string;
    requiresAuth?: boolean;
    /** 显示底部 Dock（仅一级工作台 / 指定入口） */
    showAdminDock?: boolean;
    /** 显示本模块侧栏 */
    showModuleSidebar?: boolean;
  }
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'

  const component: DefineComponent<Record<string, never>, Record<string, never>, unknown>
  export default component
}
