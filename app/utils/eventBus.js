// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
// 全局事件总线，替代 Vue2 的 new Vue() 事件总线 ($eventHub)。
// 采用内联的 mitt 实现（约 30 行，MIT License），避免引入需 node_modules
// 安装的 npm 依赖——HBuilderX 项目不便管理外部包，故直接内联。
// 用法：
//   import { eventBus } from '@/utils/eventBus';
//   eventBus.on('event', handler);
//   eventBus.emit('event', payload);
//   eventBus.off('event', handler);

// 内联 mitt（https://github.com/developit/mitt）
function mitt(all) {
	all = all || new Map();
	return {
		all,
		on(type, handler) {
			const handlers = all.get(type);
			if (handlers) {
				handlers.push(handler);
			} else {
				all.set(type, [handler]);
			}
		},
		off(type, handler) {
			const handlers = all.get(type);
			if (handlers) {
				if (handler) {
					handlers.splice(handlers.indexOf(handler) >>> 0, 1);
				} else {
					all.set(type, []);
				}
			}
		},
		emit(type, evt) {
			let handlers = all.get(type);
			if (handlers) {
				handlers.slice().map((handler) => {
					handler(evt);
				});
			}
			handlers = all.get('*');
			if (handlers) {
				handlers.slice().map((handler) => {
					handler(type, evt);
				});
			}
		}
	};
}

export const eventBus = mitt();

export default eventBus;
