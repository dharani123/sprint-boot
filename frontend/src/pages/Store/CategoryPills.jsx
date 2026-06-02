const CAT_EMOJI = {
  'All': '🏪', 'Dairy & Eggs': '🥛', 'Fruits & Veggies': '🥦',
  'Bakery': '🍞', 'Beverages': '🥤', 'Snacks': '🍿', 'Personal Care': '🧴',
}

export default function CategoryPills({ categories, active, onChange }) {
  return (
    <div className="cats-wrap">
      <div className="cats">
        {categories.map(cat => (
          <button
            key={cat}
            className={`cat-pill${active === cat ? ' active' : ''}`}
            onClick={() => onChange(cat)}
          >
            {CAT_EMOJI[cat]} {cat}
          </button>
        ))}
      </div>
    </div>
  )
}
