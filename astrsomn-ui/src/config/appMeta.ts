/** 与根目录 package.json 同步，由 vite.config define 注入；类型在 env.d.ts 的 ImportMetaEnv */

export const appPkgName = import.meta.env.VITE_APP_PKG_NAME
export const appPkgVersion = import.meta.env.VITE_APP_PKG_VERSION
