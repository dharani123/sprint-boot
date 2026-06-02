export default function OrderModal({ order, onClose }) {
  return (
    <div className="modal-overlay open">
      <div className="modal">
        <div className="modal-check">✅</div>
        <div className="modal-title">Order Placed!</div>
        <div className="modal-order-id">Order ID: {order.orderId}</div>
        <div className="modal-eta">⚡ Arriving in <strong>10 minutes</strong> — stay near the door!</div>
        <div className="modal-items">
          {order.items.map(i => (
            <div key={i.productId} className="modal-item">
              <span className="modal-item-emoji">{i.emoji}</span>
              <span className="modal-item-name">{i.name} × {i.quantity}</span>
              <span className="modal-item-price">₹{i.itemTotal.toFixed(0)}</span>
            </div>
          ))}
        </div>
        <div className="modal-bill">
          <div className="modal-bill-row"><span>Subtotal</span><span>₹{order.subtotal.toFixed(2)}</span></div>
          <div className="modal-bill-row">
            <span>Delivery fee</span>
            <span>{order.deliveryFee === 0 ? <span className="free">FREE</span> : `₹${order.deliveryFee}`}</span>
          </div>
          <div className="modal-bill-row"><span>Platform fee</span><span>₹{order.platformFee}</span></div>
          <div className="modal-bill-row total"><span>Total Paid</span><span>₹{order.total.toFixed(2)}</span></div>
        </div>
        <button className="modal-continue" onClick={onClose}>Continue Shopping</button>
      </div>
    </div>
  )
}
