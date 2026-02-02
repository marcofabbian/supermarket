import React from 'react'

export default function ProductTable({ products }) {
  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        <thead>
          <tr>
            <th></th>
            <th>Name</th>
            <th>Supermarket</th>
            <th>Brand</th>
            <th className="text-end">Price</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr>
              <td colSpan="5" className="text-center py-4">No products found</td>
            </tr>
          ) : (
            products.map(p => (
              <tr key={p.id}>
                <td style={{ width: 72 }}>
                  <img src={p.image} alt={p.name} className="img-fluid" style={{ maxHeight: 64 }} />
                </td>
                <td>{p.name}</td>
                <td>{p.supermarket}</td>
                <td>{p.brand}</td>
                <td className="text-end">${p.price.toFixed(2)}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}
