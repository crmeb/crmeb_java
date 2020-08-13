import store from '@/store'

/**
 * 根据需求过滤掉treeData中的child.length === 0的数据
 * @param treeData
 * @returns {Uint8Array | BigInt64Array | any[] | Float64Array | Int8Array | Float32Array | Int32Array | Uint32Array | Uint8ClampedArray | BigUint64Array | Int16Array | Uint16Array}
 */
export function clearTreeData(treeData) {
  return treeData.map((item) => {
    if (item.child.length === 0) {
      delete item.child
    } else {
      clearTreeData(item.child)
    }
    return item
  })
}

export function addTreeListLabel(treeData) { // 因树形控件在slot-scope模式下显示字段只能为label为此自定义添加label字段和child=children
  return treeData.map((item) => {
    if ((item.name === '设置' || item.name === '管理员列表' ||
      item.name === '身份管理' || item.name === '管理权限' ||
      item.name === '管理员列表' || item.name === '权限规则') && store.getters.name !== 'admin') {
      item.disabled = true
    }
    item.label = item.name
    return item
  })
}

export function addTreeListLabelForCasCard(treeData, child) {
   treeData.map((item) => {
    if ((item.name === '设置' || item.name === '管理员列表' ||
      item.name === '身份管理' || item.name === '管理权限' ||
      item.name === '管理员列表' || item.name === '权限规则') && store.getters.name !== 'admin') {
      item.disabled = true
    }
    item.label = item.name
    return item
  })
}


