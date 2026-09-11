// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
// 主题色逻辑，替代原 mixins/color.js。
// 用法：
//   const { colorStyle, colorStatus } = useColor();
// 说明：过渡期 mixins/color.js 仍保留给尚未转换为 <script setup> 的
// Options API 消费者，待全部消费者迁移完成后删除该 mixin。

import { ref } from 'vue';

export function useColor() {
	const colorStyle = ref('');
	const colorStatus = ref('');

	// 等价于原 mixin 的 created 钩子
	colorStyle.value = uni.getStorageSync('viewColor');
	uni.$on('ok', (data) => {
		colorStyle.value = data;
	});

	return {
		colorStyle,
		colorStatus
	};
}

export default useColor;
