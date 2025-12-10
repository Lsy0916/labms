<template>
  <div class="user-management-container">
    <el-card class="user-management-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showAddForm" :icon="Plus">新增用户</el-button>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户 (ID/姓名)"
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

      <!-- 用户类型标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="学生" name="students">
          <el-table
            :data="filteredUsers"
            v-loading="loading"
            element-loading-text="加载中..."
            style="width: 100%"
            stripe
            border
            class="user-table"
            :row-class-name="tableRowClassName"
          >
            <el-table-column type="index" label="序号" width="70" align="center" />
            <el-table-column prop="userId" label="学号" width="120" sortable />
            <el-table-column prop="name" label="姓名" width="120" sortable />
            <el-table-column prop="gender" label="性别" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" effect="dark">
                  {{ row.gender || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="classId" label="班级" width="100" sortable />
            <el-table-column prop="major" label="专业" width="150" sortable />
            <el-table-column prop="phone" label="电话" width="120" />
            <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
            <el-table-column prop="role" label="角色" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="success">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editUser(row)" :icon="Edit">编辑</el-button>
                <el-button type="primary" size="small" @click="viewUserDetails(row)" :icon="View">详情</el-button>
                <el-button type="danger" size="small" @click="deleteUserHandler(row)" :icon="Delete">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="教师" name="teachers">
          <el-table
            :data="filteredUsers"
            v-loading="loading"
            element-loading-text="加载中..."
            style="width: 100%"
            stripe
            border
            class="user-table"
            :row-class-name="tableRowClassName"
          >
            <el-table-column type="index" label="序号" width="70" align="center" />
            <el-table-column prop="teacherId" label="工号" width="120" sortable />
            <el-table-column prop="name" label="姓名" width="120" sortable />
            <el-table-column prop="gender" label="性别" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" effect="dark">
                  {{ row.gender || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="department" label="部门" width="150" sortable />
            <el-table-column prop="title" label="职称" width="120" sortable />
            <el-table-column prop="phone" label="电话" width="120" />
            <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === '在职' ? 'success' : 'info'">
                  {{ row.status || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="success">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editUser(row)" :icon="Edit">编辑</el-button>
                <el-button type="primary" size="small" @click="viewUserDetails(row)" :icon="View">详情</el-button>
                <el-button type="danger" size="small" @click="deleteUserHandler(row)" :icon="Delete">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="管理员" name="admins">
          <el-table
            :data="filteredUsers"
            v-loading="loading"
            element-loading-text="加载中..."
            style="width: 100%"
            stripe
            border
            class="user-table"
            :row-class-name="tableRowClassName"
          >
            <el-table-column type="index" label="序号" width="70" align="center" />
            <el-table-column prop="adminId" label="工号" width="120" sortable />
            <el-table-column prop="name" label="姓名" width="120" sortable />
            <el-table-column prop="gender" label="性别" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" effect="dark">
                  {{ row.gender || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="department" label="部门" width="150" sortable />
            <el-table-column prop="position" label="职位" width="120" sortable />
            <el-table-column prop="phone" label="电话" width="120" />
            <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === '在职' ? 'success' : 'info'">
                  {{ row.status || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="success">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editUser(row)" :icon="Edit">编辑</el-button>
                <el-button type="primary" size="small" @click="viewUserDetails(row)" :icon="View">详情</el-button>
                <el-button type="danger" size="small" @click="deleteUserHandler(row)" :icon="Delete">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" :title="'编辑' + getUserTypeLabel()" width="600px" class="edit-dialog">
      <el-form
        :model="editingUser"
        :rules="editFormRules"
        ref="editFormRef"
        label-width="100px"
        label-position="right"
        :disabled="formLoading"
      >
        <!-- 公共字段 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="getIdLabel()" prop="id">
              <el-input v-model="editingUser.id" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="editingUser.name" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editingUser.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="editingUser.phone" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editingUser.email" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 特定字段 -->
        <div v-if="activeTab === 'students'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="班级" prop="classId">
                <el-input v-model="editingUser.classId" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" prop="major">
                <el-input v-model="editingUser.major" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div v-else-if="activeTab === 'teachers'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门" prop="department">
                <el-input v-model="editingUser.department" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职称" prop="title">
                <el-input v-model="editingUser.title" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学历" prop="degree">
                <el-input v-model="editingUser.degree" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="入职日期" prop="hireDate">
                <el-date-picker
                  v-model="editingUser.hireDate"
                  type="date"
                  placeholder="请选择入职日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div v-else-if="activeTab === 'admins'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门" prop="department">
                <el-input v-model="editingUser.department" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位" prop="position">
                <el-input v-model="editingUser.position" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入职日期" prop="hireDate">
                <el-date-picker
                  v-model="editingUser.hireDate"
                  type="date"
                  placeholder="请选择入职日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="warning" @click="resetPassword(editingUser)" :loading="formLoading">重置密码</el-button>
          <el-button type="primary" @click="submitEditForm" :loading="formLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增用户对话框 -->
    <el-dialog v-model="addDialogVisible" :title="'新增' + getUserTypeLabel()" width="600px" class="add-dialog">
      <el-form
        :model="newUser"
        :rules="addFormRules"
        ref="addFormRef"
        label-width="100px"
        label-position="right"
        :disabled="formLoading"
      >
        <!-- 公共字段 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="getIdLabel()" prop="id">
              <el-input v-model="newUser.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="newUser.name" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="newUser.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="newUser.phone" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="newUser.email" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 特定字段 -->
        <div v-if="activeTab === 'students'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="班级" prop="classId">
                <el-input v-model="newUser.classId" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" prop="major">
                <el-input v-model="newUser.major" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div v-else-if="activeTab === 'teachers'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门" prop="department">
                <el-input v-model="newUser.department" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职称" prop="title">
                <el-input v-model="newUser.title" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学历" prop="degree">
                <el-input v-model="newUser.degree" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="入职日期" prop="hireDate">
                <el-date-picker
                  v-model="newUser.hireDate"
                  type="date"
                  placeholder="请选择入职日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div v-else-if="activeTab === 'admins'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门" prop="department">
                <el-input v-model="newUser.department" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位" prop="position">
                <el-input v-model="newUser.position" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入职日期" prop="hireDate">
                <el-date-picker
                  v-model="newUser.hireDate"
                  type="date"
                  placeholder="请选择入职日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddForm" :loading="formLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="'用户详情'" width="800px" class="detail-dialog">
      <el-descriptions :column="2" border>
        <template #extra>
          <el-tag v-if="currentUserDetail" :type="getUserRoleTagType(currentUserDetail)">
            {{ currentUserDetail.role }}
          </el-tag>
        </template>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.id !== undefined" label="ID">
          {{ currentUserDetail.id }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.userId" label="学号">
          {{ currentUserDetail.userId }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.teacherId" label="工号">
          {{ currentUserDetail.teacherId }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.adminId" label="工号">
          {{ currentUserDetail.adminId }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.name !== undefined" label="姓名">
          {{ currentUserDetail.name || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.gender !== undefined" label="性别">
          <el-tag :type="currentUserDetail.gender === '男' ? 'primary' : 'danger'" effect="dark">
            {{ currentUserDetail.gender || '未知' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.classId !== undefined" label="班级">
          {{ currentUserDetail.classId || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.department !== undefined" label="部门">
          {{ currentUserDetail.department || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.major !== undefined" label="专业">
          {{ currentUserDetail.major || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.title !== undefined" label="职称">
          {{ currentUserDetail.title || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.position !== undefined" label="职位">
          {{ currentUserDetail.position || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.degree !== undefined" label="学历">
          {{ currentUserDetail.degree || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.phone !== undefined" label="电话">
          {{ currentUserDetail.phone || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.email !== undefined" label="邮箱">
          {{ currentUserDetail.email || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.hireDate !== undefined" label="入职日期">
          {{ currentUserDetail.hireDate || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.status !== undefined" label="状态">
          <el-tag :type="currentUserDetail.status === '在职' ? 'success' : 'info'">
            {{ currentUserDetail.status || '未知' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.createdAt !== undefined" label="创建时间">
          {{ currentUserDetail.createdAt || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.updatedAt !== undefined" label="更新时间">
          {{ currentUserDetail.updatedAt || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUserDetail && currentUserDetail.roleId !== undefined" label="角色ID">
          {{ currentUserDetail.roleId || '暂无' }}
        </el-descriptions-item>

        <el-descriptions-item label="头像">
          <el-avatar :src="getUserAvatarPath || currentUserDetail?.avatarUrl || ''" shape="square">
            {{ currentUserDetail?.name?.substring(0, 1) || 'U' }}
          </el-avatar>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getAllUsers, getAllTeachers, getAllAdmins, createUser, deleteUser, updateUser, resetUserPassword } from '@/api/admin'
import { Search, Refresh, Plus, Delete, View, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 数据
const activeTab = ref('students')
const users = ref([])
const loading = ref(false)
const formLoading = ref(false)
const searchKeyword = ref('')
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const addFormRef = ref()
const editFormRef = ref()
const currentUserDetail = ref(null)

// 新增用户表单数据
const newUser = ref({
  id: '',
  name: '',
  gender: '',
  phone: '',
  email: '',
  classId: '',
  major: '',
  department: '',
  title: '',
  position: '',
  degree: '',
  hireDate: ''
})

// 编辑用户表单数据
const editingUser = ref({
  id: '',
  name: '',
  gender: '',
  phone: '',
  email: '',
  classId: '',
  major: '',
  department: '',
  title: '',
  position: '',
  degree: '',
  hireDate: ''
})

// 计算用户头像路径
const getUserAvatarPath = computed(() => {
  if (!currentUserDetail.value) return ''

  const userId = currentUserDetail.value.userId ||
                 currentUserDetail.value.teacherId ||
                 currentUserDetail.value.adminId ||
                 ''

  if (!userId) return ''

  // 如果已经有完整的头像URL，则直接返回
  if (currentUserDetail.value.avatarUrl) {
    // 如果avatarUrl已经是完整路径
    if (currentUserDetail.value.avatarUrl.startsWith('http') || currentUserDetail.value.avatarUrl.startsWith('/')) {
      return currentUserDetail.value.avatarUrl
    }
    // 如果是相对路径，补充完整路径
    if (currentUserDetail.value.role === 'student') {
      return `/src/assets/avatar/student/${currentUserDetail.value.avatarUrl}`
    } else if (currentUserDetail.value.role === 'teacher') {
      return `/src/assets/avatar/teacher/${currentUserDetail.value.avatarUrl}`
    } else if (currentUserDetail.value.role === 'admin') {
      return `/src/assets/avatar/admin/${currentUserDetail.value.avatarUrl}`
    }
  }

  // 否则按照规则生成路径
  if (currentUserDetail.value.role === 'student') {
    return `/src/assets/avatar/student/${userId}.jpg`
  } else if (currentUserDetail.value.role === 'teacher') {
    return `/src/assets/avatar/teacher/${userId}.jpg`
  } else if (currentUserDetail.value.role === 'admin') {
    return `/src/assets/avatar/admin/${userId}.jpg`
  }

  return ''
})

// 表单验证规则
const addFormRules = computed(() => {
  const rules = {
    id: [
      { required: true, message: `请输入${getIdLabel()}`, trigger: 'blur' }
    ],
    name: [
      { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    gender: [
      { required: true, message: '请选择性别', trigger: 'change' }
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

  // 根据不同用户类型添加特定规则
  if (activeTab.value === 'students') {
    rules.classId = [
      { required: true, message: '请输入班级', trigger: 'blur' }
    ]
    rules.major = [
      { required: true, message: '请输入专业', trigger: 'blur' }
    ]
  } else if (activeTab.value === 'teachers') {
    rules.department = [
      { required: true, message: '请输入部门', trigger: 'blur' }
    ]
    rules.title = [
      { required: true, message: '请输入职称', trigger: 'blur' }
    ]
    rules.hireDate = [
      { required: true, message: '请选择入职日期', trigger: 'change' }
    ]
  } else if (activeTab.value === 'admins') {
    rules.department = [
      { required: true, message: '请输入部门', trigger: 'blur' }
    ]
    rules.position = [
      { required: true, message: '请输入职位', trigger: 'blur' }
    ]
    rules.hireDate = [
      { required: true, message: '请选择入职日期', trigger: 'change' }
    ]
  }

  return rules
})

// 编辑表单验证规则
const editFormRules = computed(() => {
  const rules = {
    name: [
      { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    gender: [
      { required: true, message: '请选择性别', trigger: 'change' }
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

  // 根据不同用户类型添加特定规则
  if (activeTab.value === 'students') {
    rules.classId = [
      { required: true, message: '请输入班级', trigger: 'blur' }
    ]
    rules.major = [
      { required: true, message: '请输入专业', trigger: 'blur' }
    ]
  } else if (activeTab.value === 'teachers') {
    rules.department = [
      { required: true, message: '请输入部门', trigger: 'blur' }
    ]
    rules.title = [
      { required: true, message: '请输入职称', trigger: 'blur' }
    ]
    rules.hireDate = [
      { required: true, message: '请选择入职日期', trigger: 'change' }
    ]
  } else if (activeTab.value === 'admins') {
    rules.department = [
      { required: true, message: '请输入部门', trigger: 'blur' }
    ]
    rules.position = [
      { required: true, message: '请输入职位', trigger: 'blur' }
    ]
    rules.hireDate = [
      { required: true, message: '请选择入职日期', trigger: 'change' }
    ]
  }

  return rules
})

// 获取用户类型标签
const getUserTypeLabel = () => {
  const labels = {
    students: '学生',
    teachers: '教师',
    admins: '管理员'
  }
  return labels[activeTab.value] || '用户'
}

// 获取用户角色标签类型
const getUserRoleTagType = (user) => {
  if (!user) return 'info'

  const roleTypes = {
    'admin': 'danger',
    'teacher': 'warning',
    'student': 'success'
  }
  return roleTypes[user.role] || 'info'
}

// 获取ID字段标签
const getIdLabel = () => {
  const labels = {
    students: '学号',
    teachers: '工号',
    admins: '工号'
  }
  return labels[activeTab.value] || 'ID'
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    let response

    switch (activeTab.value) {
      case 'students':
        response = await getAllUsers()
        break
      case 'teachers':
        response = await getAllTeachers()
        break
      case 'admins':
        response = await getAllAdmins()
        break
      default:
        response = await getAllUsers()
    }

    if (response.status === 200) {
      users.value = response.data
    } else {
      console.error(`获取${activeTab.value}列表失败:`, response.message)
      ElMessage.error(`获取${activeTab.value}列表失败: ` + response.message)
    }
  } catch (error) {
    console.error(`获取${activeTab.value}列表异常:`, error)
    ElMessage.error(`获取${activeTab.value}列表异常: ` + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索过滤后的用户数据
const filteredUsers = computed(() => {
  if (!searchKeyword.value) {
    return users.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  let filtered = []

  switch (activeTab.value) {
    case 'students':
      filtered = users.value.filter(user =>
        (user.userId && user.userId.toLowerCase().includes(keyword)) ||
        (user.name && user.name.toLowerCase().includes(keyword)) ||
        (user.classId && user.classId.toLowerCase().includes(keyword)) ||
        (user.major && user.major.toLowerCase().includes(keyword))
      )
      break

    case 'teachers':
      filtered = users.value.filter(user =>
        (user.teacherId && user.teacherId.toLowerCase().includes(keyword)) ||
        (user.name && user.name.toLowerCase().includes(keyword)) ||
        (user.department && user.department.toLowerCase().includes(keyword)) ||
        (user.title && user.title.toLowerCase().includes(keyword))
      )
      break

    case 'admins':
      filtered = users.value.filter(user =>
        (user.adminId && user.adminId.toLowerCase().includes(keyword)) ||
        (user.name && user.name.toLowerCase().includes(keyword)) ||
        (user.department && user.department.toLowerCase().includes(keyword)) ||
        (user.position && user.position.toLowerCase().includes(keyword))
      )
      break

    default:
      filtered = users.value.filter(user =>
        (user.userId && user.userId.toLowerCase().includes(keyword)) ||
        (user.name && user.name.toLowerCase().includes(keyword))
      )
  }

  return filtered
})

// 处理搜索
const handleSearch = () => {
  // 搜索已在 computed 中处理
}

// 刷新数据
const refreshData = () => {
  fetchUsers()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  if (rowIndex % 4 === 0) {
    return 'highlight-row'
  }
  return ''
}

// 标签页切换
const handleTabChange = () => {
  searchKeyword.value = ''
  fetchUsers()
}

// 显示新增表单
const showAddForm = () => {
  // 重置表单数据
  newUser.value = {
    id: '',
    name: '',
    gender: '',
    phone: '',
    email: '',
    classId: '',
    major: '',
    department: '',
    title: '',
    position: '',
    degree: '',
    hireDate: ''
  }
  addDialogVisible.value = true
}

// 显示编辑表单
const editUser = (user) => {
  // 初始化编辑表单数据
  editingUser.value = {
    id: user.userId || user.teacherId || user.adminId || '',
    name: user.name || '',
    gender: user.gender || '',
    phone: user.phone || '',
    email: user.email || '',
    classId: user.classId || '',
    major: user.major || '',
    department: user.department || '',
    title: user.title || '',
    position: user.position || '',
    degree: user.degree || '',
    hireDate: user.hireDate || ''
  }
  editDialogVisible.value = true
}

// 查看用户详情
const viewUserDetails = (user) => {
  currentUserDetail.value = user
  detailDialogVisible.value = true
}

// 提交新增表单
const submitAddForm = async () => {
  if (!addFormRef.value) return

  try {
    await addFormRef.value.validate()
    formLoading.value = true

    // 构造提交数据
    let submitData = {}

    if (activeTab.value === 'students') {
      submitData = {
        userId: newUser.value.id,
        name: newUser.value.name,
        gender: newUser.value.gender,
        classId: newUser.value.classId,
        major: newUser.value.major,
        phone: newUser.value.phone,
        email: newUser.value.email,
        roleId: 'student',
        role: 'student',
        password: '123456',
        avatarUrl: 'default_avatar.png'
      }
    } else if (activeTab.value === 'teachers') {
      submitData = {
        teacherId: newUser.value.id,
        name: newUser.value.name,
        gender: newUser.value.gender,
        department: newUser.value.department,
        title: newUser.value.title,
        phone: newUser.value.phone,
        email: newUser.value.email,
        degree: newUser.value.degree,
        hireDate: newUser.value.hireDate,
        status: '在职',
        password: '123456',
        role: 'teacher',
        roleId: 'teacher',
        avatarUrl: 'default_avatar.png'
      }
    } else if (activeTab.value === 'admins') {
      submitData = {
        adminId: newUser.value.id,
        name: newUser.value.name,
        gender: newUser.value.gender,
        department: newUser.value.department,
        position: newUser.value.position,
        phone: newUser.value.phone,
        email: newUser.value.email,
        hireDate: newUser.value.hireDate,
        status: '在职',
        password: '123456',
        role: 'admin',
        roleId: 'admin',
        avatarUrl: 'default_avatar.png'
      }
    }

    const response = await createUser(submitData)

    if (response.status === 200) {
      ElMessage.success('新增用户成功')
      addDialogVisible.value = false
      fetchUsers() // 重新加载数据
    } else {
      ElMessage.error('新增用户失败: ' + response.message)
    }
  } catch (error) {
    console.error('新增用户异常:', error)
    ElMessage.error('新增用户异常: ' + (error.message || error))
  } finally {
    formLoading.value = false
  }
}

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    formLoading.value = true

    // 构造提交数据
    let submitData = {
      roleId: activeTab.value === 'students' ? 'student' : activeTab.value === 'teachers' ? 'teacher' : 'admin',
      userId: editingUser.value.id
    }

    if (activeTab.value === 'students') {
      submitData = {
        ...submitData,
        studentId: editingUser.value.id,
        name: editingUser.value.name,
        gender: editingUser.value.gender,
        classId: editingUser.value.classId,
        major: editingUser.value.major,
        phone: editingUser.value.phone,
        email: editingUser.value.email
      }
    } else if (activeTab.value === 'teachers') {
      submitData = {
        ...submitData,
        teacherId: editingUser.value.id,
        name: editingUser.value.name,
        gender: editingUser.value.gender,
        department: editingUser.value.department,
        title: editingUser.value.title,
        phone: editingUser.value.phone,
        email: editingUser.value.email,
        degree: editingUser.value.degree,
        hireDate: editingUser.value.hireDate
      }
    } else if (activeTab.value === 'admins') {
      submitData = {
        ...submitData,
        adminId: editingUser.value.id,
        name: editingUser.value.name,
        gender: editingUser.value.gender,
        department: editingUser.value.department,
        position: editingUser.value.position,
        phone: editingUser.value.phone,
        email: editingUser.value.email
      }
    }

    const response = await updateUser(submitData)

    if (response.status === 200) {
      ElMessage.success('更新用户信息成功')
      editDialogVisible.value = false
      fetchUsers() // 重新加载数据
    } else {
      ElMessage.error('更新用户信息失败: ' + response.message)
    }
  } catch (error) {
    console.error('更新用户信息异常:', error)
    ElMessage.error('更新用户信息异常: ' + (error.message || error))
  } finally {
    formLoading.value = false
  }
}

// 删除用户
const deleteUserHandler = (user) => {
  ElMessageBox.confirm(
    `确定要删除用户"${user.name}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      // 根据不同用户类型构造参数
      let params = {}

      if (activeTab.value === 'students') {
        params = {
          roleId: 'student',
          userId: user.userId
        }
      } else if (activeTab.value === 'teachers') {
        params = {
          roleId: 'teacher',
          userId: user.teacherId
        }
      } else if (activeTab.value === 'admins') {
        params = {
          roleId: 'admin',
          userId: user.adminId
        }
      }

      const response = await deleteUser(params)

      if (response.status === 200) {
        ElMessage.success('删除成功')
        fetchUsers() // 重新加载数据
      } else {
        ElMessage.error('删除失败: ' + response.message)
      }
    } catch (error) {
      console.error('删除用户异常:', error)
      ElMessage.error('删除用户异常: ' + (error.message || error))
    }
  }).catch(() => {
    // 用户取消删除
    ElMessage.info('已取消删除')
  })
}

// 重置用户密码
const resetPassword = (user) => {
  ElMessageBox.confirm(
    `确定要重置用户"${user.name}"的密码吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      // 根据不同用户类型构造参数
      let roleId = '';
      let userId = '';

      if (activeTab.value === 'students') {
        roleId = 'student';
        userId = user.userId || user.id;
      } else if (activeTab.value === 'teachers') {
        roleId = 'teacher';
        userId = user.teacherId || user.id;
      } else if (activeTab.value === 'admins') {
        roleId = 'admin';
        userId = user.adminId || user.id;
      }

      const response = await resetUserPassword(roleId, userId);

      if (response.status === 200) {
        ElMessage.success('密码重置成功，默认密码为123456');
        // 如果在编辑对话框中操作，不关闭对话框
      } else {
        ElMessage.error('密码重置失败: ' + response.message);
      }
    } catch (error) {
      console.error('重置密码异常:', error);
      ElMessage.error('重置密码异常: ' + (error.message || error));
    }
  }).catch(() => {
    // 用户取消操作
    ElMessage.info('已取消操作');
  });
}

// 监听搜索关键词变化
watch(searchKeyword, () => {
  // 搜索已在 computed 中处理
})

// 监听标签页变化，重置表单验证
watch(activeTab, () => {
  if (addFormRef.value) {
    addFormRef.value.clearValidate()
  }
})

// 组件挂载时获取数据
onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.user-management-container {
  padding: 20px;
}

.user-management-card {
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
.user-table {
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
  .user-management-card {
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

  .user-table {
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
  }

  .detail-dialog {
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

    :deep(.el-descriptions__label) {
      background-color: #2d2e2f;
      color: #ffffff;
    }

    :deep(.el-descriptions__content) {
      background-color: #1d1e1f;
      color: #ffffff;
    }

    :deep(.el-descriptions__body) {
      background-color: #1d1e1f;
      color: #ffffff;
    }
  }
}
</style>
