<!-- 【文章信息】管理页面 -->
<template>
  <div class="front-body">
    <el-card class="front-card">
      <!-- 表单弹窗 -->
      <article-form ref="articleDialog" @reloadList="getList"/>
      <!-- 查询条件 -->
      <el-form :model="queryForm" ref="queryForm" :inline="true" label-width="90px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="queryForm.title" placeholder="请输入文章标题" size="small" clearable/>
        </el-form-item>
        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="queryForm.categoryId" placeholder="请选择文章分类" size="small" filterable clearable>
            <el-option v-for="category in categoryDict" :key="category.id" :value="category.id" :label="category.name"/>
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatusRange">
          <el-select v-model="queryForm.auditStatusRange" placeholder="请选择审核状态" size="small" multiple collapse-tags filterable clearable>
            <el-option v-for="dict in auditStatusDict" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery()">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="openDialog({pageFrom:'add',title:'发布文章信息'})">发布文章</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="el-icon-delete" size="mini"  @click="handleDeleteAll">批量删除</el-button>
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <el-table border :header-cell-style="{background:'#f4f4f5'}" :height="600" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" align="center" width="50"/>
        <el-table-column label="id" prop="id" v-if="false"/>
        <el-table-column label="序号" type="index" width="50"/>
        <el-table-column label="文章图片" prop="picture" sortable>
          <template slot-scope="scope">
            <span-photo :value="scope.row.picture"></span-photo>
          </template>
        </el-table-column>
        <el-table-column label="文章标题" prop="title" show-overflow-tooltip sortable/>
        <el-table-column label="发布时间" prop="publishTime" sortable/>
        <el-table-column label="文章分类" prop="categoryId" sortable>
          <template slot-scope="scope">
            <span v-for="category in categoryDict" v-if="category.id==scope.row.categoryId">{{category.name}}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" prop="auditStatus" sortable>
          <template slot-scope="scope">
            <span :style="dict.style" v-for="dict in auditStatusDict" v-if="dict.dictValue==scope.row.auditStatus">{{dict.dictLabel}}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核结果" prop="auditResult" show-overflow-tooltip sortable/>
        <el-table-column label="操作" align="center" width="300">
          <template slot-scope="scope">
            <el-button size="mini" type="info" plain icon="el-icon-view" @click="openDialog({title:'查看文章信息',pageFrom:'view',id:scope.row.id})">查看</el-button>
            <el-button size="mini" type="primary" plain icon="el-icon-edit" @click="openDialog({title:'修改文章信息',pageFrom:'edit',id:scope.row.id})">修改</el-button>
            <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 分页区域 -->
    <div class="pagination" style="text-align: center">
      <el-pagination
        @current-change="handleCurrentChange"
        :page-size="queryForm.size"
        layout="total, prev, pager, next"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
  import articleForm from './articleDetails'
  export default {
    components: {articleForm},
    data() {
      return {
        loading: true,//是否加载完成
        selectedData: [],//勾选数据
        total: 0, //记录数
        registerDict: [],
        categoryDict: [],
        dataList: [],
        queryForm: {
          current: 1,
          size: 10,
          picture: "",
          title: "",
          userId: "",
          publishTimeRange: [],
          publishTime: "",
          categoryId: "",
          content: "",
          auditStatusRange: [],
          auditStatus: "",
          auditResult: "",
        }
      };
    },
    mounted() {
      //初始化数据
      this.getList();
      //初始化【文章作者】
      this.getRegisterDict();
      //初始化【文章分类】
      this.getCategoryDict();
    },
    computed: {
      //【审核状态】
      auditStatusDict() {
        return this.$store.getters.getDictArray('audit_status');
      },
    },
    methods: {
      //点击搜索
      handleQuery() {
        this.queryForm.current = 1;
        this.getList();
      },
      //查询列表数据
      getList() {
        this.$request.doGet('/api/article/getUserPage', this.queryForm).then(res => {
          if (res.isOk) {
            this.loading = false;
            this.dataList = res.obj.records;
            this.total = res.obj.total;
          }
        });
      },
      //【文章作者】
      getRegisterDict() {
        this.$request.doGet('/api/register/getList').then(res => {
          if (res.isOk) {
            this.registerDict = res.list;
          }
        });
      },
      //【文章分类】
      getCategoryDict() {
        this.$request.doGet('/api/category/getList').then(res => {
          if (res.isOk) {
            this.categoryDict = res.list;
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
        this.$refs.articleDialog.open(option);
      },
      //选择页数
      handleSizeChange: function (size) {
        this.queryForm.size = size;
        this.getList();
      },
      //选择页码
      handleCurrentChange: function (currentPage) {
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
            that.$request.doGet('/api/article/delAll', {ids: ids}).then(res => {
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
          that.$request.doGet('/api/article/delete?id=' + id).then(res => {
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
