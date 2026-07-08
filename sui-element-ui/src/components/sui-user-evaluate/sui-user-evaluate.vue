<!-- 【评论|建议】列表 -->
<template>
  <div style="padding:10px 0">
    <label v-if="type=='评价'">选择喜欢程度</label>
    <el-rate v-if="type=='评价'" style="margin: 5px 0" :texts="texts" v-model="stars" show-text></el-rate>
    <el-input type="textarea" :rows="8" v-model="message.content" placeholder="请输入评论内容"/>
    <el-button @click="sub" size="mini" class="evaluate"> {{type}} </el-button>
    <el-tabs v-model="activeName" type="card" style="margin-top: 5px;">
      <el-tab-pane :label="'全部('+dataList.length+')'" name="全部">
        <div v-if="dataList.length>0">
           <span v-for="item in dataList" :key="item.id">
            <el-card class="box-card div-shadow">
              <el-rate style="margin: 5px 0" v-if="type=='评价'" disabled :texts="texts" v-model="item.stars" show-text></el-rate>
              <el-button v-if="userInfo && (item.userId != userInfo.id)" @click="reply(item)" class="float-r" type="primary" size="mini" icon="el-icon-edit" circle title="回复"></el-button>
              <el-button v-if="userInfo && (item.userId==userId)" @click="delClick(item.id)" class="float-r" size="mini" icon="el-icon-delete" style="color: #F56C6C;margin-right:5px" circle title="删除"></el-button>
              <el-avatar style="vertical-align: middle" :src="formatUrl(item.photo)"></el-avatar>
                <span style="color: grey;">{{item.loginName}}</span>
                <span style="color: grey;font-size: small"><i class="el-icon-time"></i>{{item.createDate}}</span>
              <div style="font-family: cursive;padding: 5px">{{item.content}}</div>
              <div v-if="item.children && item.children.length>0">
                <sui-reply :childrenList="item.children" :item="item" @replyUser="reply" @del="delClick"></sui-reply>
              </div>
            </el-card>
          </span>
        </div>
        <el-empty v-else description="暂无评论数据"></el-empty>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
  export default {
    name:"user-evaluate",
    props: {
      refId: {
        type: String,
        default: ''
      },
      type: {
        type: String,
        default: ''
      },
    },
    data(){
      return{
        stars: 5,
        texts:['1星', '2星','3星', '4星', '5星'],
        dataList:[],
        activeName: '全部',
        icon:"el-icon-star-on",
        flag:false,
        total:0,
        message: {
          type:"",
          pid: "",
          stars: "",
          userId: "",
          refId: "",
          content: ""
        },
        queryForm: {
          current: 1,
          size: 50,
          type:"",
          refId: "",
        }
      }
    },
    computed:{
      hasLogin() {
        return this.$store.getters.hasLogin;
      },
      userInfo() {
        return this.$store.getters.userInfo
      },
      userId(){
        return this.$store.getters.userInfo==null?"":this.$store.getters.userInfo.id;
      },
    },
    watch: {
      'refId'(id) {
        if(id){
          this.getList();
        }
      }
    },
    mounted() {
      this.getList()
    },
    methods: {
      //回复
      reply(item) {
        const that = this;
        if (!that.hasLogin) {
          that.showWarn("请先进行登录");
          return
        }
        if (item.userId == that.userInfo.id) {
          that.showWarn("自己不能回复自己哦");
          return
        }
        this.$prompt('请输入回复内容', '温馨提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }).then(({value}) => {
          if(value==null){
            that.showWarn("请输入内容");
            return;
          }
          that.message.type = "回复";
          that.message.pid = item.id;
          that.message.content = value;
          that.$request.doPost('/api/message/sub', that.message).then(res => {
            if (res.isOk) {
              that.message.content ="";
              that.showSuccess("回复成功");
              that.getList();
            }
          });
        })
      },
      delClick(id) {
        const that = this;
        that.showConfirm("确认删除吗?", function () {
          that.$request.doPost('/api/message/delete?id='+id).then(res => {
            if (res.isOk) {
              that.showSuccess("删除成功");
              that.getList();
            };
          });
        })
      },
      //提交评论
      sub() {
        const that = this;
        if (!that.hasLogin) {
          that.showWarn("请先进行登录");
          return
        }
        if(that.message.content.trim()==''){
          that.showWarn("请填写内容");
          return
        }
        that.message.type = that.type;
        that.message.refId = that.refId;
        if (that.type == '评价') {
          that.message.stars = that.stars;
        }
        that.$request.doPost('/api/message/sub',that.message).then(res => {
          if (res.isOk) {
            that.message.content="";
            that.showSuccess("评论成功");
            that.getList();
          }
        });
      },
      //查询列表数据
      getList() {
        const that = this;
        if(that.type==''){
          that.showWarn("请设置消息类型");
          return
        }
        that.queryForm.type = this.type;
        that.queryForm.refId = this.refId;
        that.$request.doGet('/api/message/getTreePage',that.queryForm).then(res => {
          if (res.isOk) {
            this.dataList = res.obj.records;
            this.total = res.obj.total;
          }
        })
      },
    }
  }
</script>
<style scoped lang="scss">
  @import '../../assets/styles/theme';
  .evaluate{
    text-align: right;
    position: relative;
    top: 8px;
    z-index: 1000;
    float: right;
    color: white;
    background: $theme-front-color;
  }
  .reply-del {
    color: orange;
    float: right;
    font-size: small;
    margin-left: 5px
  }
  .reply-del:hover {
    color: red;
  }
</style>
