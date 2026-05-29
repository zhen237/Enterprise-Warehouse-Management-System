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