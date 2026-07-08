<!-- 【用户登录】弹窗页面 -->
<template>
  <el-dialog width="420px" class="front-dialog" center :title="dialog.title" v-dialog-drag :close-on-click-modal="false" :append-to-body="true" :visible.sync="dialog.open">
    <el-form :model="register" ref="registerForm" @keyup.native.enter="login">
      <div class="front-input">
        <span class="fa fa-user-o" aria-hidden="true"></span>
        <el-input v-model="register.loginName" placeholder="请输入登录账号"></el-input>
      </div>
      <div class="front-input">
        <span class="fa fa-key" aria-hidden="true"></span>
        <el-input v-model="register.password" type="password" placeholder="请输入密码"></el-input>
      </div>
    </el-form>
    <validCode style="z-index: 10000;top: -174px" :show="isValid" @success="validSuccess"/>
    <div class="dialog-footer">
      <el-button class="sub-btn" @click="login">登 录</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {mapActions} from "vuex";
  import validCode from "vue-puzzle-vcode";
  export default {
    components:{validCode},
    name: "userLogin",
    data() {
      return {
        isValid: false,//验证标识
        roleList: [],
        //弹窗设置
        dialog: {
          title: "",
          open: false,
          disabled: false
        },
        //表单数据
        register: {
          loginName: "",
          password: ""
        },
      }
    },
    computed:{
      showValidCode(){
        return this.$store.getters.config.showValidCode
      }
    },
    methods: {
      ...mapActions(['commitFrontLoginInfo','commitToken','commitFrontLogin']),
      validSuccess(){
        this.isValid = false;//通过验证后，进行登录
        this.subLogin();
      },
      login() {
        this.showValidCode ? this.isValid = true : this.subLogin();
      },
      subLogin() {
        const loading = this.$loading({
          lock: true,
          text: '请稍后,登录中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0,0,0,0.25)'
        });
        const that = this;
        that.commitFrontLogin(this.register).then(res => {
          if (res.isOk) {//用code获取用户token
            that.commitToken({from: 3, code: res.data.code}).then(res => {
              that.dialog.open = false;
              that.$router.push("/front/home");
              that.showSuccess("登录成功");
            })
          }
        });
        loading.close();
      },
      //打开弹窗
      open(option) {
        this.showDialog(option.title, false);
      },
      //取消弹窗
      cancel() {
        this.dialog.open = false
      },
      //显示弹窗
      showDialog(title, disabled) {
        this.dialog.open = true;//打开弹窗
        this.dialog.title = title;//设置标题
        this.dialog.disabled = disabled;//是否可编辑
      },
    }
  }
</script>

<style scoped lang="scss">
  @import '../../../../assets/styles/theme';
  .front-dialog ::v-deep .el-dialog__headerbtn .el-dialog__close{
    color: white!important;
  }
  .front-dialog ::v-deep .el-dialog__header  {
    background: $theme-front-color;
  }
  .front-dialog ::v-deep .el-dialog__title  {
    font-weight: bold;
    color: white!important;
  }
  .sub-btn  {
    margin: 20px 0 10px;
    width: 100%;
    background: $theme-front-color;
    color: white;
    height: 45px
  }
  .front-input {
    margin: 20px 0;
    padding: 3px 15px;
    border: 1px solid #eee;
    background:white;
    border-radius: 5px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
  }
  .front-input span   {
    font-size: 15px;
    color: #464646;
    margin-right: 10px;
  }
</style>
