/**
 * 用户状态管理模块
 * 使用 localStorage 持久化存储登录用户信息
 *
 * 存储键名：warehouse_user
 * 存储内容：{ id, username, name, role }
 *
 * 提供方法：
 *   - setCurrentUser(user)       保存当前登录用户
 *   - getCurrentUser()           获取当前登录用户（JSON 解析后返回）
 *   - removeCurrentUser()        清除登录用户（退出登录时调用）
 *   - isAdmin()                  判断当前用户是否为管理员
 *   - isOperator()               判断当前用户是否为操作员
 *   - isEmployee()               判断当前用户是否为普通员工
 */

const USER_KEY = 'warehouse_user'

export function setCurrentUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function getCurrentUser() {
  const user = localStorage.getItem(USER_KEY)
  return user ? JSON.parse(user) : null
}

export function removeCurrentUser() {
  localStorage.removeItem(USER_KEY)
}

export function isAdmin() {
  const user = getCurrentUser()
  return user && user.role === 'ADMIN'
}

export function isOperator() {
  const user = getCurrentUser()
  return user && user.role === 'OPERATOR'
}

export function isEmployee() {
  const user = getCurrentUser()
  return user && user.role === 'EMPLOYEE'
}