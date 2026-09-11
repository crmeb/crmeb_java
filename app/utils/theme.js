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
  if (value.value !== undefined) {
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

function buildLegacyThemeData(data = {}) {
  const themeColor = data.theme_color || "#e93323";
  const gradientColor = data.gradient_color || themeColor;
  const subColor = data.sub_color || themeColor;
  const lightColor = data.light_color || hexToRgba(themeColor, 0.1);
  return {
    theme: "theme1",
    theme_color: themeColor,
    gradient_color: gradientColor,
    sub_color: subColor,
    light_color: lightColor,
    main_color: themeColor,
    price_color: themeColor,
    main_gradient: `linear-gradient(90deg, ${gradientColor} 0%, ${themeColor} 100%)`,
    second_gradient: `linear-gradient(90deg, ${gradientColor} 0%, ${subColor} 100%)`,
    coupons_border: `1px solid ${themeColor}`,
    coupons_light_color: lightColor,
  };
}

function setLegacyThemeData(themeData) {
  const app = typeof getApp === "function" ? getApp() : null;
  uni.setStorageSync("theme", themeData.theme);
  uni.setStorageSync("themeData", themeData);
  uni.setStorageSync("themeColor", themeData.theme_color);
  if (app && app.globalData) {
    app.globalData.theme = themeData.theme;
    app.globalData.themeData = themeData;
    app.globalData.themeColor = themeData.theme_color;
  }
  uni.$emit("themeData", themeData);
}

function buildThemeStyle(data = {}) {
  const themeColor = data.theme_color;
  const gradientColor = data.gradient_color || themeColor;
  const subColor = data.sub_color || themeColor;
  const lightColor = data.light_color || hexToRgba(themeColor, 0.1);
  const themeColorRgba = hexToRgba(themeColor, 1);

  return `
      --view-theme: ${themeColorRgba};
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
      --view-main-gradient: linear-gradient(90deg, ${gradientColor} 0%, ${themeColorRgba} 100%);
      --view-second-gradient: linear-gradient(90deg, ${gradientColor} 0%, ${subColor} 100%);
      --view-index-gradient: linear-gradient(270deg, ${themeColorRgba} 0%, #F5F5F5 100%);
      --view-seckill-gradient: linear-gradient(270deg, ${themeColorRgba} 0%, rgba(255,255,255,0.2) 72%, rgba(255,255,255,1) 100%);
      --view-coupons-gradient: linear-gradient(0deg, ${gradientColor} 0%, ${themeColorRgba} 100%);
      --view-bargain-gradient: linear-gradient(270deg, ${gradientColor} 0%, ${themeColorRgba} 100%);
      --view-login-gradient: linear-gradient(180deg, ${hexToRgba(themeColor, 0.2)} 0%, rgba(255,255,255,0) 100%);
      --view-bragin-border: ${themeColorRgba} transparent transparent;
    `;
}

export function applyH5ThemeStyle(styleText = "") {
  // #ifdef H5
  if (typeof document === "undefined" || !styleText) return;
  styleText
    .split(";")
    .map((item) => item.trim())
    .filter(Boolean)
    .forEach((item) => {
      const index = item.indexOf(":");
      if (index === -1) return;
      document.documentElement.style.setProperty(
        item.slice(0, index).trim(),
        item.slice(index + 1).trim(),
      );
    });
  // #endif
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
  data = normalizeThemeColor(data);
  // 处理自定义主题色数据
  if (data.theme_color) {
    const legacyThemeData = buildLegacyThemeData(data);
    const selectedTheme = buildThemeStyle(data);
    setLegacyThemeData(legacyThemeData);
    uni.setStorageSync("viewColor", selectedTheme);
    applyH5ThemeStyle(selectedTheme);
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
