// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { defineStore } from 'pinia';
import { ref } from 'vue';
import Cache from '@/plugins/cache';

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref(Cache.local.getJSON('visitedViews') || []);
  const cachedViews = ref([]);

  function addVisitedView(view) {
    if (visitedViews.value.some((v) => v.path === view.path)) return;
    visitedViews.value.push(
      Object.assign(
        {},
        {
          path: view.path,
          query: view.query,
          fullPath: view.fullPath,
          meta: view.meta,
        },
        {
          title: view.meta.title || 'no-name',
        },
      ),
    );
    Cache.local.setJSON('visitedViews', [...visitedViews.value]);
  }

  function addCachedView(view) {
    if (cachedViews.value.includes(view.name)) return;
    if (!view.meta.noCache) {
      cachedViews.value.push(view.name);
    }
  }

  function addView(view) {
    addVisitedView(view);
    addCachedView(view);
  }

  function delVisitedView(view) {
    return new Promise((resolve) => {
      for (const [i, v] of visitedViews.value.entries()) {
        if (v.path === view.path) {
          visitedViews.value.splice(i, 1);
          break;
        }
      }
      resolve([...visitedViews.value]);
    });
  }

  function delCachedView(view) {
    return new Promise((resolve) => {
      const index = cachedViews.value.indexOf(view.name);
      if (index > -1) cachedViews.value.splice(index, 1);
      resolve([...cachedViews.value]);
    });
  }

  function delView(view) {
    return new Promise((resolve) => {
      delVisitedView(view);
      delCachedView(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delOthersVisitedViews(view) {
    visitedViews.value = visitedViews.value.filter((v) => {
      return v.meta.affix || v.path === view.path;
    });
    Cache.local.setJSON('visitedViews', [...visitedViews.value]);
  }

  function delOthersCachedViews(view) {
    const index = cachedViews.value.indexOf(view.name);
    if (index > -1) {
      cachedViews.value = cachedViews.value.slice(index, index + 1);
    } else {
      cachedViews.value = [];
    }
  }

  function delOthersViews(view) {
    return new Promise((resolve) => {
      delOthersVisitedViews(view);
      delOthersCachedViews(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function delAllVisitedViews() {
    return new Promise((resolve) => {
      const affixTags = visitedViews.value.filter((tag) => tag.meta.affix);
      visitedViews.value = affixTags;
      Cache.local.setJSON('visitedViews', [...visitedViews.value]);
      resolve([...visitedViews.value]);
    });
  }

  function delAllCachedViews() {
    return new Promise((resolve) => {
      cachedViews.value = [];
      resolve([...cachedViews.value]);
    });
  }

  function delAllViews() {
    return new Promise((resolve) => {
      delAllVisitedViews();
      delAllCachedViews();
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value],
      });
    });
  }

  function updateVisitedView(view) {
    for (let i = 0; i < visitedViews.value.length; i++) {
      if (visitedViews.value[i].path === view.path) {
        visitedViews.value[i] = Object.assign(visitedViews.value[i], view);
        break;
      }
    }
  }

  return {
    visitedViews,
    cachedViews,
    addView,
    addVisitedView,
    addCachedView,
    delView,
    delVisitedView,
    delCachedView,
    delOthersViews,
    delOthersVisitedViews,
    delOthersCachedViews,
    delAllViews,
    delAllVisitedViews,
    delAllCachedViews,
    updateVisitedView,
  };
});
