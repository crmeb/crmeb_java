import { getToken } from '@/utils/auth';

export function getCookies() {
  return getToken();
}
