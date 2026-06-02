import { useState } from 'react'
import { GROUPS } from './endpoints'

export default function Sidebar({ activeKey, onSelect }) {
  const [open, setOpen] = useState(new Set(GROUPS.map(g => g.id)))

  function toggle(id) {
    setOpen(prev => {
      const next = new Set(prev)
      next.has(id) ? next.delete(id) : next.add(id)
      return next
    })
  }

  return (
    <div className="sidebar">
      <div className="sidebar-title">API Endpoints</div>
      {GROUPS.map(group => (
        <div key={group.id} className={`group${open.has(group.id) ? ' open' : ''}`}>
          <div className="group-label" onClick={() => toggle(group.id)}>
            <span className="arrow">▶</span> {group.label}
          </div>
          <div className="group-items">
            {group.items.map(item => (
              <div
                key={item.key}
                className={`api-item${activeKey === item.key ? ' active' : ''}`}
                onClick={() => onSelect(item.key)}
              >
                <span className={`method-badge ${item.method}`}>{item.method}</span>
                {item.label}
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  )
}
