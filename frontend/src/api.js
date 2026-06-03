// Wrapper around fetch that automatically attaches the JWT token.
// Use this instead of plain fetch() for any endpoint that requires login.
export function authFetch(url, options = {}) {
  const token = localStorage.getItem('token')
  return fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })
}
