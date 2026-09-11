import request from '@/utils/request';
import { themeProductCategory } from '@/api/theme';

function normalizeProductPage(page = {}) {
  const rawList = Array.isArray(page) ? page : page.list || [];
  const pageData = Array.isArray(page) ? {} : page;
  const list = rawList.map((item = {}) => ({
    ...item,
    store_name: item.store_name || item.storeName || item.name || '',
    cate_name: item.cate_name || item.cateName || '',
    temp_id: item.temp_id || item.tempId || '',
  }));
  const count = pageData.count || pageData.total || list.length;
  return {
    ...pageData,
    list,
    count,
    data: {
      ...pageData,
      list,
      count,
    },
  };
}

function toProductListParams(params = {}) {
  const cateId = Array.isArray(params.cate_id) ? params.cate_id.join(',') : params.cate_id;
  return {
    page: params.page,
    limit: params.limit,
    cate_id: cateId,
    store_name: params.store_name,
    priceOrder: params.priceOrder,
    salesOrder: params.salesOrder,
  };
}

export function cascaderListApi(status = 1) {
  return themeProductCategory({ status });
}

export function changeListApi(params) {
  return request({
    url: '/admin/theme/product/list',
    method: 'get',
    params: toProductListParams(params),
  }).then(normalizeProductPage);
}

export function productLabelUseListApi() {
  return Promise.resolve([]);
}

export function productGetTempKeysApi() {
  return Promise.resolve({});
}

export function uploadType() {
  return Promise.resolve({});
}
