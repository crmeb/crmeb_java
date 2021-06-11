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

//加法函数，用来得到精确的加法结果
//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
//调用：$h.Add(arg1,arg2)
//返回值：arg1加上arg2的精确结果
export function Add(arg1, arg2) {
  arg2 = parseFloat(arg2);
  var r1, r2, m;
  try {
    r1 = arg1.toString().split(".")[1].length
  } catch (e) {
    r1 = 0
  }
  try {
    r2 = arg2.toString().split(".")[1].length
  } catch (e) {
    r2 = 0
  }
  m = Math.pow(100, Math.max(r1, r2));
  return (this.Mul(arg1, m) + this.Mul(arg2, m)) / m;
}

//乘法函数，用来得到精确的乘法结果
//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
//调用：$h.Mul(arg1,arg2)
//返回值：arg1乘以arg2的精确结果
export function Mul(arg1, arg2) {
  arg1 = parseFloat(arg1);
  arg2 = parseFloat(arg2);
  var m = 0,
    s1 = arg1.toString(),
    s2 = arg2.toString();
  try {
    m += s1.split(".")[1].length
  } catch (e) {}
  try {
    m += s2.split(".")[1].length
  } catch (e) {}
  return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}

//替换安全域名
export function setDomain(url) {
  url = url ? url.toString() : '';
  // 正则替换存在的转义符
  url = url.replace(/\\/g,'');
  url = window.location.protocol==='https:'? url.replace('http://','https://') : url;
  if(url.startsWith('src="')){
    url = url.replaceAll('src="','');
  }
  if(url.startsWith('//img') && window.location.protocol==='https:'){
    url = url.replace('//img','https://img');
  }
  return url;
}

// 过滤富文本中的 img 相对路径访问
export function replaceImgSrcHttps(content) {
  return content.replaceAll('src="//','src="https://');
}
