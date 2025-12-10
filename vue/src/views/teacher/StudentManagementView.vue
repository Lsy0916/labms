                                                                                                                                                                   <template>
  <div class="student-management-container">
    <el-card class="student-management-card">
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showAddForm" :icon="Plus">新增学生</el-button>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索学生 (学号/姓名/班级/专业)"
              clearable
              style="width: 300px"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="refreshData" :icon="Refresh" circle />
          </div>
        </div>
      </template>

      <!-- 学生列表表格 -->
      <el-table
        :data="filteredStudents"
        v-loading="loading"
        element-loading-text="加载中..."
        style="width: 100%"
        stripe
        border
        class="student-table"
        :row-class-name="tableRowClassName"
      >
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="userId" label="学号" width="120" sortable />
        <el-table-column prop="name" label="姓名" width="100" sortable />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" effect="dark">
              {{ row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="classId" label="班级" width="100" sortable />
        <el-table-column prop="major" label="专业" width="150" sortable />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetails(row)">详情</el-button>
            <el-button type="danger" size="small" @click="deleteStudentHandler(row.userId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="学生详情" width="500px" class="detail-dialog">
      <el-form label-width="80px" label-position="left">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号:">
              <span>{{ selectedStudent.userId }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名:">
              <span>{{ selectedStudent.name }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别:">
              <span>{{ selectedStudent.gender }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级:">
              <span>{{ selectedStudent.classId }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业:">
              <span>{{ selectedStudent.major }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色:">
              <el-tag type="success">{{ selectedStudent.role }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="电话:">
          <span>{{ selectedStudent.phone }}</span>
        </el-form-item>

        <el-form-item label="邮箱:">
          <span>{{ selectedStudent.email }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增学生对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增学生" width="500px" class="add-dialog">
      <el-form
        :model="newStudent"
        :rules="addFormRules"
        ref="addFormRef"
        label-width="80px"
        label-position="left"
      >
        <el-form-item label="学号" prop="userId">
          <el-input v-model="newStudent.userId" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="newStudent.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="newStudent.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-input v-model="newStudent.classId" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="newStudent.major" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="newStudent.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="newStudent.email" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAllStudents, addStudent, deleteStudent } from '@/api/user'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 学生数据
const students = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const detailDialogVisible = ref(false)
const addDialogVisible = ref(false)
const selectedStudent = ref({})
const addFormRef = ref()

// 新增学生表单数据
const newStudent = ref({
  userId: '',
  name: '',
  gender: '',
  classId: '',
  major: '',
  phone: '',
  email: ''
})

// 表单验证规则
const addFormRules = {
  userId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
}

// 获取学生列表
const fetchStudents = async () => {
  loading.value = true
  try {
    const response = await getAllStudents()

    if (response.status === 200) {
      students.value = response.data
    } else {
      console.error('获取学生列表失败:', response.message)
      ElMessage.error('获取学生列表失败: ' + response.message)
    }
  } catch (error) {
    console.error('获取学生列表异常:', error)
    ElMessage.error('获取学生列表异常: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索过滤后的学生数据
const filteredStudents = computed(() => {
  if (!searchKeyword.value) {
    return students.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return students.value.filter(student =>
    student.userId.toLowerCase().includes(keyword) ||
    student.name.toLowerCase().includes(keyword) ||
    student.classId.toLowerCase().includes(keyword) ||
    student.major.toLowerCase().includes(keyword)
  )
})

// 处理搜索
const handleSearch = () => {
  // 搜索已经在computed中处理
}

// 刷新数据
const refreshData = () => {
  fetchStudents()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  if (rowIndex % 4 === 0) {
    return 'highlight-row'
  }
  return ''
}

// 查看详情
const viewDetails = (student) => {
  selectedStudent.value = student
  detailDialogVisible.value = true
}

// 显示新增表单
const showAddForm = () => {
  // 重置表单数据
  newStudent.value = {
    userId: '',
    name: '',
    gender: '',
    classId: '',
    major: '',
    phone: '',
    email: ''
  }
  addDialogVisible.value = true
}

// 提交新增表单
const submitAddForm = async () => {
  try {
    await addFormRef.value.validate()

    const response = await addStudent(newStudent.value)

    if (response.status === 200) {
      ElMessage.success('新增学生成功')
      addDialogVisible.value = false
      fetchStudents() // 重新加载数据
    } else {
      ElMessage.error('新增学生失败: ' + response.message)
    }
  } catch (error) {
    console.error('新增学生异常:', error)
    ElMessage.error('新增学生异常: ' + error.message)
  }
}

// 删除学生
const deleteStudentHandler = (userId) => {
  ElMessageBox.confirm(
    '确定要删除该学生吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteStudent(userId)

      if (response.status === 200) {
        ElMessage.success('删除成功')
        fetchStudents() // 重新加载数据
      } else {
        ElMessage.error('删除失败: ' + response.message)
      }
    } catch (error) {
      console.error('删除学生异常:', error)
      ElMessage.error('删除学生异常: ' + error.message)
    }
  }).catch(() => {
    // 用户取消删除
    ElMessage.info('已取消删除')
  })
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStudents()
})
</script>

<style lang="scss" scoped>
.student-management-container {
  padding: 20px;
}

.student-management-card {
  min-height: calc(100vh - 120px);

  :deep(.el-card__header) {
    padding: 15px 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;

  .header-actions {
    display: flex;
    align-items: center;
    gap: 10px;
  }
}

/* 表格样式优化 */
.student-table {
  :deep(.el-table__header th) {
    background-color: #f5f7fa;
    color: #333;
    font-weight: bold;
  }

  :deep(.highlight-row) {
    background-color: #f0f9eb;
  }
}

/* 深色主题适配 */
html.dark {
  .student-management-card {
    background-color: #1d1e1f;
    border: none;

    :deep(.el-card__header) {
      background-color: #1d1e1f;
      border-color: #333;
      color: #ffffff;
    }

    :deep(.el-card__body) {
      background-color: #1d1e1f;
      color: #ffffff;
    }
  }

  .student-table {
    :deep(.el-table__header th) {
      background-color: #2d2e2f;
      color: #ffffff;
    }

    :deep(.el-table__row td) {
      background-color: #1d1e1f;
      color: #ffffff;
    }

    :deep(.el-table__row--striped td) {
      background-color: #2a2b2c !important;
    }

    :deep(.el-table) {
      background-color: #1d1e1f;
    }

    :deep(.el-table__empty-block) {
      background-color: #1d1e1f;
    }

    :deep(.el-table__empty-text) {
      color: #ffffff;
    }

    :deep(.el-loading-mask) {
      background-color: rgba(29, 30, 31, 0.8);
    }

    :deep(.highlight-row) {
      background-color: #2d3a2d;
    }
  }

  :deep(.el-table--border th),
  :deep(.el-table--border td) {
    border-color: #333;
  }

  :deep(.el-table::before),
  :deep(.el-table--border::after) {
    background-color: #333;
  }

  .detail-dialog,
  .add-dialog {
    :deep(.el-dialog) {
      background-color: #1d1e1f;
    }

    :deep(.el-dialog__header) {
      background-color: #1d1e1f;
      border-color: #333;
      color: #ffffff;
    }

    :deep(.el-dialog__body) {
      background-color: #1d1e1f;
      color: #ffffff;
    }

    :deep(.el-dialog__footer) {
      background-color: #1d1e1f;
      border-color: #333;
    }

    :deep(.el-form-item__label) {
      color: #ffffff;
    }

    :deep(.el-input__wrapper) {
      background-color: #1d1e1f;
      border-color: #434343;

      .el-input {
        background-color: #1d1e1f;
        color: #ffffff;
      }
    }
  }
}

.detail-dialog,
.add-dialog {
  :deep(.el-form-item) {
    margin-bottom: 15px;
  }

  :deep(.el-form-item__label) {
    font-weight: bold;
  }
}
</style>
