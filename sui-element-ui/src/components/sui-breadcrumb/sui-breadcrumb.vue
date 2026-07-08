<template>
  <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb">
    <el-breadcrumb-item v-if="$route.path!='/front/home'" to="/front/home">返回首页</el-breadcrumb-item>
    <el-breadcrumb-item>{{$route.meta.title}}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
  export default {
    name: "sui-breadcrumb",
  }
</script>

<style scoped>
  .breadcrumb {
    position: fixed;
    width: 1200px;
    left: 50%;
    transform: translate(-50%);
    z-index: 100;
    top: 60px;
    height: 40px;
    line-height: 40px;
    padding-left: 10px;
    margin-left: -5px;
    background: #FFFFFF;
  }
</style>
