export function parseJson(value, fallback) {
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

export function normalizeThemeInfo(data = {}, type) {
  let value = parseJson(data, data || {});
  if (type === 'category' && (typeof value === 'number' || typeof value === 'string')) {
    value = { status: Number(value) || 0 };
  }
  if (!value || typeof value !== 'object') return value;

  const configValue = parseJson(value.value, value.value || {});
  const normalized = {
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

  if (type === 'theme' && value.themeData) {
    return {
      ...normalized,
      ...parseJson(value.themeData, {}),
    };
  }
  return normalized;
}

export function normalizeTheme(item = {}) {
  const themeData = parseJson(item.themeData, item.themeData || item.theme_data || {});
  return {
    ...item,
    add_time: item.addTime || item.add_time || '',
    category_data_id: item.categoryDataId || item.category_data_id || 0,
    category_data_update_time: item.categoryDataUpdateTime || item.category_data_update_time || '',
    category_default_image: item.categoryDefaultImage || item.category_default_image || '',
    category_image: item.categoryImage || item.category_image || '',
    detail_data_id: item.detailDataId || item.detail_data_id || 0,
    detail_data_update_time: item.detailDataUpdateTime || item.detail_data_update_time || '',
    detail_default_image: item.detailDefaultImage || item.detail_default_image || '',
    detail_image: item.detailImage || item.detail_image || '',
    home_data_id: item.homeDataId || item.home_data_id || 0,
    home_data_update_time: item.homeDataUpdateTime || item.home_data_update_time || '',
    home_default_image: item.homeDefaultImage || item.home_default_image || '',
    home_image: item.homeImage || item.home_image || '',
    is_del: item.isDel || item.is_del || 0,
    is_use: item.isUse || item.is_use || 0,
    page_type: item.pageType || item.page_type || 'theme',
    theme_data: themeData,
    theme_data_id: item.themeDataId || item.theme_data_id || 0,
    theme_data_update_time: item.themeDataUpdateTime || item.theme_data_update_time || '',
    up_time: item.upTime || item.up_time || '',
    user_data_id: item.userDataId || item.user_data_id || 0,
    user_data_update_time: item.userDataUpdateTime || item.user_data_update_time || '',
    user_default_image: item.userDefaultImage || item.user_default_image || '',
    user_image: item.userImage || item.user_image || '',
  };
}

export function normalizeThemeList(page = {}) {
  const list = Array.isArray(page.list) ? page.list.map(normalizeTheme) : [];
  const data = {
    ...page,
    list,
    count: page.total || page.count || 0,
  };
  return {
    ...data,
    data,
  };
}

export function normalizeUsingTheme(data = {}) {
  const themeData = parseJson(data.themeData, data.themeData || data.theme_data || {});
  const dataInfo = parseJson(data.dataInfo, data.dataInfo || data.data_info || []);
  return {
    ...normalizeTheme(data),
    data_info: Array.isArray(dataInfo)
      ? dataInfo.map((item) => ({
          ...item,
          update_time: item.updateTime || item.update_time || '',
        }))
      : [],
    theme_data: themeData,
    confuse: data.confuse || false,
  };
}

export function toThemeListParams(params = {}) {
  return {
    page: params.page,
    limit: params.limit,
    title: params.title,
    type: params.type,
    isUse: params.isUse,
    isDel: params.isDel,
    pageType: params.pageType || params.page_type,
  };
}

export function normalizeThemeCategoryTree(list = []) {
  if (!Array.isArray(list)) return [];
  return list.map((item = {}) => {
    const children = normalizeThemeCategoryTree(item.children || item.child || item.childList || []);
    const title = item.title || item.label || item.name || '';
    const value = item.value !== undefined && item.value !== null ? item.value : item.id;
    const normalized = {
      ...item,
      id: item.id !== undefined && item.id !== null ? item.id : value,
      value,
      title,
      label: item.label || title,
      name: item.name || title,
      child: children.length ? children : undefined,
      children: children.length ? children : undefined,
    };
    return normalized;
  });
}
