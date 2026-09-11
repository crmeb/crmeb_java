// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import hasRole from './permission/hasRole';
import hasPermi from './permission/hasPermi';
import dialogDrag from './dialog/drag';
import dialogDragWidth from './dialog/dragWidth';
import dialogDragHeight from './dialog/dragHeight';
import copy from './copy/copy';
import dbClick from './module/dbClick';
import lazy from './module/lazy';

const install = function (app) {
  app.directive('hasRole', hasRole);
  app.directive('hasPermi', hasPermi);
  app.directive('dialogDrag', dialogDrag);
  app.directive('dialogDragWidth', dialogDragWidth);
  app.directive('dialogDragHeight', dialogDragHeight);
  app.directive('copy', copy);
  app.directive('dbClick', dbClick);
  app.directive('lazy', lazy);
};

export default install;
