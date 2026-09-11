export function HandlePrice(num, type) {
  const value = num === undefined || num === null ? '' : String(num);
  const parts = value.split('.');
  if (type) {
    return parts.length > 1 && parts[1] ? `.${parts[1]}` : '';
  }
  return parts[0];
}
