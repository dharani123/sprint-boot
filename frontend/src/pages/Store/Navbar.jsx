import { Link } from 'react-router-dom'

export default function Navbar({ cartTotal, onCartOpen }) {
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
      <Link to="/explorer" className="explorer-link">🔬 API Explorer</Link>
      <button className="cart-btn" onClick={onCartOpen}>
        🛒 My Cart
        {cartTotal > 0 && <span className="cart-badge">{cartTotal}</span>}
      </button>
    </nav>
  )
}
