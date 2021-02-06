import { storeStaffListApi } from '@/api/storePoint'
import {  seckillListApi } from '@/api/marketing'
import Cookies from 'js-cookie'

/**
 * @description 确定操作弹框
 */
export function modalSure(title) {
  return new Promise((resolve, reject) => {
    this.$confirm(`确定${title || '永久删除该文件'}`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      resolve()
    }).catch(() => {
      reject()
      this.$message({
        type: 'info',
        message: '已取消'
      })
    })
  })
}

/**
 * @description 短信是否登录
 */
export function isLogin() {
  return new Promise((resolve, reject) => {
    isLoginApi().then(async res => {
      resolve(res)
    }).catch(res => {
      reject(res)
    })
  })
}

/**
 * @description 核销员列表
 */
export function getStoreStaff() {
  return new Promise((resolve, reject) => {
    storeStaffListApi({page: 1, limit: 9999}).then(async res => {
      localStorage.setItem('storeStaffList', res.list ? JSON.stringify(res.list) : [])
    })
  })
}

/**
 * @description 秒杀配置列表
 */
export function getSeckillList(status) {
  return new Promise((resolve, reject) => {
    seckillListApi({page: 1, limit: 9999, isDel: false, status: status || null}).then(async res => {
      resolve(res)
    })
  })
}
