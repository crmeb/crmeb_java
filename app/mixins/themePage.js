import { getThemeInfo } from '@/api/api.js';
import { applyTheme } from '@/utils/theme.js';

function parseJson(value, fallback) {
	if (!value) return fallback;
	if (typeof value === 'object') return value;
	try {
		return JSON.parse(value);
	} catch (e) {
		return fallback;
	}
}

function pickValue(data, camelKey, snakeKey, fallback) {
	if (data[camelKey] !== undefined && data[camelKey] !== null) return data[camelKey];
	if (data[snakeKey] !== undefined && data[snakeKey] !== null) return data[snakeKey];
	return fallback;
}

function looksLikeThemePage(data) {
	return !!(
		data &&
		typeof data === 'object' &&
		(data.value || data.type || data.title || data.is_show !== undefined || data.isShow !== undefined)
	);
}

function looksLikeComponentCollection(data) {
	if (!data || typeof data !== 'object') return false;
	const list = Array.isArray(data) ? data : Object.keys(data).map((key) => data[key]);
	return list.some((item) => item && typeof item === 'object' && (item.name || item.defaultName));
}

function normalizeThemeInfo(data = {}, type) {
	let value = parseJson(data, data || {});
	if (type === 'category' && (typeof value === 'number' || typeof value === 'string')) {
		return { status: Number(value) || 0 };
	}
	if (!value || typeof value !== 'object') return value;
	if (value.value === undefined && looksLikeComponentCollection(value)) {
		return { value };
	}

	let configValue = parseJson(value.value, value.value || {});
	let normalized = {
		...value,
		value: configValue,
		theme_color: pickValue(value, 'themeColor', 'theme_color', value.theme_color),
		gradient_color: pickValue(value, 'gradientColor', 'gradient_color', value.gradient_color),
		sub_color: pickValue(value, 'subColor', 'sub_color', value.sub_color),
		light_color: pickValue(value, 'lightColor', 'light_color', value.light_color),
		is_show: pickValue(value, 'isShow', 'is_show', value.is_show),
		is_bg_color: pickValue(value, 'isBgColor', 'is_bg_color', value.is_bg_color),
		is_bg_pic: pickValue(value, 'isBgPic', 'is_bg_pic', value.is_bg_pic),
		color_picker: pickValue(value, 'colorPicker', 'color_picker', value.color_picker),
		bg_pic: pickValue(value, 'bgPic', 'bg_pic', value.bg_pic),
		cover_pic: pickValue(value, 'coverPic', 'cover_pic', value.cover_pic),
		bg_tab_val: pickValue(value, 'bgTabVal', 'bg_tab_val', value.bg_tab_val),
	};

	if (looksLikeThemePage(configValue) && configValue.value !== undefined) {
		const innerValue = parseJson(configValue.value, configValue.value || {});
		normalized = {
			...normalized,
			...configValue,
			value: innerValue,
			is_show: pickValue(configValue, 'isShow', 'is_show', normalized.is_show),
			is_bg_color: pickValue(configValue, 'isBgColor', 'is_bg_color', normalized.is_bg_color),
			is_bg_pic: pickValue(configValue, 'isBgPic', 'is_bg_pic', normalized.is_bg_pic),
			color_picker: pickValue(configValue, 'colorPicker', 'color_picker', normalized.color_picker),
			bg_pic: pickValue(configValue, 'bgPic', 'bg_pic', normalized.bg_pic),
			cover_pic: pickValue(configValue, 'coverPic', 'cover_pic', normalized.cover_pic),
			bg_tab_val: pickValue(configValue, 'bgTabVal', 'bg_tab_val', normalized.bg_tab_val),
		};
	}

	if (type === 'theme' && (value.themeData || value.theme_data)) {
		return {
			...normalized,
			...parseJson(value.themeData || value.theme_data, {}),
		};
	}
	return normalized;
}

function hasThemeData(data) {
	return !!(data && typeof data === 'object' && Object.keys(data).length);
}

function parseQueryString(value = '') {
	return value
		.split('&')
		.reduce((params, item) => {
			const [key, val] = item.split('=');
			if (key) params[key] = val ? decodeURIComponent(val) : '';
			return params;
		}, {});
}

export default {
	data() {
		return {
			themeId: '',
			themeDiyData: null,
			themeChecked: false,
		};
	},
	methods: {
		getThemeIdFromOptions(options = {}) {
			let themeId = options.id || options.theme_id || options.themeId || '';
			// #ifdef MP
			if (!themeId && options.scene) {
				const scene = decodeURIComponent(options.scene);
				const sceneData =
					this.$util && this.$util.getUrlParams
						? this.$util.getUrlParams(scene)
						: parseQueryString(scene);
				themeId = sceneData.id || sceneData.theme_id || sceneData.themeId || '';
			}
			// #endif
			if (themeId) {
				uni.setStorageSync('previewThemeId', themeId);
				return themeId;
			}
			return uni.getStorageSync('previewThemeId') || '';
		},
		initThemePage(type, options = {}) {
			this.themeId = this.getThemeIdFromOptions(options);
			const params = {};
			if (this.themeId) params.theme_id = this.themeId;
			applyTheme(this.themeId).catch(() => {});
			return getThemeInfo(type, params)
				.then((res) => {
					const data = normalizeThemeInfo(res.data || {}, type);
					this.themeDiyData = hasThemeData(data) ? data : null;
					this.themeChecked = true;
					return data;
				})
				.catch(() => {
					this.themeDiyData = null;
					this.themeChecked = true;
				});
		},
	},
};
