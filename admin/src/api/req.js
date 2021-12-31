import axios from 'axios'

const service = axios.create({
  timeout: 40000,
})
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response
    if (res.status !== 200  && res.status !== 401) {
      Message({
        message: res.data.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject()
    }else {
      return res.data
    }
  },
  error => {
  }
)
export default service
