function statusClass(s) {
  if (!s) return 's0'
  if (s >= 200 && s < 300) return 's200'
  if (s >= 400 && s < 500) return 's4xx'
  return 's5xx'
}

function prettyJson(text) {
  try { return JSON.stringify(JSON.parse(text), null, 2) }
  catch { return text }
}

export default function ResponsePanel({ response, loading }) {
  return (
    <div className="panel">
      <div className="panel-header">
        <div className="dot" style={{ background: '#68d391' }} /> Response
        {loading && <span className="spinner" />}
      </div>

      {response ? (
        <>
          <div className="res-meta">
            <span className="meta-label">Status</span>
            <span className={`meta-val ${statusClass(response.status)}`}>{response.status}</span>
            <span className="meta-label">Time</span>
            <span className="meta-val" style={{ color: '#63b3ed' }}>{response.time} ms</span>
          </div>
          <div className="res-body">
            <pre className={`res-pre${response.isError ? ' err' : ''}`}>
              {prettyJson(response.body)}
            </pre>
          </div>
        </>
      ) : (
        <div className="res-body">
          <pre className="res-pre empty">
            {loading ? 'Sending request…' : 'Hit Send to see the response here.'}
          </pre>
        </div>
      )}
    </div>
  )
}
