/**
 * 防抖函数
 * @param {Function} func - 需要防抖的函数
 * @param {number} wait - 延迟执行毫秒数
 * @param {boolean} immediate - 是否立即执行
 * @returns {Function} - 防抖后的函数
 */
export function debounce(func, wait, immediate = false) {
  let timeout, result

  const debounced = function (...args) {
    const context = this

    if (timeout) clearTimeout(timeout)
    
    if (immediate) {
      // 如果已经执行过，不再执行
      const callNow = !timeout
      timeout = setTimeout(() => {
        timeout = null
      }, wait)
      if (callNow) result = func.apply(context, args)
    } else {
      timeout = setTimeout(() => {
        func.apply(context, args)
      }, wait)
    }
    
    return result
  }

  debounced.cancel = function () {
    clearTimeout(timeout)
    timeout = null
  }

  return debounced
}