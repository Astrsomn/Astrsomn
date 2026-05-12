<template>
  <AdminModuleShell :groups="adminUnifiedNav">
    <router-view v-slot="{ Component }">
      <transition name="module-shell-fade" mode="out-in">
        <component :is="Component" :key="routeKey" />
      </transition>
    </router-view>
  </AdminModuleShell>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AdminModuleShell from '@/views/admin/layout/AdminModuleShell.vue';
import { adminUnifiedNav } from '@/views/admin/layout/adminUnifiedNav.ts';

const route = useRoute();
const routeKey = computed(() => route.path);
</script>
