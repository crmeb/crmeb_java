export function cmsListApi() {
  return Promise.resolve({ list: [], count: 0, data: { list: [], count: 0 } });
}

export function categoryListApi() {
  return Promise.resolve([]);
}
