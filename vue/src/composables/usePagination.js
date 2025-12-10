// 分页组合式函数
import { ref, reactive } from 'vue'

export function usePagination(fetchFunction) {
  // 分页状态
  const pagination = reactive({
    pageNo: 1,
    pageSize: 10,
    total: 0,
    loading: false
  })

  // 分页数据
  const pageData = ref([])

  // 获取分页数据
  const fetchData = async (params = {}) => {
    try {
      pagination.loading = true
      
      const queryParams = {
        pageNo: pagination.pageNo,
        pageSize: pagination.pageSize,
        ...params
      }
      
      const response = await fetchFunction(queryParams)
      
      if (response.success) {
        pageData.value = response.data.list || response.data
        pagination.total = response.data.total || 0
      }
      
      return response
    } catch (error) {
      console.error('获取分页数据失败:', error)
      return {
        success: false,
        message: '获取数据失败'
      }
    } finally {
      pagination.loading = false
    }
  }

  // 更改每页大小
  const handleSizeChange = (newSize) => {
    pagination.pageSize = newSize
    pagination.pageNo = 1
    fetchData()
  }

  // 更改页码
  const handleCurrentChange = (newPage) => {
    pagination.pageNo = newPage
    fetchData()
  }

  // 重置分页
  const resetPagination = () => {
    pagination.pageNo = 1
    pagination.total = 0
  }

  return {
    pagination,
    pageData,
    fetchData,
    handleSizeChange,
    handleCurrentChange,
    resetPagination
  }
}