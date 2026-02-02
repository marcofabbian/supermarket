import React, { useState, useMemo, useEffect, useRef } from 'react'
import ProductTable from './components/ProductTable'

const initialProducts = [
  { id: 1, image: 'https://picsum.photos/seed/milk/64', name: 'Milk 1L', supermarket: 'SuperMart', brand: 'DairyCo', price: 1.99 },
  { id: 2, image: 'https://picsum.photos/seed/bread/64', name: 'Bread', supermarket: 'BakeryShop', brand: 'BakeHouse', price: 2.49 },
  { id: 3, image: 'https://picsum.photos/seed/apples/64', name: 'Apples 1kg', supermarket: 'FreshFarm', brand: 'Orchard', price: 3.79 }
]

export default function App() {
  const [query, setQuery] = useState('')
  const [products, setProducts] = useState(initialProducts)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const abortRef = useRef(null)

  // Fetch products from the backend. The backend currently ignores `q` but we'll send it
  // so the API can later implement server-side search. Calls are debounced.
  useEffect(() => {
    const q = query.trim()
    // debounce
    const id = setTimeout(() => {
      // cancel previous
      if (abortRef.current) {
        try { abortRef.current.abort() } catch (e) {}
      }
      const controller = new AbortController()
      abortRef.current = controller

      setLoading(true)
      setError(null)

      const url = '/api/products' + (q ? `?q=${encodeURIComponent(q)}` : '')

      fetch(url, { signal: controller.signal })
        .then(res => {
          if (!res.ok) throw new Error(`HTTP ${res.status}`)
          return res.json()
        })
        .then(data => {
          if (Array.isArray(data)) setProducts(data)
          else setProducts(initialProducts)
        })
        .catch(err => {
          if (err.name === 'AbortError') return
          console.error('Failed to fetch products', err)
          setError('Failed to fetch products; showing local sample data')
          setProducts(initialProducts)
        })
        .finally(() => {
          setLoading(false)
        })
    }, 300)

    return () => clearTimeout(id)
  }, [query])

  const filtered = useMemo(() => {
    // We assume the server returns filtered results when it supports `q`.
    // As a fallback (or in case of proxying), keep a client-side filter as well.
    const q = query.trim().toLowerCase()
    if (!q) return products
    return products.filter(p =>
      (p.name + ' ' + p.supermarket + ' ' + p.brand).toLowerCase().includes(q)
    )
  }, [products, query])

  return (
    <div className="container py-4">
      <h1 className="mb-4">Supermarket - Products</h1>

      <div className="mb-3">
        <input
          type="text"
          className="form-control"
          placeholder="Search products, supermarket or brand"
          value={query}
          onChange={e => setQuery(e.target.value)}
        />
      </div>

      {loading && <div className="mb-3">Loading products...</div>}
      {error && <div className="alert alert-warning">{error}</div>}

      <ProductTable products={filtered} />
    </div>
  )
}
