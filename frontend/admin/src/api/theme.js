import request from '@/utils/request';
import {
  normalizeTheme,
  normalizeThemeInfo,
  normalizeThemeList,
  normalizeThemeCategoryTree,
  normalizeUsingTheme,
  toThemeListParams,
} from '@/utils/themeAdapter';

function normalizeSaveResult(value = {}) {
  const data = value && typeof value === 'object' ? value : { id: value };
  return {
    ...data,
    data,
    msg: '保存成功',
    message: '保存成功',
  };
}

export function getThemeList(params) {
  return request({
    url: '/admin/theme/list',
    method: 'get',
    params: toThemeListParams(params),
  }).then(normalizeThemeList);
}

export function getMicroPageList(params) {
  return getThemeList({
    ...params,
    pageType: 'micro',
  });
}

export function themeInfo(id, type) {
  return request({
    url: '/admin/theme/info',
    method: 'get',
    params: { id, type },
  }).then((data) => {
    const value = normalizeThemeInfo(data || {}, type);
    return {
      ...value,
      data: value,
    };
  });
}

export function themeSave(id, data) {
  return request({
    url: '/admin/theme/saveTheme',
    method: 'post',
    params: { id },
    data,
  }).then(normalizeSaveResult);
}

export function saveThemeTitle(id, data) {
  return request({
    url: '/admin/theme/saveTitleInfo',
    method: 'post',
    params: {
      id,
      tid: data && data.tid,
    },
    data,
  }).then(normalizeSaveResult);
}

export function saveThemeTitleInfo(data) {
  return request({
    url: '/admin/theme/saveTitleInfo',
    method: 'post',
    data,
  });
}

export function saveThemeImage(id, data) {
  return request({
    url: '/admin/theme/saveThemeImage',
    method: 'post',
    params: {
      id,
    },
    data,
  }).then(normalizeSaveResult);
}

export function fileUpload(data, params = { model: 'theme', pid: 0 }) {
  return request({
    url: '/admin/upload/image',
    method: 'post',
    params,
    data,
  }).then((value = {}) => ({
    status: 200,
    data: {
      src: value.sattDir || value.att_dir || value.url || value.src || value.fileUrl || '',
      ...value,
    },
    ...value,
  }));
}

export function importTheme(data) {
  return request({
    url: '/admin/theme/import',
    method: 'post',
    data,
  });
}

export function exportTheme(id) {
  return request({
    url: '/admin/theme/export',
    method: 'get',
    params: { id },
  });
}

export function getExportRecord(id) {
  return request({
    url: '/admin/theme/export/url',
    method: 'get',
    params: { record_id: id },
  });
}

export function useTheme(id) {
  return request({
    url: '/admin/theme/use/' + id,
    method: 'get',
  });
}

export function getThemeUsing() {
  return request({
    url: '/admin/theme/using',
    method: 'get',
  }).then((data) => {
    const value = normalizeUsingTheme(data);
    return {
      ...value,
      data: value,
    };
  });
}

export function restoreTheme(id) {
  return request({
    url: '/admin/theme/restore/' + id,
    method: 'get',
  });
}

export function useThemeData(id, params) {
  return request({
    url: '/admin/theme/use_data/' + id,
    method: 'get',
    params,
  });
}

export function deleteTheme(id) {
  return request({
    url: '/admin/theme/delete',
    method: 'get',
    params: { id },
  });
}

export function getThemeVersion(params) {
  return request({
    url: '/admin/theme/version',
    method: 'get',
    params,
  });
}

export function themeProductCategory(params = { status: -1 }) {
  return request({
    url: '/admin/theme/product/category',
    method: 'get',
    params,
  }).then((data) => {
    const list = normalizeThemeCategoryTree(data);
    return {
      data: list,
      list,
    };
  });
}

export function categoryList(params) {
  return request({
    url: '/admin/theme/product/category',
    method: 'get',
    params: {
      status: -1,
      ...params,
    },
  })
    .then((data) => {
      const list = normalizeThemeCategoryTree(data);
      return {
        data: list,
        list,
      };
    })
    .catch(() => ({ data: [], list: [] }));
}

export function getCategory() {
  return request({
    url: '/admin/category/list/tree',
    method: 'get',
  }).catch(() => []);
}

function toThemeProductParams(params = {}) {
  const cateId = Array.isArray(params.cate_id || params.cate_ids)
    ? (params.cate_id || params.cate_ids).join(',')
    : params.cate_id || params.cate_ids;
  return {
    page: params.page,
    limit: params.limit,
    cate_id: cateId,
    store_name: params.store_name,
    priceOrder: params.priceOrder,
    salesOrder: params.salesOrder,
  };
}

function normalizeThemeProductPage(data = {}) {
  const list = Array.isArray(data) ? data : data.list || [];
  const pageData = Array.isArray(data) ? {} : data;
  return {
    data: list,
    list,
    count: pageData.count || pageData.total || list.length,
  };
}

export function getProProduct(params) {
  return request({
    url: '/admin/theme/product/list',
    method: 'get',
    params: toThemeProductParams(params),
  })
    .then(normalizeThemeProductPage)
    .catch(() => ({ data: [], list: [], count: 0 }));
}

export function getProductList(params) {
  return getProProduct(params).then((res) => ({
    ...res,
    data: {
      list: res.list,
      count: res.count,
    },
  }));
}

export function getThemeProduct(params) {
  return getProProduct(params);
}

function toThemeArticleParams(params = {}) {
  return {
    page: params.page,
    limit: params.limit,
    cid: params.cid || params.pid || '',
  };
}

function normalizeThemeArticle(item = {}) {
  const imageInput = Array.isArray(item.image_input)
    ? item.image_input
    : Array.isArray(item.imageInput)
    ? item.imageInput
    : item.image
    ? [item.image]
    : [];
  return {
    ...item,
    cid: item.cid || '',
    image_input: imageInput,
    image: item.image || imageInput[0] || '',
    title: item.title || '',
    visit: item.visit || '',
    add_time: item.add_time || item.addTime || '',
  };
}

function normalizeThemeArticlePage(data = {}) {
  const pageData = Array.isArray(data) ? { list: data, count: data.length } : data || {};
  const list = Array.isArray(pageData.list) ? pageData.list.map(normalizeThemeArticle) : [];
  return {
    data: {
      list,
      count: pageData.count || pageData.total || list.length,
    },
    list,
    count: pageData.count || pageData.total || list.length,
  };
}

export function themeArticleCategory() {
  return request({
    url: '/admin/theme/article/category',
    method: 'get',
  }).then((data) => {
    const list = normalizeThemeCategoryTree(data);
    return {
      data: list,
      list,
    };
  });
}

export function getArticleList(params) {
  return request({
    url: '/admin/theme/article/list',
    method: 'get',
    params: toThemeArticleParams(params),
  })
    .then(normalizeThemeArticlePage)
    .catch(() => ({ data: { list: [], count: 0 }, list: [], count: 0 }));
}

export function getCouponList(params) {
  return request({
    url: '/admin/marketing/coupon/list',
    method: 'get',
    params,
  })
    .then((data) => data.list || data)
    .catch(() => []);
}

export function getRoutineCode() {
  return Promise.resolve({ image: '' });
}

export function diyUpdateName() {
  return Promise.resolve({});
}

export function setDefault() {
  return Promise.resolve({});
}

export function recovery() {
  return Promise.resolve({});
}

export function getMember() {
  return Promise.resolve({});
}

export function memberSave() {
  return Promise.resolve({});
}

export function getWordsAll() {
  return Promise.resolve([]);
}

export function getDiyField() {
  return request({
    url: '/admin/theme/text/field',
    method: 'get',
  }).then((data) => ({
    data: data || {},
  }));
}

export function newcomerList() {
  return Promise.resolve([]);
}

export function diyProList() {
  return Promise.resolve({ list: [], count: 0 });
}

export function diyDel() {
  return Promise.resolve({});
}

export function setStatus() {
  return Promise.resolve({});
}

export function exportDiyDataApi() {
  return Promise.resolve({});
}

export function colorChange() {
  return Promise.resolve({});
}

export function getColorChange() {
  return Promise.resolve({});
}

export function getUrl() {
  return Promise.resolve({ url: [] });
}

export function diyGetInfo() {
  return Promise.resolve({});
}

export function diySave() {
  return Promise.resolve({});
}

export { normalizeTheme, normalizeThemeInfo, normalizeThemeList, normalizeUsingTheme };
