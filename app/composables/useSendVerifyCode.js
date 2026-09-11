// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
// 发送验证码倒计时逻辑，替代原 mixins/SendVerifyCode.js。
// 用法：
//   const { disabled, text, sendCode } = useSendVerifyCode();

import { ref } from 'vue';

export function useSendVerifyCode() {
	const disabled = ref(false);
	const text = ref('获取验证码');

	function sendCode() {
		if (disabled.value) return;
		disabled.value = true;
		let n = 60;
		text.value = '剩余 ' + n + 's';
		const run = setInterval(() => {
			n = n - 1;
			if (n < 0) {
				clearInterval(run);
			}
			text.value = '剩余 ' + n + 's';
			if (text.value < '剩余 ' + 0 + 's') {
				disabled.value = false;
				text.value = '重新获取';
			}
		}, 1000);
	}

	return {
		disabled,
		text,
		sendCode
	};
}

export default useSendVerifyCode;
