import { getThemeInfo } from "@/api/api.js";

function parseJson(value, fallback) {
  if (!value) return fallback;
  if (typeof value === "object") return value;
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

function normalizeThemeColor(data = {}) {
  let value = parseJson(data, data || {});
  if (!value || typeof value !== "object") return {};
  if (value.themeData || value.theme_data) {
    value = {
      ...value,
      ...parseJson(value.themeData || value.theme_data, {}),
    };
  }
  if (value.value && typeof value.value !== "string") {
    value = {
      ...value,
      ...parseJson(value.value, {}),
    };
  }
  return {
    ...value,
    theme_color: pickValue(value, "themeColor", "theme_color", value.theme_color),
    gradient_color: pickValue(value, "gradientColor", "gradient_color", value.gradient_color),
    sub_color: pickValue(value, "subColor", "sub_color", value.sub_color),
    light_color: pickValue(value, "lightColor", "light_color", value.light_color),
  };
}

/**
 * 处理颜色
 */
export function hexToRgba(hex, alpha) {
  if (!hex) return "";
  let sColor = hex.toLowerCase();
  const reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
  if (sColor && reg.test(sColor)) {
    if (sColor.length === 4) {
      let sColorNew = "#";
      for (let i = 1; i < 4; i += 1) {
        sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1));
      }
      sColor = sColorNew;
    }
    //处理六位的颜色值
    let sColorChange = [];
    for (let i = 1; i < 7; i += 2) {
      sColorChange.push(parseInt("0x" + sColor.slice(i, i + 2)));
    }
    return "rgba(" + sColorChange.join(",") + "," + alpha + ")";
  }
  return sColor;
}

/**
 * 设置主题颜色
 * @param {Object} data 主题数据
 */
export function setThemeColor(data) {
  let selectedTheme;
  data = normalizeThemeColor(data);
  // 处理自定义主题色数据
  if (data.theme_color) {
    let themeColor = data.theme_color;
    let gradientColor = data.gradient_color || themeColor;
    let subColor = data.sub_color || themeColor;
    let lightColor = data.light_color || hexToRgba(themeColor, 0.1);
    selectedTheme = `
      --view-theme: ${hexToRgba(themeColor, 1)};
      --view-theme-16: ${themeColor};
      --view-priceColor: ${themeColor};
      --view-minorColor: ${subColor};
      --view-minorColorT: ${lightColor};
      --view-bntColor: ${subColor};
      --view-op-ten: ${hexToRgba(themeColor, 0.1)};
      --view-main-start: ${gradientColor};
      --view-main-over: ${themeColor};
      --view-op-point-four: ${hexToRgba(themeColor, 0.04)};
      --view-op-point-eight: ${hexToRgba(themeColor, 0.8)};
      --view-linear: linear-gradient(180deg, ${hexToRgba(
        themeColor,
        0.2,
      )} 0%, rgba(255,255,255,0) 100%);
      --view-gradient: ${gradientColor};
    `;
    uni.setStorageSync("viewColor", selectedTheme);
    uni.$emit("ok", selectedTheme);
  }
}

/**
 * 获取并应用主题
 * @param {Number|String} themeId 主题ID
 */
export function applyTheme(themeId) {
  let data = {};
  if (themeId) data.theme_id = themeId;
  return getThemeInfo("theme", data).then((res) => {
    uni.setStorageSync("is_diy", 1);
    uni.$emit("is_diy", 1);
    const themeData = normalizeThemeColor(res.data);
    setThemeColor(themeData);
    return themeData;
  });
}
