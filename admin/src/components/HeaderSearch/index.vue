<template>
  <div :class="{ show: show }" class="header-search">
    <i class="iconfont iconios-search" style="font-size: 20px" @click.stop="click"></i>
    <!--<svg-icon class-name="search-icon" icon-class="search" @click.stop="click" />-->
    <el-select
      ref="headerSearchSelectRef"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="搜索菜单"
      class="header-search-select"
      @change="change"
    >
      <el-option v-for="item in options" :key="item.url" :value="item" :label="item.name.join(' > ')" />
    </el-select>
  </div>
</template>

<script setup>
// fuse is a lightweight fuzzy-search module
// make search results more in line with expectations
import { ref, watch, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import Fuse from 'fuse.js';
import path from 'path';
import { usePermissionStore } from '@/store/modules/permission';

defineOptions({ name: 'HeaderSearch' });

const router = useRouter();
const permissionStore = usePermissionStore();

const search = ref('');
const options = ref([]);
const searchPool = ref([]);
const show = ref(false);
const fuse = ref(undefined);
const headerSearchSelectRef = ref(null);

// 原 mapGetters(['permission_routes']) → permissionStore.routes
function routes() {
  return permissionStore.routes;
}

watch(
  routes,
  () => {
    searchPool.value = generateRoutes(permissionStore.routes);
  },
);

watch(searchPool, (list) => {
  initFuse(list);
});

watch(show, (value) => {
  if (value) {
    document.body.addEventListener('click', close);
  } else {
    document.body.removeEventListener('click', close);
  }
});

onMounted(() => {
  searchPool.value = generateRoutes(permissionStore.routes);
});

function click() {
  show.value = !show.value;
  if (show.value) {
    headerSearchSelectRef.value && headerSearchSelectRef.value.focus();
  }
}

function close() {
  headerSearchSelectRef.value && headerSearchSelectRef.value.blur();
  options.value = [];
  show.value = false;
}

function change(val) {
  router.push(val.path);
  search.value = '';
  options.value = [];
  nextTick(() => {
    show.value = false;
  });
}

function initFuse(list) {
  fuse.value = new Fuse(list, {
    shouldSort: true,
    threshold: 0.4,
    location: 0,
    distance: 100,
    maxPatternLength: 32,
    minMatchCharLength: 1,
    keys: [
      {
        name: 'name',
        weight: 0.7,
      },
      {
        name: 'url',
        weight: 0.3,
      },
    ],
  });
}

// Filter out the routes that can be displayed in the sidebar
// And generate the internationalized title
function generateRoutes(routesArg, basePath = '/', prefixTitle = []) {
  let res = [];
  for (const routerItem of routesArg) {
    // skip hidden router
    if (routerItem.hidden) {
      continue;
    }

    const data = {
      path: path.resolve(basePath, routerItem.url),
      name: [...prefixTitle],
      children: routerItem.child || [],
    };

    if (routerItem.name) {
      data.name = [...data.name, routerItem.name];

      if (routerItem.redirect !== 'noRedirect') {
        // only push the routes with title
        // special case: need to exclude parent router without redirect
        res.push(data);
      }
    }

    // recursive child routes
    if (routerItem.child) {
      const tempRoutes = generateRoutes(routerItem.child, data.url, data.name);
      if (tempRoutes.length >= 1) {
        res = [...res, ...tempRoutes];
      }
    }
  }
  return res;
}

function querySearch(query) {
  if (query !== '') {
    options.value = fuse.value.search(query);
  } else {
    options.value = [];
  }
}
</script>

<style lang="scss" scoped>
.header-search {
  font-size: 0 !important;
  display: inline-flex !important;
  cursor: pointer;
  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    /*vertical-align: middle;*/
    line-height: 50px;
    :deep(.el-input__inner) {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      /*border-bottom: 1px solid #d9d9d9;*/
      vertical-align: middle;
    }
  }

  &.show {
    .header-search-select {
      width: 210px;
      margin-left: 10px;
    }
  }
}
</style>
