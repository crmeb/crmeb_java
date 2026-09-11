// 服务式 API（ElMessage/ElMessageBox/ElNotification/ElLoading）挂载到 body，
// 不经过模板组件扫描，ElementPlusResolver 的按需样式注入无法覆盖。
// 这里显式引入其 CSS，确保按需 CSS 改造后样式正常。
import 'element-plus/es/components/message/style/css';
import 'element-plus/es/components/message-box/style/css';
import 'element-plus/es/components/notification/style/css';
import 'element-plus/es/components/loading/style/css';

import { ElMessage as ElementMessage } from 'element-plus';

const MESSAGE_DEDUPE_INTERVAL = 500;
const PERMISSION_MESSAGE_DEDUPE_INTERVAL = 2000;
const PERMISSION_DENIED_PATTERN = /(暂无权限|没有权限|无访问权限|权限不足|没有相关权限)/;
const recentMessages = new Map();

function getMessageMetadata(options, fallbackType = 'info') {
  const normalizedOptions = typeof options === 'string' ? { message: options } : options;
  if (!normalizedOptions || typeof normalizedOptions.message !== 'string') {
    return { key: '', dedupeInterval: MESSAGE_DEDUPE_INTERVAL };
  }

  const type = normalizedOptions.type || fallbackType;
  const isPermissionDenied = type === 'error' && PERMISSION_DENIED_PATTERN.test(normalizedOptions.message);
  return {
    key: `${type}:${isPermissionDenied ? 'permission-denied' : normalizedOptions.message}`,
    dedupeInterval: isPermissionDenied ? PERMISSION_MESSAGE_DEDUPE_INTERVAL : MESSAGE_DEDUPE_INTERVAL,
  };
}

function showMessage(options, context, fallbackType) {
  const { key, dedupeInterval } = getMessageMetadata(options, fallbackType);
  const now = Date.now();
  const recentMessage = key && recentMessages.get(key);

  if (recentMessage && now - recentMessage.createdAt < dedupeInterval) {
    return recentMessage.handler;
  }

  const handler = fallbackType
    ? ElementMessage[fallbackType](options, context)
    : ElementMessage(options, context);
  if (key) {
    const messageRecord = { createdAt: now, handler };
    recentMessages.set(key, messageRecord);
    setTimeout(() => {
      if (recentMessages.get(key) === messageRecord) recentMessages.delete(key);
    }, dedupeInterval);
  }
  return handler;
}

export const ElMessage = Object.assign((options, context) => showMessage(options, context), {
  success: (options, context) => showMessage(options, context, 'success'),
  warning: (options, context) => showMessage(options, context, 'warning'),
  info: (options, context) => showMessage(options, context, 'info'),
  error: (options, context) => showMessage(options, context, 'error'),
  closeAll: (...args) => {
    recentMessages.clear();
    return ElementMessage.closeAll(...args);
  },
});

export { ElLoading } from 'element-plus';
export { ElMessageBox } from 'element-plus';
export { ElNotification } from 'element-plus';
