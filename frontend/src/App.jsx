import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Store        from './pages/Store'
import RegisterPage from './pages/Auth/RegisterPage'
import LoginPage    from './pages/Auth/LoginPage'

// Wraps any route that requires login.
// If the user is not authenticated, redirect them to /login instead of showing the page.
function ProtectedRoute({ children }) {
  const { user } = useAuth()
  return user ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/"         element={<ProtectedRoute><Store /></ProtectedRoute>} />
<Route path="/register" element={<RegisterPage />} />
      <Route path="/login"    element={<LoginPage />} />
      <Route path="*"         element={<Navigate to="/" />} />
    </Routes>
  )
}
