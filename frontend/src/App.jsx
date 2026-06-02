import { Routes, Route, Navigate } from 'react-router-dom'
import Store    from './pages/Store'
import Explorer from './pages/Explorer'

export default function App() {
  return (
    <Routes>
      <Route path="/"         element={<Store />} />
      <Route path="/explorer" element={<Explorer />} />
      <Route path="*"         element={<Navigate to="/" />} />
    </Routes>
  )
}
