<!-- 【用户注册】弹窗页面 -->
<template>
  <el-dialog width="420px" class="front-dialog" :title="dialog.title" :close-on-click-modal="false" center :append-to-body="true" v-dialog-drag :visible.sync="dialog.open">
    <el-form :model="register" ref="registerForm" :rules="rules" label-width="100px">
      <el-form-item label="登录账号" prop="loginName">
        <el-input v-model="register.loginName" placeholder="请输入登录账号" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="登录密码" prop="password">
        <el-input v-model="register.password" type="password" placeholder="请输入登录密码" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="prePassword">
        <el-input v-model="register.prePassword" type="password" placeholder="请输入确认密码" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="用户姓名" prop="userName">
        <el-input v-model="register.userName" placeholder="请输入用户姓名" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="用户性别" prop="sex">
        <el-select v-model="register.sex" placeholder="请选择用户性别" :disabled="dialog.disabled" class="sui-input">
          <el-option v-for="dict in sexDict" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"/>
        </el-select>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="register.phone" placeholder="请输入手机号" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="register.email" placeholder="请输入邮箱" :disabled="dialog.disabled" class="sui-input"></el-input>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button class="sub-btn" @click="submitRegister">提交注册</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {mapActions} from "vuex";
  import axios from "axios";
  import md5 from 'js-md5';
  export default {
    name: "userRegister",
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
          photo: "",
          userName: "",
          sex:"",
          loginName: "",
          prePassword:"",
          password: ""
        },
        //表单校验
        rules: {
          photo: [{required: true, message: '请上传用户头像', trigger: 'blur'}],
          loginName: [{required: true, message: '请输入登录账号', trigger: 'blur'},{validator: this.$validator.unique, form: this, tableName: 'register', column: 'login_name', model: 'register', message: '登录账号已存在', trigger: 'blur'}],
          password: [{required: true, message: '请输入登录密码', trigger: 'blur'},{validator: this.$validator.password, message: '密码长度不能小于6位数', trigger: 'blur'}],
          prePassword: [{required: true, message: '请输入确认密码', trigger: 'blur'},{validator: this.$validator.password, message: '密码长度不能小于6位数', trigger: 'blur'}],
          userName: [{required: true, message: '请输入用户姓名', trigger: 'blur'}],
          sex: [{required: true, message: '请选择用户性别', trigger: 'blur'}],
          phone: [{validator: this.$validator.phone, message: '请输入11位手机号', trigger: 'blur'},{validator: this.$validator.unique, form: this,model: 'register', tableName: 'register', column: 'phone',  message: '手机号已存在', trigger: 'blur'}],
          email: [{type: 'email', message: '请输入正确的邮箱', trigger:'blur'},{validator:this.$validator.unique,form:this,tableName:'register',column:'email',model:'register',message: '邮箱已存在',trigger: 'blur'}],
        }
      }
    },
    computed: {
      //【性别】
      sexDict() {
        return this.$store.getters.getDictArray('sys_sex');
      }
    },
    methods: {
      //打开弹窗
      open(option) {
        this.showDialog(option.title, false);
      },
      //取消弹窗
      cancel() {
        this.dialog.open = false
      },
      //提交注册
      submitRegister() {
        const that = this;
        that.$refs.registerForm.validate((valid) => {
          if (valid) {//表单校验
            if (that.register.password != that.register.prePassword) {
              this.showWarn("两次密码不一致!");
              return false
            }
            that.$request.doPost('/api/login/register', that.register).then(res => {
              if (res.isOk) {
                that.dialog.open = false;
                that.showSuccess("注册成功");
                that.$emit('reloadList');
              }
            });
          }
        });
      },
      //显示弹窗
      showDialog(title, disabled) {
        this.resetForm(this.$refs.registerForm);//重置表单
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
