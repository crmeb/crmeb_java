// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
export const advertisementDefault = () => {
  return {
    isShowAddBtn: true, //添加按钮
    isShowEdit: true, //删除按钮
    isShowStatus: false, //开启状态
    isShowLinkUrl: true, //链接地址
    isShowLinkUrlChose: true, //选择地址选项
    isShowImageUrl: true, //图片地址
    isShowMoreLinkUrl: false, //多条链接
    maxList: 5,
    title: '标题',
    defaultList: {
      name: '',
      imageUrl: '',
      linkUrl: '',
      id: 0,
      sort: 0,
    },
    modelMaxLength: 5,
    list: [
      {
        name: '',
        imageUrl: '',
        linkUrl: '',
        id: 0,
        sort: 0,
      },
    ],
  };
};
