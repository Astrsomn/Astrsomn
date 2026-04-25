<template>
  <router-view />
  <footer class="global-footer">
    <span class="copyright">© 2026 Astrsomn. All rights reserved.</span>
    <span class="divider">|</span>
    <span class="version">版本 {{ appPkgVersion }}</span>
  </footer>
</template>

<script setup lang="ts">
import { appPkgVersion } from '@/config/appMeta'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}

.global-footer {
  position: fixed;
  bottom: 0px;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: right;
  gap: 12px;

  background: transparent;
  z-index: 1000;
}

.copyright, .version {
  font-size: 12px;
  color: #94a3b8;
}

.divider {
  color: #e2e8f0;
}
</style>
