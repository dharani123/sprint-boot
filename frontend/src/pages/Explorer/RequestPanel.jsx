import { useState } from 'react'
import { ENDPOINTS } from './endpoints'

export default function RequestPanel({ epKey, url, setUrl, method, setMethod, params, setParams, headers, setHeaders, body, setBody, onSend, loading }) {
  const [activeTab, setActiveTab] = useState('params')
  const ep = ENDPOINTS[epKey]

  function updateRow(setter, index, field, value) {
    setter(prev => prev.map((r, i) => i === index ? { ...r, [field]: value } : r))
  }
  function deleteRow(setter, index) {
    setter(prev => prev.filter((_, i) => i !== index))
  }
  function addRow(setter) {
    setter(prev => [...prev, { key: '', value: '', enabled: true }])
  }

  function KvTable({ rows, setter }) {
    return (
      <>
        <div className="kv-table">
          <table>
            <thead><tr><th></th><th>Key</th><th>Value</th><th></th></tr></thead>
            <tbody>
              {rows.map((row, i) => (
                <tr key={i}>
                  <td><input type="checkbox" checked={row.enabled} onChange={e => updateRow(setter, i, 'enabled', e.target.checked)} /></td>
                  <td><input className="kv-input" type="text" value={row.key}   onChange={e => updateRow(setter, i, 'key',   e.target.value)} placeholder="key" /></td>
                  <td><input className="kv-input" type="text" value={row.value} onChange={e => updateRow(setter, i, 'value', e.target.value)} placeholder="value" /></td>
                  <td><button className="del-btn" onClick={() => deleteRow(setter, i)}>✕</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <button className="add-row-btn" onClick={() => addRow(setter)}>+ Add Row</button>
      </>
    )
  }

  return (
    <div className="panel">
      <div className="panel-header">
        <div className="dot" style={{ background: '#6366f1' }} /> Request
      </div>
      <div className="tabs">
        {['params', 'headers', 'body', 'concept'].map(tab => (
          <div key={tab} className={`tab${activeTab === tab ? ' active' : ''}`} onClick={() => setActiveTab(tab)}>
            {tab === 'concept' ? '📖 Concept' : tab.charAt(0).toUpperCase() + tab.slice(1)}
          </div>
        ))}
      </div>

      <div className={`tab-content${activeTab === 'params' ? ' active' : ''}`}>
        <KvTable rows={params} setter={setParams} />
      </div>

      <div className={`tab-content${activeTab === 'headers' ? ' active' : ''}`}>
        <KvTable rows={headers} setter={setHeaders} />
      </div>

      <div className={`tab-content${activeTab === 'body' ? ' active' : ''}`}>
        <div className="body-area">
          <div className="body-hint">JSON body (for POST / PUT)</div>
          <textarea className="body-textarea" value={body} onChange={e => setBody(e.target.value)} />
        </div>
      </div>

      <div className={`tab-content${activeTab === 'concept' ? ' active' : ''}`}>
        {ep ? (
          <div className="concept-box" dangerouslySetInnerHTML={{ __html: ep.concept }} />
        ) : (
          <div className="concept-box" style={{ color: '#4b5280' }}>Select an endpoint to see its concept explanation.</div>
        )}
      </div>
    </div>
  )
}
