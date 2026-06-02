import { useState, useEffect, useCallback } from 'react'
import Navbar         from './Navbar'
import CategoryPills  from './CategoryPills'
import ProductCard    from './ProductCard'
import CartDrawer     from './CartDrawer'
import OrderModal     from './OrderModal'
import './store.css'

const CATEGORIES = ['All', 'Dairy & Eggs', 'Fruits & Veggies', 'Bakery', 'Beverages', 'Snacks', 'Personal Care']

export default function Store() {
  const [products,       setProducts]       = useState([])
  const [cart,           setCart]           = useState({})   // { id: quantity }
  const [activeCategory, setActiveCategory] = useState('All')
  const [cartOpen,       setCartOpen]       = useState(false)
  const [orderResult,    setOrderResult]    = useState(null)
  const [loading,        setLoading]        = useState(true)

  useEffect(() => {
    fetch('/api/products')
      .then(r => r.json())
      .then(data => { setProducts(data); setLoading(false) })
      .catch(() => setLoading(false))
  }, [])

  const filtered   = activeCategory === 'All' ? products : products.filter(p => p.category === activeCategory)
  const cartTotal  = Object.values(cart).reduce((s, v) => s + v, 0)

  const addToCart = useCallback((id) => {
    setCart(prev => ({ ...prev, [id]: 1 }))
  }, [])

  const changeQty = useCallback((id, delta) => {
    setCart(prev => {
      const next = { ...prev, [id]: (prev[id] || 0) + delta }
      if (next[id] <= 0) delete next[id]
      return { ...next }
    })
  }, [])

  async function placeOrder() {
    const items = Object.entries(cart).map(([id, qty]) => ({
      productId: parseInt(id), quantity: qty,
    }))
    const res   = await fetch('/api/cart/checkout', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(items),
    })
    const order = await res.json()
    setOrderResult(order)
    setCartOpen(false)
    setCart({})
  }

  return (
    <div className="store-root">
      <Navbar cartTotal={cartTotal} onCartOpen={() => setCartOpen(true)} />

      <div className="banner">
        🎉 Free delivery on orders above <span>₹199</span> &nbsp;|&nbsp; ⚡ Delivered in <span>10 minutes</span>
      </div>

      <CategoryPills categories={CATEGORIES} active={activeCategory} onChange={setActiveCategory} />

      <main className="main">
        {loading ? (
          <div className="loading">⚙️ Loading products…</div>
        ) : (
          <>
            <div className="section-title">
              {activeCategory === 'All' ? 'All Products' : activeCategory}
              <span className="count-badge">({filtered.length})</span>
            </div>
            <div className="products-grid">
              {filtered.map(p => (
                <ProductCard
                  key={p.id}
                  product={p}
                  qty={cart[p.id] || 0}
                  onAdd={() => addToCart(p.id)}
                  onChangeQty={delta => changeQty(p.id, delta)}
                />
              ))}
            </div>
          </>
        )}
      </main>

      <CartDrawer
        open={cartOpen}
        products={products}
        cart={cart}
        onClose={() => setCartOpen(false)}
        onChangeQty={changeQty}
        onPlaceOrder={placeOrder}
      />

      {orderResult && (
        <OrderModal order={orderResult} onClose={() => setOrderResult(null)} />
      )}
    </div>
  )
}
