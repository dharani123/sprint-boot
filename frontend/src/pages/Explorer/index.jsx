import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import Sidebar       from './Sidebar'
import RequestPanel  from './RequestPanel'
import ResponsePanel from './ResponsePanel'
import { ENDPOINTS } from './endpoints'
import './explorer.css'

export default function Explorer() {
  const [epKey,    setEpKey]    = useState(null)
  const [url,      setUrl]      = useState('')
  const [method,   setMethod]   = useState('GET')
  const [params,   setParams]   = useState([])
  const [headers,  setHeaders]  = useState([])
  const [body,     setBody]     = useState('')
  const [response, setResponse] = useState(null)
  const [loading,  setLoading]  = useState(false)
  const [online,   setOnline]   = useState(false)

  // Server health check
  useEffect(() => {
    fetch('/actuator/health')
      .then(r => r.ok ? setOnline(true) : setOnline(false))
      .catch(() => setOnline(false))
  }, [])

  function loadEndpoint(key) {
    const ep = ENDPOINTS[key]
    if (!ep) return
    setEpKey(key)
    setUrl(ep.url)
    setMethod(ep.method)
    setParams(ep.params.length ? ep.params : [])
    setHeaders(ep.headers.length ? ep.headers : [])
    setBody(ep.body)
    setResponse(null)
  }

  function buildUrl(basePath, params) {
    const enabled = params.filter(p => p.enabled && p.key)
    if (!enabled.length) return basePath
    const qs = enabled.map(p => `${encodeURIComponent(p.key)}=${encodeURIComponent(p.value)}`).join('&')
    return `${basePath}?${qs}`
  }

  async function sendRequest() {
    const fullUrl = buildUrl(url, params)
    const opts = {
      method,
      headers: Object.fromEntries(
        headers.filter(h => h.enabled && h.key).map(h => [h.key, h.value])
      ),
    }
    if (['POST', 'PUT', 'PATCH'].includes(method) && body.trim()) {
      opts.body = body
    }
    setLoading(true)
    setResponse(null)
    const start = Date.now()
    try {
      const res  = await fetch(fullUrl, opts)
      const text = await res.text()
      setResponse({ status: res.status, time: Date.now() - start, body: text })
    } catch (err) {
      setResponse({ status: 0, time: Date.now() - start, body: String(err), isError: true })
    } finally {
      setLoading(false)
    }
  }

  const ep = epKey ? ENDPOINTS[epKey] : null

  return (
    <div className="explorer-root">
      {/* Top Bar */}
      <div className="topbar">
        <h1>⚡ Spring Boot Learner</h1>
        <span className="subtitle">Interactive API Explorer</span>
        <Link to="/" className="store-link">🛒 QuickMart Store</Link>
        <div className="server-status">
          <div className={`status-dot${online ? ' online' : ''}`} />
          <span>{online ? 'Server Online' : 'Server Offline'}</span>
        </div>
      </div>

      <div className="layout">
        <Sidebar activeKey={epKey} onSelect={loadEndpoint} />

        <div className="ep-main">
          {ep ? (
            <>
              {/* Endpoint Header */}
              <div className="endpoint-header">
                <div className="endpoint-title">
                  <span style={{ color: { GET:'#68d391', POST:'#f6ad55', PUT:'#63b3ed', DELETE:'#f56565' }[ep.method], fontFamily:'monospace', fontWeight:800, marginRight:10 }}>
                    {ep.method}
                  </span>
                  {ep.title.replace(/^[A-Z]+ /, '')}
                </div>
                <div className="endpoint-desc">{ep.desc}</div>
                <div className="endpoint-analogy">💡 {ep.analogy}</div>
              </div>

              {/* URL Bar */}
              <div className="urlbar">
                <select className="method-select" value={method} onChange={e => setMethod(e.target.value)}>
                  {['GET','POST','PUT','DELETE','PATCH'].map(m => <option key={m}>{m}</option>)}
                </select>
                <input className="url-input" type="text" value={url} onChange={e => setUrl(e.target.value)} />
                <button className="send-btn" onClick={sendRequest} disabled={loading}>
                  {loading ? 'Sending…' : 'Send ↵'}
                </button>
              </div>

              {/* Request + Response */}
              <div className="panels">
                <RequestPanel
                  epKey={epKey}
                  url={url} setUrl={setUrl}
                  method={method} setMethod={setMethod}
                  params={params} setParams={setParams}
                  headers={headers} setHeaders={setHeaders}
                  body={body} setBody={setBody}
                  onSend={sendRequest} loading={loading}
                />
                <ResponsePanel response={response} loading={loading} />
              </div>
            </>
          ) : (
            <div className="placeholder">
              <div className="big">🚀</div>
              <p>Select an endpoint from the sidebar to start exploring your Spring Boot APIs</p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
