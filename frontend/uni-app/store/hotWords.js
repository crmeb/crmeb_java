// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
// 兼容原代码中对 Vuex 命名空间模块 hotWords 的调用（原 store 中实际未定义该模块，
// 此处补齐以保留调用行为，避免迁移时报错）。

import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useHotWordsStore = defineStore('hotWords', () => {
	const hotWord = ref([]);

	function setHotWord(list) {
		hotWord.value = list;
	}

	return { hotWord, setHotWord };
});
