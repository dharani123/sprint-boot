export default function ProductCard({ product, qty, onAdd, onChangeQty }) {
  return (
    <div className="product-card">
      <div className="product-img">{product.emoji}</div>
      <div className="product-body">
        <div className="product-name">{product.name}</div>
        <div className="product-unit">{product.unitLabel}</div>
        <div className="product-footer">
          <span className="product-price">₹{product.price}</span>
          {qty === 0 ? (
            <button className="add-btn" onClick={onAdd}>+</button>
          ) : (
            <div className="qty-ctrl">
              <button className="qty-btn" onClick={() => onChangeQty(-1)}>−</button>
              <span className="qty-num">{qty}</span>
              <button className="qty-btn" onClick={() => onChangeQty(1)}>+</button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
