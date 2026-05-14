<template>
  <div class="product-list">
    <h2>商品列表</h2>
    <el-row :gutter="20">
      <el-col :span="6" v-for="product in products" :key="product.id">
        <el-card class="product-card" shadow="hover">
          <div slot="header" class="clearfix">
            <span>{{ product.name }}</span>
            <el-tag :type="getStatusType(product.status)" size="mini" style="float: right;">
              {{ getStatusText(product.status) }}
            </el-tag>
          </div>
          <p>{{ product.description }}</p>
          <p style="color: #409EFF; font-weight: bold;">日租金: ¥{{ product.dailyRate }}</p>
          <p style="color: #E6A23C;">押金: ¥{{ product.deposit }}</p>
          <div style="margin-top: 10px;">
            <el-button 
              type="primary" 
              size="small" 
              @click="openRentalDialog(product)"
              :disabled="product.status !== 'IDLE'"
            >
              立即租赁
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog title="租赁商品" :visible.sync="rentalDialogVisible" width="500px" :close-on-click-modal="false">
      <el-steps :active="currentStep" finish-status="success" align-center style="margin-bottom: 30px;">
        <el-step title="填写信息"></el-step>
        <el-step title="支付押金"></el-step>
        <el-step title="租赁成功"></el-step>
      </el-steps>

      <div v-if="currentStep === 0">
        <el-form :model="rentalForm" label-width="100px">
          <el-form-item label="商品名称">
            <span>{{ selectedProduct ? selectedProduct.name : '' }}</span>
          </el-form-item>
          <el-form-item label="用户姓名">
            <el-input v-model="rentalForm.userName" placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="rentalForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="rentalForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="租赁天数">
            <span style="color: #409EFF; font-weight: bold;">{{ calculateDays }} 天</span>
          </el-form-item>
          <el-form-item label="租金总额">
            <span style="color: #F56C6C; font-weight: bold; font-size: 18px;">¥{{ calculateAmount }}</span>
          </el-form-item>
          <el-form-item label="押金金额">
            <span style="color: #E6A23C; font-weight: bold; font-size: 18px;">¥{{ selectedProduct ? selectedProduct.deposit : 0 }}</span>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="currentStep === 1" class="payment-step">
        <el-alert
          title="请确认支付信息"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        >
        </el-alert>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ currentOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="租赁天数">{{ currentOrder.rentalDays }} 天</el-descriptions-item>
          <el-descriptions-item label="租金总额">
            <span style="color: #F56C6C; font-weight: bold;">¥{{ currentOrder.rentalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付押金">
            <span style="color: #E6A23C; font-weight: bold;">¥{{ currentOrder.deposit }}</span>
          </el-descriptions-item>
        </el-descriptions>
        <div style="text-align: center; margin-top: 20px;">
          <el-button type="primary" size="large" :loading="payLoading" @click="handlePay">
            确认支付押金
          </el-button>
        </div>
      </div>

      <div v-if="currentStep === 2" class="success-step">
        <div style="text-align: center; padding: 20px 0;">
          <i class="el-icon-success" style="font-size: 72px; color: #67C23A;"></i>
          <h2 style="color: #67C23A; margin-top: 20px;">租赁成功！</h2>
          <p style="color: #909399; margin-top: 10px;">押金已冻结，商品已为您预留</p>
        </div>
        <el-descriptions :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="冻结状态">
            <el-tag type="success">已冻结</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="冻结流水">{{ currentOrder.freezeSerialNo }}</el-descriptions-item>
          <el-descriptions-item label="冻结金额">
            <span style="color: #E6A23C; font-weight: bold;">¥{{ currentOrder.deposit }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button v-if="currentStep === 0" @click="rentalDialogVisible = false">取 消</el-button>
        <el-button v-if="currentStep === 0" type="primary" @click="submitRental">下一步</el-button>
        <el-button v-if="currentStep === 1" @click="currentStep = 0">返回修改</el-button>
        <el-button v-if="currentStep === 2" @click="goToOrders">查看订单</el-button>
        <el-button v-if="currentStep === 2" type="primary" @click="continueRental">继续租赁</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProducts, createOrder, freezeDeposit as apiFreezeDeposit } from '../api'
import router from '../router'

export default {
  name: 'ProductList',
  data() {
    return {
      products: [],
      rentalDialogVisible: false,
      currentStep: 0,
      selectedProduct: null,
      currentOrder: {},
      payLoading: false,
      rentalForm: {
        productId: null,
        userId: 1,
        userName: '',
        startTime: null,
        endTime: null
      }
    }
  },
  computed: {
    calculateDays() {
      if (!this.rentalForm.startTime || !this.rentalForm.endTime) return 0
      const start = new Date(this.rentalForm.startTime)
      const end = new Date(this.rentalForm.endTime)
      const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
      return days > 0 ? days : 0
    },
    calculateAmount() {
      if (!this.selectedProduct) return 0
      return (this.selectedProduct.dailyRate * this.calculateDays).toFixed(2)
    }
  },
  mounted() {
    this.loadProducts()
  },
  methods: {
    async loadProducts() {
      try {
        const response = await getProducts()
        this.products = response.data
      } catch (error) {
        this.$message.error('加载商品失败')
      }
    },
    getStatusType(status) {
      const types = {
        'IDLE': 'success',
        'RENTED': 'warning',
        'IN_REPAIR': 'danger',
        'OFF_SHELF': 'info'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        'IDLE': '空闲',
        'RENTED': '已租出',
        'IN_REPAIR': '维修中',
        'OFF_SHELF': '下架'
      }
      return texts[status] || status
    },
    openRentalDialog(product) {
      this.selectedProduct = product
      this.rentalForm.productId = product.id
      this.currentStep = 0
      this.currentOrder = {}
      this.rentalDialogVisible = true
    },
    async submitRental() {
      if (!this.rentalForm.userName || !this.rentalForm.startTime || !this.rentalForm.endTime) {
        this.$message.warning('请填写完整信息')
        return
      }
      if (this.calculateDays <= 0) {
        this.$message.warning('结束时间必须晚于开始时间')
        return
      }
      try {
        const response = await createOrder(this.rentalForm)
        this.currentOrder = response.data
        this.currentStep = 1
      } catch (error) {
        this.$message.error('创建订单失败')
      }
    },
    async handlePay() {
      this.payLoading = true
      try {
        const response = await apiFreezeDeposit(this.currentOrder.id)
        this.currentOrder = response.data
        this.currentStep = 2
        this.$message.success('支付成功！押金已冻结')
        this.loadProducts()
      } catch (error) {
        this.$message.error('支付失败，请重试')
      } finally {
        this.payLoading = false
      }
    },
    goToOrders() {
      this.rentalDialogVisible = false
      router.push('/orders')
    },
    continueRental() {
      this.rentalDialogVisible = false
      this.rentalForm.userName = ''
      this.rentalForm.startTime = null
      this.rentalForm.endTime = null
    }
  }
}
</script>

<style scoped>
.product-list {
  padding: 20px;
}
.product-card {
  margin-bottom: 20px;
}
.payment-step {
  padding: 10px 0;
}
.success-step {
  padding: 10px 0;
}
</style>
