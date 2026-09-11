// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

module.exports = {
	productionSourceMap: false, // 生产打包时不输出map文件，增加打包速度,
	configureWebpack: config => {
		if (process.env.NODE_ENV === 'production') {
			//注释可看见打印，解开看不见
			// webpack5 下 minimizer 数组结构可能变化，遍历处理更稳妥
			const minimizers = (config.optimization && config.optimization.minimizer) || [];
			minimizers.forEach(minimizer => {
				const compress = minimizer && minimizer.options && minimizer.options.terserOptions && minimizer.options.terserOptions.compress;
				if (!compress) return;
				compress.warnings = false;
				compress.drop_console = true;
				compress.drop_debugger = true;
				compress.pure_funcs = ['console.log'];
			});
		}
	}
}
