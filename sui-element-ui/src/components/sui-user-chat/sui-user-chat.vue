<template>
  <div style="margin: 30px auto;width: 800px;border: solid 1px #ccc">
    <div style="height: 50px;background: url('/static/images/front/chat/banner.jpg') no-repeat;padding: 5px 10px">
      <el-avatar v-if="hasLogin" class="float-l" style="width: 40px;height: 40px;border-radius: 50%;vertical-align: middle" :src="formatUrl($store.getters.userInfo.photo)"></el-avatar>
      <span  v-else style="display: block; padding-left: 10px;padding-top: 10px;color: white">请先登录</span>
      <span class="float-l" style="display: block;font-size: larger;padding-top: 10px;padding-left: 10px">{{$store.getters.userInfo.userName}}</span>
    </div>
    <div style="height: 600px;width:798px;background: rgba(28,132,198,0.19)">
      <!--左侧聊天-->
      <div class="float-l" style="width: 200px;height:100%;overflow-y:auto;background: #FAFAFA">
        <div :title="user.userName" v-for="(user,index) in userList" :key="user.id" @click="selectUser(user,index)" :class="{ 'selected': selectedIndex == index }" class="chatUser pointer" style="padding: 10px">
          <el-badge v-if="user.online" is-dot>
            <el-avatar style="width: 50px;height: 50px;border-radius: 50%;vertical-align: middle" :src="formatUrl(user.photo)"></el-avatar>
          </el-badge>
          <el-avatar v-else style="width: 50px;height: 50px;border-radius: 50%;vertical-align: middle" :src="formatUrl(user.photo)"></el-avatar>
          <span style="font-size: larger;">{{user.userName}}</span>
          <div class="clearfix"></div>
        </div>
      </div>
      <!--右侧聊天-->
      <div class="float-l" style="width: 598px;height:100%;background: #F5F5F5">
        <div style="background: #F5F5F5">
          <div style="height:50px;padding:15px;font-size: larger;border-bottom: solid 1px #ccc">
            <span v-if="chatUser.userName">{{chatUser.userName}}
              <span style="font-size: small;color: grey" v-if="chatUser.online">[在线]</span>
              <span style="font-size: small;color: grey" v-else>[离线]</span>
            </span>
            <span v-else style="font-family: cursive">...在线聊天</span>
          </div>
          <div v-if="chatUser.id">
            <!--中间区域-->
            <div  style="height:350px;overflow-y: auto" ref="msg-box" class="chat-content" >
              <!-- chatList 聊天记录数组-->
              <div v-for="(item,index) in chatList" :key="index">
                <!-- 对方 -->
                <div v-if="!item.isMine" class="word">
                  <img :src="formatUrl(chatUser.photo)">
                  <div class="info">
                    <p class="time">{{item.userName}}  {{item.createDate}}</p>
                    <div class="info-content" v-html="item.contactText"></div>
                  </div>
                </div>
                <!-- 我的 -->
                <div v-else class="word-my">
                  <div class="info">
                    <p class="time">{{item.userName}} {{item.createDate}}</p>
                    <div class="info-content" v-html="item.contactText"></div>
                  </div>
                  <img :src="formatUrl(item.photo)">
                </div>
              </div>
            </div>
            <div style="height:200px">
              <div contenteditable="true" id="sendMsg" ref="sendMsg" class="message-content" v-html="msgText"></div>
              <div style="border-top: solid 1px #F5F5F5;padding: 10px;float: left">
                <sui-face @selectFace="selectFace" class="pointer" :show="false"></sui-face>
              </div>
              <el-button type="primary" size="mini" style="float:right;position: relative;right: 10px;top: 10px" @click="sendText">发送</el-button>
            </div>
          </div>
          <el-empty style="margin-top: 80px" v-else description="选择左侧用户进行聊天"></el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import md5 from "js-md5";
  export default {
    data() {
      return {
        websock: null,
        interval: null,
        selectedIndex:-1,
        timer:"",
        chatUser:{},
        userList:[],
        msgText:"",
        chatList:[],
      };
    },
    mounted() {
      this.getUserList();//加载聊天用户
      this.getChatList();//加载聊天历史
    },
    computed:{
      hasLogin(){
        return this.$store.getters.hasLogin;
      },
      refId() {
        let array = new Array();
        array.push(this.$store.getters.userInfo.id);
        array.push(this.chatUser.id);
        array.sort();
        let md5Str = array.join("-");
        return md5(md5Str);
      },
    },
    created() {
      //初始化webSocket
      this.initWebSocket();
      const that = this;
      this.interval = setInterval(function() {//10s查询一次在线用户
        that.getUserList();
      }, 10000);
    },
    destroyed() {
      this.websock.close(); //离开路由之后断开websocket连接
      clearInterval(this.interval);
    },
    methods: {
      //选择表情
      selectFace(face) {
        const that = this;
        let innerHTML = document.getElementById("sendMsg").innerHTML;
        that.msgText = innerHTML + face
      },
      //选择用户
      selectUser(user,index){
        this.selectedIndex = index;
        if(!this.$store.getters.hasLogin){
          this.showWarn("请先进行登录");
          return
        }
        this.chatUser = user;
        this.getChatList();
      },
      //获取聊天用户
      getUserList() {
        const that = this;
        that.$request.doPost('/api/socket/getChatUserList',{userType:"admin",roleCode:""}).then(res => {
          if (res.isOk) {
            that.userList = res.list.filter(function (v) {
              return that.$store.getters.userInfo.id != v.id
            })
          };
        });
      },
      //获取聊天记录
      getChatList(){
        const that = this;
        that.$request.doPost('/api/socket/getChatList',{refId:that.refId}).then(res => {
          if (res.isOk) {
            that.chatList = [];
            if(res.list.length>0){
              that.chatList = res.list.map(function (v) {
                return {isMine:that.$store.getters.userInfo.id==v.userId,contentText:v.content,userName:v.userName,photo:that.$store.getters.userInfo.photo,createDate:v.createDate,contactText:v.content}
              });
              setTimeout(() => {
                that.scrollBottm();
              }, 500);
            }
          };
        });
      },
      //发送聊天信息
      sendText() {
        let that = this;
        that.$refs["sendMsg"].focus();
        let innerHTML = document.getElementById("sendMsg").innerHTML;
        that.msgText = innerHTML;
        if (!that.msgText) {
          return;
        }
        that.$request.doPost('/api/socket/subChat',{refId:that.refId,content:that.msgText,userId:that.$store.getters.userInfo.id}).then(res => {
          if (res.isOk) {
            that.sendBox(that.msgText);
            that.msgText="";
            setTimeout(() => {
              that.scrollBottm();
            }, 500);
          };
        });
      },
      //消息发送
      sendBox(msgText){
        const that = this;
        const message = {
          userId: that.chatUser.id,
          message: msgText
        };
        that.getChatList();//我方加载历史数据
        that.websocketSend(JSON.stringify(message));
        console.log("消息已发送",message);
      },
      //滚动条到底部
      scrollBottm() {
        let el = this.$refs["msg-box"];
        el.scrollTop = el.scrollHeight;
      },
      //-------------------------------------------初始化weosocket------------------------------------------------------
      initWebSocket(){//初始化weosocket
        try {
          let userId = this.$store.getters.userInfo.id;
          let host = this.$store.getters.config.projectDomain.replace("http","ws");
          const wsUri = host+"/websocket/" + userId;
          this.websock = new WebSocket(wsUri);
          this.websock.onmessage = this.websocketOnmessage;
          this.websock.onopen = this.websocketOnopen;
          this.websock.onerror = this.websocketOnerror;
          this.websock.onclose = this.websocketClose;
        } catch (e) {
          console.error("创建异常",e);
        }
      },
      websocketOnopen() {//连接建立之后执行send方法发送数据
        console.log("聊天创建成功");
        console.log("【userId:】",this.$store.getters.userInfo.id,'【userName:】',this.$store.getters.userInfo.userName);
      },
      websocketOnerror(){//连接建立失败重连
        this.initWebSocket();
      },
      websocketOnmessage(e){ //数据接收
        console.log("接收到的信息：",e.data);
        this.getChatList();//对方加载历史数据
        this.getUserList();//对方获取在线用户
      },
      websocketSend(Data){//数据发送
        console.log("数据发送：",Data);
        this.websock.send(Data);
      },
      websocketClose(e){//关闭
        console.log('断开连接：',e);
      },
    }
  };
</script>

<style lang="scss" scoped>
  .chat-dialog ::v-deep .el-dialog--center .el-dialog__body{
    padding: 0 0 50px!important;
  }
  .selected{
    background: #EBEBEC;
  }
  .message-content{
    padding: 10px;
    background: white;
    width: 100%;
    height: 100px;
    font-family: cursive;
    color: black;
    font-weight: bold;
    border: solid 1px #cccccc;
    border-radius: 10px
  }
  .chat-content{
    width: 100%;
    padding: 20px;
    .word{
      display: flex;
      margin-bottom: 20px;
      img{
        width: 40px;
        height: 40px;
        border-radius: 50%;
      }
      .info{
        margin-left: 10px;
        .time{
          font-size: 12px;
          color: rgba(51,51,51,0.8);
          margin: 0;
          height: 20px;
          line-height: 20px;
          margin-top: -5px;
        }
        .info-content{
          padding: 10px;
          font-size: 14px;
          background: #fff;
          position: relative;
          margin-top: 8px;
        }
        //小三角形
        .info-content::before{
          position: absolute;
          left: -8px;
          top: 8px;
          content: '';
          border-right: 10px solid #FFF;
          border-top: 8px solid transparent;
          border-bottom: 8px solid transparent;
        }
      }
    }
    .word-my{
      display: flex;
      justify-content:flex-end;
      margin-bottom: 20px;
      img{
        width: 40px;
        height: 40px;
        border-radius: 50%;
      }
      .info{
        width: 90%;
        margin-left: 10px;
        text-align: right;
        .time{
          font-size: 12px;
          color: rgba(51,51,51,0.8);
          margin: 0;
          height: 20px;
          line-height: 20px;
          margin-top: -5px;
          margin-right: 10px;
        }
        .info-content{
          max-width: 70%;
          padding: 10px;
          font-size: 14px;
          float: right;
          margin-right: 10px;
          position: relative;
          margin-top: 8px;
          background: #A3C3F6;
          text-align: left;
        }
        //小三角形
        .info-content::after{
          position: absolute;
          right: -8px;
          top: 8px;
          content: '';
          border-left: 10px solid #A3C3F6;
          border-top: 8px solid transparent;
          border-bottom: 8px solid transparent;
        }
      }
    }
  }
</style>
