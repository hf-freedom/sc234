<template>
  <div id="app">
    <el-container style="height: 100vh; border: 1px solid #eee;">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246);">
        <el-menu router default-active="1" class="el-menu-vertical-demo">
          <el-menu-item index="1" route="/">
            <i class="el-icon-s-home"></i>
            <span slot="title">商品列表</span>
          </el-menu-item>
          <el-menu-item index="2" route="/orders">
            <i class="el-icon-document"></i>
            <span slot="title">订单管理</span>
          </el-menu-item>
          <el-menu-item index="3" route="/statistics">
            <i class="el-icon-data-line"></i>
            <span slot="title">数据统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="text-align: right; font-size: 12px;">
          <div style="text-align: left; font-size: 20px; font-weight: bold; line-height: 60px;">
            租赁订单押金系统
          </div>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
.el-header {
  background-color: #409EFF;
  color: #333;
  line-height: 60px;
}
.el-aside {
  color: #333;
}
</style>
