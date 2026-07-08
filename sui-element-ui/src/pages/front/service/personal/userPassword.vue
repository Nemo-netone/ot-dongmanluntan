<!-- 【修改密码】弹窗页面 -->
<template>
  <el-dialog width="420px" class="front-dialog" center :title="dialog.title" :close-on-click-modal="false" :append-to-body="true" v-dialog-drag :visible.sync="dialog.open">
    <el-form :model="register" ref="passwordForm" :rules="rules" label-width="100px">
      <el-form-item label="登录账号" prop="loginName">
        <el-input v-model="register.loginName" disabled placeholder="请输入登录账号" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="原始密码" prop="oldPassword">
        <el-input v-model="register.oldPassword" type="password" placeholder="请输入原始密码" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="新的密码" prop="newPassword">
        <el-input v-model="register.newPassword" type="password" placeholder="请输入新的密码" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="prePassword">
        <el-input v-model="register.prePassword" type="password" placeholder="请输入确认密码" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button class="sub-btn" @click="submitForm()">确认修改</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {mapActions} from "vuex";

  export default {
    name: "userPassword",
    data() {
      return {
        //弹窗设置
        dialog: {
          title: "",
          open: false,
          disabled: false
        },
        //表单数据
        register: {
          loginName:"",
          oldPassword: "",
          newPassword: "",
          prePassword: ""
        },
        //表单校验
        rules: {
          loginName: [{required: true, message: '请输入登录账号', trigger: 'blur'}],
          oldPassword: [{required: true, message: '请输入原始密码', trigger: 'blur'}],
          newPassword: [{required: true, message: '请输入新的密码', trigger: 'blur'},{validator: this.$validator.password, message: '密码长度不能小于6位数', trigger: 'blur'}],
          prePassword: [{required: true, message: '请输入确认密码', trigger: 'blur'},{validator: this.$validator.password, message: '密码长度不能小于6位数', trigger: 'blur'}]
        }
      }
    },
    methods: {
      ...mapActions(['commitFrontLoginOut']),
     //打开弹窗
     open(option) {
       const pageFrom = option.pageFrom;
       const disabled = pageFrom == 'view' ? true : false;
       this.showDialog(option.title, disabled);
       this.getLoginInfo();
     },
      //取消弹窗
      cancel() {
        this.dialog.open = false
      },
      //获取当前用户信息
      getLoginInfo() {
        this.$request.doGet("/api/register/getLoginInfo").then(res => {
          if (res.isOk) {
            this.register = res.data.userInfo;
          }
        });
      },
      //提交表单
      submitForm() {
        const that = this;
        that.$refs.passwordForm.validate((valid) => {
          if (valid) {//表达校验
            if(this.register.newPassword!=this.register.prePassword){
              this.showWarn("两次密码不一致");
              return
            }
            that.$request.doPost('/api/register/modifyPsd', that.register).then(res => {
              if (res.isOk) {
                that.dialog.open = false;
                that.showConfirm('您的密码已修改成功，是否进行重新登录?',function (){
                  that.commitFrontLoginOut().then(res=>{
                    if(res.isOk){
                      that.showToast("退出成功");
                      that.$router.push("/front/home");
                    }
                  })
                })
              }
            });
          }
        });
      },
      //显示弹窗
      showDialog(title, disabled) {
        this.resetForm(this.$refs.passwordForm);//重置表单
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
</style>
