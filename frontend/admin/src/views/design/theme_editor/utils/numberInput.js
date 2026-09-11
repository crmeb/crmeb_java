const DEFAULT_MIN = 0
const DEFAULT_MAX = 100
const DEFAULT_STEP = 1

function toFiniteNumber(value, fallback) {
  const number = Number(value)
  return Number.isFinite(number) ? number : fallback
}

function getDecimalLength(value) {
  const [, decimal = ''] = String(value).split('.')
  return decimal.length
}

export function getNumberMin(config, fallback = DEFAULT_MIN) {
  return toFiniteNumber(config && config.min, fallback)
}

export function getNumberMax(config, fallback = DEFAULT_MAX) {
  return toFiniteNumber(config && config.max, fallback)
}

export function getNumberStep(config, fallback = DEFAULT_STEP) {
  const step = toFiniteNumber(config && config.step, fallback)
  return step > 0 ? step : fallback
}

export function normalizeNumberValue(value, config, options = {}) {
  const min = getNumberMin(config, options.minFallback)
  const max = getNumberMax(config, options.maxFallback)
  const step = getNumberStep(config, options.stepFallback)
  const number = Number(value)

  if (!Number.isFinite(number)) return min

  const precision = Math.max(getDecimalLength(step), getDecimalLength(min), getDecimalLength(max))
  let nextValue = Number((Math.round(number / step) * step).toFixed(precision))

  if (Number.isFinite(min) && nextValue < min) nextValue = min
  if (Number.isFinite(max) && nextValue > max) nextValue = max

  return nextValue
}

export function normalizeNumberField(target, key, config, options) {
  if (!target || !key) return
  target[key] = normalizeNumberValue(target[key], config || target, options)
}
