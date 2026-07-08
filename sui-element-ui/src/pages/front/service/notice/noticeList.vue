<template>
  <div class="front-body">
    <el-card class="box-card">
      <div v-if="dataList.length>0">
        <sui-info v-for="item in dataList" :key="item.id"
                  @click.native="toPage({name:'noticeDetails',query:{id:item.id}})"
                  :title="item.title"
                  :imgsrc="item.picture"
                  :content="item.content"
                  :date="item.createDate">
        </sui-info>
      </div>
      <el-empty v-else class="sui-empty" description="暂无数据"></el-empty>
    </el-card>
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
  export default {
    name: "home-body",
    data() {
      return {
        total: 0, //记录数
        categoryDict: [],
        dataList: [],
        queryForm: {
          current: 1,
          size: 5,
          id: "",
          picture: "",
          title: "",
          name: "",
          categoryId: "",
          content: ""
        }
      };
    },
    mounted() {
      //初始化数据
      this.getList(this.queryForm);
      //初始化【所属分类】
      this.getCategoryDict();
    },
    computed: {
    },
    methods: {
      //点击搜索
      handleQuery() {
        this.queryForm.current = 1;
        this.getList(this.queryForm);
      },
      //查询列表数据
      getList(queryForm) {
        this.queryForm = queryForm
        this.$request.doGet('/api/notice/getPage', this.queryForm).then(res => {
          if (res.isOk) {
            this.loading = false;
            this.dataList = res.obj.records;
            this.total = res.obj.total;
          }
        });
      },
      //【所属分类】
      getCategoryDict() {
        this.$request.doGet('/api/notice/getList').then(res => {
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
      //选择页码
      handleCurrentChange: function (currentPage) {
        this.queryForm.current = currentPage;
        this.getList(this.queryForm);
      },
    }
  };
</script>
<style scoped>
  .box-card {
    min-height: 800px
  }
  .wheel{
    padding: 10px 0
  }
  .home-body {
    width: 1200px;
    margin: 0 auto;
  }
</style>



