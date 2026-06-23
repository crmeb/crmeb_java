// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

/**
 * 弹窗样式的表单配置的提交
 * @param title 标题
 * @param formName 表单name
 * @param isCreate 是否是编辑
 * @param editData 详情数据
 * @param callback 回调函数
 * @param keyNum 重置表单key值
 * @returns {Promise<any>}
 */
export default function modalParserFrom(title, formName, isCreate, editData, callback, keyNum) {
  const h = this.$createElement;
  return new Promise((resolve, reject) => {
    this.$msgbox({
      title,
      customClass: 'upload-form',
      closeOnClickModal: false,
      showClose: true,
      message: h('div', { class: 'parserFrom_modal'}, [
        h('ZBParser', {
          props: {
            formName,
            isCreate,
            editData,
            keyNum,
          },
          on: {
            submit(formValue) {
              callback(formValue);
            }
          },
        }),
      ]),
      showCancelButton: false,
      showConfirmButton: false,
    })
      .then(() => {
        resolve();
      })
      .catch(() => {
        reject();
        // this.$message({
        //   type: 'info',
        //   message: '已取消',
        // });
      });
  });
}
