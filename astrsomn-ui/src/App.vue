<template>
  <div class="app-root">
    <router-view/>
    <SystemMessageNoticeCenter/>
    <footer class="global-footer">
      <span class="copyright">{{ t.footer.copyright }}</span>
      <span class="divider">|</span>
      <span class="version">版本 v{{ appConfig.version }}</span>
    </footer>
  </div>
</template>

<script lang="ts" setup>
import {onMounted} from 'vue'
import {appConfig} from '@/config/config.ts'
import SystemMessageNoticeCenter from '@/components/global/SystemMessageNoticeCenter.vue'
import {useGuide} from '@/composables/useGuide.ts'
import {usePageTranslation} from '@/locales/pages.ts'
import 'driver.js/dist/driver.css'

const {initAutoGuide} = useGuide()
const t = usePageTranslation('app')

onMounted(() => {
  initAutoGuide()
})
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

.app-root {
  width: 100%;
  height: 100%;
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
