export function liveGoods() {
  return Promise.resolve({ list: [], count: 0 });
}

export function liveGoodsList() {
  return liveGoods();
}
