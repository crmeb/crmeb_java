<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span v-if="item.redirect === 'noRedirect' || index == levelList.length - 1" class="no-redirect">{{
          item.meta.title
        }}</span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import pathToRegexp from 'path-to-regexp';

const route = useRoute();
const router = useRouter();

const levelList = ref(null);

function getBreadcrumb() {
  // only show routes with meta.title
  let matched = route.matched.filter((item) => item.meta && item.meta.title);
  const first = matched[0];

  if (!isDashboard(first)) {
    matched = [{ path: '/dashboard', meta: { title: '主页' } }].concat(matched);
  }

  levelList.value = matched.filter((item) => item.meta && item.meta.title && item.meta.breadcrumb !== false);
}

function isDashboard(routeItem) {
  const name = routeItem && routeItem.name;
  if (!name) {
    return false;
  }
  return name.trim().toLocaleLowerCase() === 'Dashboard'.toLocaleLowerCase();
}

function pathCompile(path) {
  // To solve this problem https://github.com/PanJiaChen/vue-element-admin/issues/561
  const { params } = route;
  const toPath = pathToRegexp.compile(path);
  return toPath(params);
}

function handleLink(item) {
  const { redirect, path } = item;
  if (redirect) {
    router.push(redirect);
    return;
  }
  router.push(pathCompile(path));
}

watch(
  () => route,
  (r) => {
    // if you go to the redirect page, do not update the breadcrumbs
    if (r.path.startsWith('/redirect/')) {
      return;
    }
    getBreadcrumb();
  },
);

getBreadcrumb();
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
