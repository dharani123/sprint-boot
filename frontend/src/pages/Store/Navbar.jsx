import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function Navbar({ cartTotal, onCartOpen }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="nav">
      <div className="nav-logo"><span>⚡</span> QuickMart</div>
      <div className="nav-delivery">
        <span>📍</span>
        <div className="nav-delivery-text">
          <div className="label">DELIVER TO</div>
          <div className="address">Bangalore, 560001 ▾</div>
        </div>
      </div>
      <div className="spacer" />

      <div className="nav-user">
        <span className="nav-user-name">👤 {user?.name?.split(' ')[0]}</span>
        <button className="logout-btn" onClick={handleLogout}>Logout</button>
      </div>

      <button className="cart-btn" onClick={onCartOpen}>
        🛒 My Cart
        {cartTotal > 0 && <span className="cart-badge">{cartTotal}</span>}
      </button>
    </nav>
  )
}
