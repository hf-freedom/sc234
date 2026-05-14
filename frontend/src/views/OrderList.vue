<template>
  <div class="order-list">
    <h2>订单管理</h2>
    <el-button type="primary" @click="loadOrders" size="small">刷新</el-button>
    
    <el-table :data="orders" border style="width: 100%; margin-top: 20px;">
      <el-table-column prop="orderNo" label="订单编号" width="130"></el-table-column>
      <el-table-column prop="productName" label="商品名称" width="120"></el-table-column>
      <el-table-column prop="userName" label="用户" width="100"></el-table-column>
      <el-table-column prop="rentalDays" label="租赁天数" width="90"></el-table-column>
      <el-table-column prop="rentalAmount" label="租金" width="90">
        <template slot-scope="scope">¥{{ scope.row.rentalAmount }}</template>
      </el-table-column>
      <el-table-column prop="deposit" label="押金" width="90">
        <template slot-scope="scope">¥{{ scope.row.deposit }}</template>
      </el-table-column>
      <el-table-column prop="freezeStatus" label="冻结状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.freezeStatus === '已冻结' ? 'success' : 'info'" size="mini">
            {{ scope.row.freezeStatus || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="freezeSerialNo" label="冻结流水" width="130">
        <template slot-scope="scope">{{ scope.row.freezeSerialNo || '-' }}</template>
      </el-table-column>
      <el-table-column prop="overdueDays" label="逾期天数" width="90">
        <template slot-scope="scope">{{ scope.row.overdueDays || 0 }}</template>
      </el-table-column>
      <el-table-column prop="overdueAmount" label="逾期费用" width="90">
        <template slot-scope="scope">¥{{ scope.row.overdueAmount || 0 }}</template>
      </el-table-column>
      <el-table-column prop="damageAmount" label="损坏扣款" width="90">
        <template slot-scope="scope">¥{{ scope.row.damageAmount || 0 }}</template>
      </el-table-column>
      <el-table-column prop="refundAmount" label="退款金额" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.refundAmount > 0" style="color: #67C23A; font-weight: bold;">
            ¥{{ scope.row.refundAmount }}
          </span>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="mini">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template slot-scope="scope">
          <el-button 
            type="warning" 
            size="mini" 
            @click="handleFreezeDeposit(scope.row.id)"
            v-if="scope.row.status === 'PENDING_PAYMENT'"
          >
            冻结押金
          </el-button>
          <el-button 
            type="success" 
            size="mini" 
            @click="confirmRental(scope.row.id)"
            v-if="scope.row.status === 'DEPOSIT_FROZEN'"
          >
            确认租赁
          </el-button>
          <el-button 
            type="info" 
            size="mini" 
            @click="openReturnDialog(scope.row)"
            v-if="scope.row.status === 'RENTING'"
          >
            归还验收
          </el-button>
          <el-button 
            type="primary" 
            size="mini" 
            @click="openEarlyReturnDialog(scope.row)"
            v-if="scope.row.status === 'RENTING'"
          >
            提前归还
          </el-button>
          <el-button 
            type="text" 
            size="mini" 
            @click="viewOrderDetail(scope.row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="归还验收" :visible.sync="returnDialogVisible" width="500px">
      <el-form :model="returnForm" label-width="120px">
        <el-form-item label="是否有损坏">
          <el-switch v-model="returnForm.hasDamage"></el-switch>
        </el-form-item>
        <el-form-item label="损坏金额" v-if="returnForm.hasDamage">
          <el-input-number v-model="returnForm.damageAmount" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="损坏描述" v-if="returnForm.hasDamage">
          <el-input type="textarea" v-model="returnForm.damageDescription"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="returnDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReturn">确认归还</el-button>
      </div>
    </el-dialog>

    <el-dialog title="提前归还" :visible.sync="earlyReturnDialogVisible" width="550px">
      <el-alert
        title="提前归还将按规则退还部分租金"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px;"
      >
      </el-alert>
      
      <el-descriptions :column="1" border v-if="selectedOrder">
        <el-descriptions-item label="订单编号">{{ selectedOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ selectedOrder.productName }}</el-descriptions-item>
        <el-descriptions-item label="原租赁天数">{{ selectedOrder.rentalDays }} 天</el-descriptions-item>
        <el-descriptions-item label="实际使用天数">{{ actualDays }} 天</el-descriptions-item>
        <el-descriptions-item label="剩余天数">
          <span style="color: #E6A23C;">{{ remainingDays }} 天</span>
        </el-descriptions-item>
        <el-descriptions-item label="原租金总额">¥{{ selectedOrder.rentalAmount }}</el-descriptions-item>
        <el-descriptions-item label="实际租金">
          <span style="color: #F56C6C;">¥{{ actualRentalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="预计退还租金">
          <span style="color: #67C23A; font-weight: bold; font-size: 16px;">¥{{ estimatedRefund }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="退款规则说明">
          <span style="color: #909399; font-size: 12px;">提前归还将退还剩余天数租金的50%</span>
        </el-descriptions-item>
      </el-descriptions>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="earlyReturnDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitEarlyReturn" :loading="earlyReturnLoading">
          确认提前归还
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="550px">
      <el-descriptions :column="1" border v-if="detailOrder">
        <el-descriptions-item label="订单编号">{{ detailOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ detailOrder.productName }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ detailOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="租赁开始时间">{{ formatDate(detailOrder.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="租赁结束时间">{{ formatDate(detailOrder.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="实际归还时间">{{ formatDate(detailOrder.actualReturnTime) }}</el-descriptions-item>
        <el-descriptions-item label="租赁天数">{{ detailOrder.rentalDays }} 天</el-descriptions-item>
        <el-descriptions-item label="日租金">¥{{ detailOrder.dailyRate }}</el-descriptions-item>
        <el-descriptions-item label="租金总额">¥{{ detailOrder.rentalAmount }}</el-descriptions-item>
        <el-descriptions-item label="押金">¥{{ detailOrder.deposit }}</el-descriptions-item>
        <el-descriptions-item label="逾期天数">{{ detailOrder.overdueDays || 0 }} 天</el-descriptions-item>
        <el-descriptions-item label="逾期费用">¥{{ detailOrder.overdueAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="损坏扣款">¥{{ detailOrder.damageAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">
          <span style="color: #67C23A; font-weight: bold; font-size: 16px;">¥{{ detailOrder.refundAmount || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(detailOrder.status)" size="mini">
            {{ getStatusText(detailOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="损坏说明" v-if="detailOrder.damageDescription">
          {{ detailOrder.damageDescription }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getOrders, freezeDeposit as apiFreezeDeposit, confirmRental, returnProduct, earlyReturn as apiEarlyReturn } from '../api'

export default {
  name: 'OrderList',
  data() {
    return {
      orders: [],
      returnDialogVisible: false,
      earlyReturnDialogVisible: false,
      detailDialogVisible: false,
      selectedOrder: null,
      detailOrder: null,
      earlyReturnLoading: false,
      returnForm: {
        orderId: null,
        hasDamage: false,
        damageAmount: 0,
        damageDescription: ''
      }
    }
  },
  computed: {
    actualDays() {
      if (!this.selectedOrder) return 0
      const start = new Date(this.selectedOrder.startTime)
      const now = new Date()
      const days = Math.ceil((now - start) / (1000 * 60 * 60 * 24))
      return days > 0 ? days : 1
    },
    remainingDays() {
      if (!this.selectedOrder) return 0
      return Math.max(0, this.selectedOrder.rentalDays - this.actualDays)
    },
    actualRentalAmount() {
      if (!this.selectedOrder) return 0
      return (this.selectedOrder.dailyRate * this.actualDays).toFixed(2)
    },
    estimatedRefund() {
      if (!this.selectedOrder) return 0
      return (this.selectedOrder.dailyRate * this.remainingDays * 0.5).toFixed(2)
    }
  },
  mounted() {
    this.loadOrders()
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    async loadOrders() {
      try {
        const response = await getOrders()
        this.orders = response.data
      } catch (error) {
        this.$message.error('加载订单失败')
      }
    },
    getStatusType(status) {
      const types = {
        'PENDING_PAYMENT': 'info',
        'DEPOSIT_FROZEN': 'warning',
        'RENTING': 'success',
        'RETURNED': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        'PENDING_PAYMENT': '待支付',
        'DEPOSIT_FROZEN': '押金已冻结',
        'RENTING': '租赁中',
        'RETURNED': '已归还',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return texts[status] || status
    },
    async handleFreezeDeposit(id) {
      try {
        await apiFreezeDeposit(id)
        this.$message.success('押金冻结成功')
        this.loadOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async confirmRental(id) {
      try {
        await confirmRental(id)
        this.$message.success('租赁确认成功')
        this.loadOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    openReturnDialog(order) {
      this.returnForm.orderId = order.id
      this.returnDialogVisible = true
    },
    async submitReturn() {
      try {
        await returnProduct(this.returnForm)
        this.$message.success('归还成功')
        this.returnDialogVisible = false
        this.loadOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    openEarlyReturnDialog(order) {
      this.selectedOrder = order
      this.earlyReturnDialogVisible = true
    },
    async submitEarlyReturn() {
      this.earlyReturnLoading = true
      try {
        const response = await apiEarlyReturn(this.selectedOrder.id)
        this.$message.success('提前归还成功，已按规则退还部分租金')
        this.earlyReturnDialogVisible = false
        this.loadOrders()
        
        if (response.data) {
          this.$notify({
            title: '退款成功',
            message: `退款金额：¥${response.data.refundAmount}`,
            type: 'success',
            duration: 5000
          })
        }
      } catch (error) {
        this.$message.error('操作失败')
      } finally {
        this.earlyReturnLoading = false
      }
    },
    viewOrderDetail(order) {
      this.detailOrder = order
      this.detailDialogVisible = true
    }
  }
}
</script>

<style scoped>
.order-list {
  padding: 20px;
}
</style>
