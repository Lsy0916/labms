/**
 * 解析用户信息字符串的工具函数
 * @param {string} userInfoStr - 包含用户信息的字符串
 * @returns {Object|null} 解析后的用户对象，如果解析失败则返回null
 */
export function parseUserInfo(userInfoStr) {
  try {
    // 移除可能存在的引号转义
    const cleanedStr = userInfoStr.replace(/\\"/g, '"');
    
    // 使用正则表达式提取各个字段的值
    const userIdMatch = cleanedStr.match(/"userId":"([^"]*)"/);
    const nameMatch = cleanedStr.match(/"name":"([^"]*)"/);
    const avatarUrlMatch = cleanedStr.match(/"avatarUrl":"([^"]*)"/);
    const roleIdMatch = cleanedStr.match(/"roleId":"([^"]*)"/);
    
    // 构建用户对象
    const userInfo = {
      userId: userIdMatch ? userIdMatch[1] : null,
      name: nameMatch ? nameMatch[1] : null,
      avatarUrl: avatarUrlMatch ? avatarUrlMatch[1] : null,
      roleId: roleIdMatch ? roleIdMatch[1] : null
    };
    
    return userInfo;
  } catch (error) {
    console.error('解析用户信息时出错:', error);
    return null;
  }
}

/**
 * 将用户信息字符串转换为标准JSON格式
 * @param {string} userInfoStr - 包含用户信息的字符串
 * @returns {Object|null} 解析后的用户对象，如果解析失败则返回null
 */
export function parseUserInfoJson(userInfoStr) {
  try {
    // 尝试直接解析为JSON
    return JSON.parse(userInfoStr);
  } catch (error) {
    try {
      // 如果直接解析失败，则尝试清理后再解析
      // 移除首尾的引号（如果存在）
      let cleanStr = userInfoStr.trim();
      if (cleanStr.startsWith('"') && cleanStr.endsWith('"')) {
        cleanStr = cleanStr.slice(1, -1);
      }
      
      // 替换转义字符
      cleanStr = cleanStr.replace(/\\"/g, '"')
                        .replace(/\\n/g, '')
                        .replace(/\\r/g, '');
      
      return JSON.parse(cleanStr);
    } catch (innerError) {
      console.error('无法解析用户信息字符串:', userInfoStr, innerError);
      return null;
    }
  }
}

export default {
  parseUserInfo,
  parseUserInfoJson
};