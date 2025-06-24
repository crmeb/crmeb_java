// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { copyrightInfoApi } from '@/api/authInformation';
/**
 * @description 短信是否登录
 */
export function isLogin() {
  return new Promise((resolve, reject) => {
    isLoginApi()
      .then(async (res) => {
        resolve(res);
      })
      .catch((res) => {
        reject(res);
      });
  });
}

/**
 * @description 获取授权
 */
export function getCopyrightInfo() {
  return new Promise((resolve, reject) => {
    copyrightInfoApi()
      .then(async (res) => {
        resolve(res);
      })
      .catch((res) => {
        reject(res);
      });
  });
}

/**
 * @description 表格列表中删除最后一页中的唯一一个数据的操作
 */
export function handleDeleteTable(length, tableFrom) {
  if (length === 1 && tableFrom.page > 1) return (tableFrom.page = tableFrom.page - 1);
}
