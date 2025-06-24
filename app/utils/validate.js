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
 * 验证小数点后两位及多个小数
 * money 金额
 */
export function isMoney(money) {
	var reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/
	if (reg.test(money)) {
		return true
	} else {
		return false
	}
}

/**
 * 验证手机号码
 * money 金额
 */
export function checkPhone(phone) {
	var reg = /^1(3|4|5|6|7|8|9)\d{9}$/
	if (reg.test(phone)) {
		return true
	} else {
		return false
	}
}

/**
 * 函数防抖 (只执行最后一次点击)
 * @param fn
 * @param delay
 * @returns {Function}
 * @constructor
 */
export const Debounce = (fn, t) => {
	const delay = t || 500
	let timer
	return function() {
		const args = arguments
		if (timer) {
			clearTimeout(timer)
		}
		timer = setTimeout(() => {
			timer = null
			fn.apply(this, args)
		}, delay)
	}
}

// 节流函数
export function throttle(fn, delay) {
	var lastArgs;
	var timer;
	var delay = delay || 200;
	return function(...args) {
		lastArgs = args;
		if(!timer){
			timer = setTimeout(()=>{
				timer = null;
				fn.apply(this, lastArgs);
			}, delay);
		}
	}
}