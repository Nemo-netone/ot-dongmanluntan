<!-- 【积分信息】管理页面 -->
<template>
  <div class="front-body">
    <el-card class="front-card">
      <el-card style="padding-bottom:10px">
        <div class="body">
          <!-- 表单弹窗 -->
          <integral-form ref="integralDialog" @reloadList="getList"/>
          <!-- 查询条件 -->
          <el-form :model="queryForm" ref="queryForm" :inline="true" label-width="90px">
            <el-form-item label="用户姓名" prop="registerId">
              <el-select v-model="queryForm.registerId" placeholder="请选择用户姓名" size="small" filterable clearable>
                <el-option v-for="register in registerDict" :key="register.id" :value="register.id" :label="register.userName"/>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery()">重置</el-button>
            </el-form-item>
          </el-form>
          <!-- 表格区域 -->
          <el-table border :header-cell-style="{background:'#f4f4f5'}" :height="620" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
            <el-table-column label="id" prop="id" v-if="false"/>
            <el-table-column label="积分排名" prop="name" sortable>
              <template slot-scope="scope">
                <el-tag type="warning">第{{scope.$index+1}}名</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="用户姓名" prop="registerId" sortable>
              <template slot-scope="scope">
                <el-tag type="success"><associate-label :dict="registerDict" :value="scope.row.registerId" label="userName"></associate-label></el-tag>
              </template>
            </el-table-column>
            <el-table-column label="积分分数" prop="integralNum" sortable>
              <template slot-scope="scope">
                <el-tag type="warning">{{scope.row.integralNum}}分</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="积分总分" prop="remarks" show-overflow-tooltip sortable>
              <template slot-scope="scope">
                <el-tag type="info">{{scope.row.remarks}}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script>
    export default {
      data() {
        return {
          loading: true,//是否加载完成
          selectedData: [],//勾选数据
          total: 0, //记录数
          registerDict: [],
          dataList: [],
          queryForm: {
            current: 1,
            size: 10,
            id: "",
            name: "",
            registerId: "",
            integralTimeRange: [],
            integralTime: "",
            integralNum: "",
            remarks: "",
          }
        };
      },
      mounted() {
        //初始化数据
        this.getList();
        //初始化【用户姓名】
        this.getRegisterDict();
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
          this.$request.doGet('/api/integral/getRankingList', this.queryForm).then(res => {
            if (res.isOk) {
              this.loading = false;
              this.dataList = res.list;
              this.total = this.dataList.length;
            }
          });
        },
        //【用户姓名】
        getRegisterDict() {
          this.$request.doGet('/api/register/getList').then(res => {
            if (res.isOk) {
              this.registerDict = res.list;
            }
          });
        },
        //重置查询
        resetQuery() {
          this.$refs['queryForm'].resetFields();
          this.handleQuery();
        },
        //打开弹窗
        openDialog(option) {
          this.$refs.integralDialog.open(option);
        },
        //选择页数
        handleSizeChange(size) {
          this.queryForm.size = size;
          this.getList();
        },
        //选择页码
        handleCurrentChange(currentPage) {
          this.queryForm.current = currentPage;
          this.getList();
        },
        //获取勾选数据
        handleSelectionChange(data) {
          this.selectedData = data;
        },
        //批量删除
        handleDeleteAll() {
          const that = this;
          that.showConfirm('确定删除选中的数据吗?', function () {
            if (that.selectedData.length > 0) {
              let ids = that.selectedData.map(function (v) {return v.id});
              that.$request.doGet('/api/integral/delAll', {ids: ids}).then(res => {
                if (res.isOk) {
                  that.showSuccess("删除成功");
                  that.getList();
                }
              });
            } else {
              that.showWarn("至少选择一条数据！");
            }
          });
        },
        //选中行删除
        handleDelete(id) {
          const that = this;
          that.showConfirm('确定删除选中的数据吗?', function () {
            that.$request.doGet('/api/integral/delete?id=' + id).then(res => {
              if (res.isOk) {
                that.showSuccess("删除成功");
                that.getList();
              }
            });
          });
        }
      }
    };
</script>
