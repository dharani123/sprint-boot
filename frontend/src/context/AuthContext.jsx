import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  // On first load, try to restore the user from localStorage
  // This keeps the user logged in after a page refresh
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  const login = (userData, token) => {
    // Store the JWT token — sent with every future API request
    localStorage.setItem('token', token)
    // Store user info so we can show the user's name in the UI
    localStorage.setItem('user', JSON.stringify(userData))
    setUser(userData)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// Custom hook — instead of writing useContext(AuthContext) everywhere,
// any component just calls useAuth() to get { user, login, logout }
export function useAuth() {
  return useContext(AuthContext)
}
