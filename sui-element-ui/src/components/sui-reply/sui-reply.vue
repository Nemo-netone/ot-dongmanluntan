<template>
  <div style="margin-top: 20px">
    <div class="reply" v-for="reply in childrenList" :key="reply.id">
      <el-image class="reply-header pointer" :src="formatUrl(reply.photo)"/>
      <span>{{reply.loginName}} :<i style="color: cornflowerblue">@{{item.loginName}} </i></span><span style="font-family: cursive" v-html="reply.content"></span>
      <div>
        <span class="msg-time"><i class="el-icon-time"></i> {{reply.createDate}}</span>
        <a v-if="userInfo && (reply.userId != userInfo.id)" @click="replyUser(reply)" style="color: blue" class="reply-del" title="回复">回复</a>
        <div style="clear: both"></div>
        <a v-if="userInfo && (reply.userId == userInfo.id)" @click="del(reply.id)" class="reply-del" title="删除">删除</a>
        <div style="clear: both;border-bottom:dashed 1px grey"></div>
      </div>
      <sui-reply v-if="reply.children" :childrenList="reply.children" :item="reply" @replyUser="replyUser" @del="del"></sui-reply>
    </div>
  </div>
</template>

<script>
  export default {
    props:{
      childrenList: {
        type: Array,
        default: []
      },
      item: {
        type: Object,
        default: {}
      },
    },
    name: "sui-reply",
    data(){
      return{

      }
    },
    computed: {
      userInfo() {
        return this.$store.getters.userInfo
      },
      hasLogin() {
        return this.$store.getters.hasLogin;
      }
    },
    methods:{
      replyUser(reply){
        this.$emit('replyUser',reply);
      },
      del(id){
        this.$emit('del',id);
      }
    }
  }
</script>

<style scoped>
  .reply-header {
    vertical-align: middle;
    width: 30px;
    height: 30px;
    border-radius: 50%
  }
  .msg-time {
    color: grey;
    font-size: small
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
