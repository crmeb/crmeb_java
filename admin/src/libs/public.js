// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { storeStaffListApi } from '@/api/storePoint';
import { seckillListApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import Cookies from 'js-cookie';

/**
 * @description 确定操作弹框
 */
export function modalSure(title) {
  return new Promise((resolve, reject) => {
    this.$confirm(`确定${title || '永久删除该数据'}`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'sure-modal',
    })
      .then(() => {
        resolve();
      })
      .catch(() => {
        reject();
        this.$message({
          type: 'info',
          message: '已取消',
        });
      });
  });
}

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
 * @description 核销员列表
 */
export function getStoreStaff() {
  return new Promise((resolve, reject) => {
    if (checkPermi(['admin:system:staff:list'])) {
      storeStaffListApi({ page: 1, limit: 9999 }).then(async (res) => {
        localStorage.setItem('storeStaffList', res.list ? JSON.stringify(res.list) : []);
      });
    }
  });
}

/**
 * @description 秒杀配置列表
 */
export function getSeckillList(status) {
  return new Promise((resolve, reject) => {
    seckillListApi({ page: 1, limit: 9999, isDel: false, status: status || null }).then(async (res) => {
      resolve(res);
    });
  });
}

/**
 * @description 表格列表中删除最后一页中的唯一一个数据的操作
 */
export function handleDeleteTable(length, tableFrom) {
  if (length === 1 && tableFrom.page > 1) return (tableFrom.page = tableFrom.page - 1);
}
