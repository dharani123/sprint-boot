export default function CartDrawer({ open, products, cart, onClose, onChangeQty, onPlaceOrder }) {
  const cartItems  = products.filter(p => cart[p.id] > 0)
  const subtotal   = cartItems.reduce((s, p) => s + p.price * cart[p.id], 0)
  const deliveryFee = subtotal >= 199 ? 0 : 25
  const platformFee = 3
  const total      = subtotal + deliveryFee + platformFee

  return (
    <>
      <div className={`cart-overlay${open ? ' open' : ''}`} onClick={onClose} />
      <div className={`cart-drawer${open ? ' open' : ''}`}>
        <div className="cart-header">
          <h2>🛒 My Cart</h2>
          <button className="cart-close" onClick={onClose}>✕</button>
        </div>

        <div className="cart-body">
          {cartItems.length === 0 ? (
            <div className="empty-cart">
              <div className="empty-icon">🛒</div>
              <p>Your cart is empty</p>
              <small>Add items from the store</small>
            </div>
          ) : cartItems.map(p => (
            <div key={p.id} className="cart-item">
              <span className="cart-item-emoji">{p.emoji}</span>
              <div className="cart-item-info">
                <div className="cart-item-name">{p.name}</div>
                <div className="cart-item-unit">{p.unitLabel}</div>
              </div>
              <div className="cart-qty">
                <button className="qty-btn" onClick={() => onChangeQty(p.id, -1)}>−</button>
                <span className="qty-num">{cart[p.id]}</span>
                <button className="qty-btn" onClick={() => onChangeQty(p.id, 1)}>+</button>
              </div>
              <div className="cart-item-price">₹{(p.price * cart[p.id]).toFixed(0)}</div>
            </div>
          ))}
        </div>

        {cartItems.length > 0 && (
          <div className="bill-section">
            <div className="bill-header">Bill Summary</div>
            <div className="bill-rows">
              <div className="bill-row"><span>Subtotal</span><span>₹{subtotal.toFixed(2)}</span></div>
              <div className="bill-row">
                <span>Delivery fee</span>
                <span>{deliveryFee === 0 ? <span className="free">FREE</span> : `₹${deliveryFee}`}</span>
              </div>
              <div className="bill-row"><span>Platform fee</span><span>₹{platformFee}</span></div>
              <div className="bill-row total"><span>To Pay</span><span>₹{total.toFixed(2)}</span></div>
            </div>
            {subtotal < 199
              ? <div className="free-delivery-hint">🏃 Add ₹{(199 - subtotal).toFixed(0)} more for FREE delivery!</div>
              : <div className="savings-hint">🎉 You're getting free delivery!</div>
            }
            <button className="checkout-btn" onClick={onPlaceOrder}>Place Order ›</button>
          </div>
        )}
      </div>
    </>
  )
}
