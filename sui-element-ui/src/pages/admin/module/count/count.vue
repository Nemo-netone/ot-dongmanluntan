<!-- 【统计信息】管理页面 -->
<template>
  <div class="body">
    <el-row style="margin-top: 100px">
      <el-col span="12">
        <echarts-line id="echartsLine" title="动漫发布数量统计" ref="echartsLine"></echarts-line>
      </el-col>
      <el-col span="12">
        <echarts-pie id="echartsPie" title="动漫发布比例统计" ref="echartsPie"></echarts-pie>
      </el-col>
    </el-row>
  </div>
</template>

<script>
    export default {
      mounted() {
        //初始化数据
        this.getList();
      },
      computed: {
      },
      methods: {
        //点击搜索
        handleQuery() {
          this.queryForm.current = 1;
          this.getList();
        },
        //查询列表数据
        getList() {
          this.$request.doGet('/admin/count/getCount', this.queryForm).then(res => {
            if (res.isOk) {
              this.loading = false;
              //柱状图
              const line = res.data.line;
              this.$refs.echartsLine.init(line);
              //饼状图
              const pie = res.data.pie.data;
              this.$refs.echartsPie.init(pie);
            }
          });
        },
      }
    };
</script>
