const emptyTree = { list: [] };
const emptyPage = { list: [], count: 0 };

export function getCategoryListApi() {
  return Promise.resolve({ ...emptyTree, data: emptyTree });
}

export function createApi() {
  return Promise.resolve({});
}

export function categoryEditApi() {
  return Promise.resolve({});
}

export function categoryDelApi() {
  return Promise.resolve({});
}

export function fileListApi() {
  return Promise.resolve({ ...emptyPage, data: emptyPage });
}

export function moveApi() {
  return Promise.resolve({});
}

export function fileUpdateApi() {
  return Promise.resolve({});
}

export function fileDelApi() {
  return Promise.resolve({});
}

export function onlineUpload() {
  return Promise.resolve({});
}

export function scanUploadCode() {
  return Promise.resolve({});
}

export function videoCloudUpload() {
  return Promise.resolve({});
}
