<!-- 【分类详情】页面 -->
<template>
  <div class="front-body">
    <el-card class="front-card">
      <div >
        <h1 class="font-shadow article-title">{{item.title}}</h1>
        <span class="article-time"><i class="el-icon-time"></i> {{item.createDate}}</span>
        <el-image v-if="item.picture" class="article-image" :src="formatUrl(item.picture)"></el-image>
        <p style="white-space: pre-wrap;" v-html="item.content"></p>
        <!--【收藏】按钮-->
        <sui-collect class="collect" v-if="item.id" :refId="item.id" :refContent="item.title" type="收藏"/>
        <!--【评论】组件-->
        <sui-user-evaluate v-if="item.id" :refId="item.id" type="评论"></sui-user-evaluate>
      </div>
    </el-card>
  </div>
</template>
<script>
  export default {
    name: "itemForm",
    data() {
      return {
        //表单数据
        item: {
          id: "",
          picture: "",
          title: "",
          name: "",
          categoryId: "",
          content: ""
        },
      }
    },
    mounted(){
      let params = this.$route.query;
      this.getItem(params.id);
      this.scrollTop();
    },
    watch: {
      '$route.query.id'(id) {
        if(id){
          this.getItem(id);
        }
      }
    },
    methods: {
      success(){
        this.$refs.userEvaluate.getList();
      },
      //根据id获取数据
      getItem(id) {
        this.$request.doGet("/api/article/get?id=" + id).then(res => {
          if (res.isOk) {
            this.item = res.obj;
          }
        });
      },
    }
  }
</script>
<style scoped>
  .article-image {
    margin-left: 0;
    border-radius: 10px;
    width: 100%;
    height: 650px;
  }

  .evaluate {
    text-align: right;
    position: relative;
    top: 30px;
    z-index: 1000
  }

  .collect {
    margin: 0 auto;
    display: block
  }
</style>
