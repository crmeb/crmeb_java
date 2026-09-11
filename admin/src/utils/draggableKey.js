const draggableKeyMap = new WeakMap();
let draggableKeySeed = 0;

export function getDraggableItemKey(item) {
  if (!item || (typeof item !== 'object' && typeof item !== 'function')) {
    return `draggable-${String(item)}`;
  }
  if (!draggableKeyMap.has(item)) {
    draggableKeySeed += 1;
    draggableKeyMap.set(item, `draggable-${draggableKeySeed}`);
  }
  return draggableKeyMap.get(item);
}
