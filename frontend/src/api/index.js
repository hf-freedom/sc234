import axios from 'axios'

const API_BASE_URL = 'http://localhost:8003'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

export const getProducts = () => api.get('/api/products')
export const getOrders = () => api.get('/api/orders')
export const getStatistics = () => api.get('/api/orders/statistics')
export const createOrder = (data) => api.post('/api/orders', data)
export const freezeDeposit = (id) => api.post(`/api/orders/${id}/freeze-deposit`)
export const confirmRental = (id) => api.post(`/api/orders/${id}/confirm-rental`)
export const returnProduct = (data) => api.post('/api/orders/return', data)
export const earlyReturn = (id) => api.post(`/api/orders/${id}/early-return`)

export default api
