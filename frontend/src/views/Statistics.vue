<template>
  <div class="statistics">
    <h2>数据统计</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-s-order" style="font-size: 40px; color: #409EFF;"></i>
            <div class="stat-content">
              <div class="stat-label">总订单数</div>
              <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-goods" style="font-size: 40px; color: #67C23A;"></i>
            <div class="stat-content">
              <div class="stat-label">商品总数</div>
              <div class="stat-value">{{ statistics.totalProducts || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-shopping-cart-2" style="font-size: 40px; color: #E6A23C;"></i>
            <div class="stat-content">
              <div class="stat-label">租赁次数</div>
              <div class="stat-value">{{ statistics.rentalCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-time" style="font-size: 40px; color: #F56C6C;"></i>
            <div class="stat-content">
              <div class="stat-label">逾期率</div>
              <div class="stat-value">{{ (statistics.overdueRate || 0).toFixed(2) }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-delete" style="font-size: 40px; color: #909399;"></i>
            <div class="stat-content">
              <div class="stat-label">损坏率</div>
              <div class="stat-value">{{ (statistics.damageRate || 0).toFixed(2) }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-money" style="font-size: 40px; color: #F56C6C;"></i>
            <div class="stat-content">
              <div class="stat-label">损坏扣款总额</div>
              <div class="stat-value">¥{{ statistics.totalDamageAmount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <i class="el-icon-wallet" style="font-size: 40px; color: #409EFF;"></i>
            <div class="stat-content">
              <div class="stat-label">租金总收入</div>
              <div class="stat-value">¥{{ statistics.totalRentalAmount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div style="margin-top: 30px;">
      <el-button type="primary" @click="loadStatistics">刷新统计</el-button>
    </div>
  </div>
</template>

<script>
import { getStatistics } from '../api'

export default {
  name: 'Statistics',
  data() {
    return {
      statistics: {}
    }
  },
  mounted() {
    this.loadStatistics()
  },
  methods: {
    async loadStatistics() {
      try {
        const response = await getStatistics()
        this.statistics = response.data
      } catch (error) {
        this.$message.error('加载统计数据失败')
      }
    }
  }
}
</script>

<style scoped>
.statistics {
  padding: 20px;
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 20px;
}
.stat-content {
  flex: 1;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
