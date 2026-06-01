<template>
  <AdminModuleShell :groups="adminUnifiedNav">
    <router-view v-slot="{ Component }">
      <transition mode="out-in" name="module-shell-fade">
        <component :is="Component" :key="routeKey"/>
      </transition>
    </router-view>
  </AdminModuleShell>
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import AdminModuleShell from '@/views/admin/layout/AdminModuleShell.vue';
import {useAdminUnifiedNav} from '@/views/admin/layout/utils/adminUnifiedNav.ts';

const route = useRoute();
const routeKey = computed(() => route.path);
const adminUnifiedNav = useAdminUnifiedNav();
</script>
