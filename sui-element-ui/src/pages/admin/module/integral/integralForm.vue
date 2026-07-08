<!-- 【积分信息】弹窗页面 -->
<template>
  <div class="body">
    <el-dialog :title="dialog.title" :close-on-click-modal="false" :visible.sync="dialog.open">
      <el-form :model="integral" ref="integralForm" :rules="rules" label-width="120px" class="edit-form">
        <el-form-item label="主键id" class="hidden" prop="id">
          <el-input type="hidden" v-model="integral.id"/>
        </el-form-item>
        <el-form-item label="积分名称" prop="name">
          <el-input v-model="integral.name" placeholder="请输入积分名称" :disabled="dialog.disabled" class="sui-input"/>
        </el-form-item>
        <el-form-item label="用户姓名" prop="registerId">
          <el-select v-model="integral.registerId" placeholder="请选择用户姓名" filterable :disabled="dialog.disabled" class="sui-input">
            <el-option v-for="register in registerDict" :key="register.id" :value="register.id" :label="register.userName"/>
          </el-select>
        </el-form-item>
        <el-form-item label="积分时间" prop="integralTime">
          <el-date-picker v-model="integral.integralTime" placeholder="请选择积分时间" :disabled="dialog.disabled" type="date" value-format="yyyy-MM-dd" class="sui-input"/>
        </el-form-item>
        <el-form-item label="积分分数" prop="integralNum">
          <el-input v-model="integral.integralNum" type="number" placeholder="请输入积分分数" :disabled="dialog.disabled" class="sui-input"/>
        </el-form-item>
        <el-form-item label="积分说明" prop="remarks">
          <el-input type="textarea" v-model="integral.remarks" placeholder="请输入积分说明" :disabled="dialog.disabled" class="sui-textarea"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="cancel">取 消</el-button>
        <el-button size="small" v-if="!dialog.disabled" type="primary" v-prevent-re-click @click="submitForm()">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "integralForm",
    data() {
      return {
        registerDict:[],
        //弹窗设置
        dialog: {
          title: "",
          open: false,
          disabled: false
        },
        //表单数据
        integral: {
          id: "",
          name: "",
          registerId: "",
          integralTime: "",
          integralNum: "",
          remarks: "",
        },
        //表单校验
        rules: {
          name: [{required: true, message: '请输入积分名称', trigger: 'blur'}],
          registerId: [{required: true, message: '请选择用户姓名', trigger: 'change'}],
          integralTime: [{required: true, message: '请选择积分时间', trigger: 'change'}],
          integralNum: [{required: true, message: '请输入积分分数', trigger: 'blur'}],
          remarks: [{required: true, message: '请输入积分说明', trigger: 'blur'}],
        }
      }
    },
    computed: {
    },
    methods: {
      //【用户姓名】
      getRegisterDict() {
        this.$request.doGet('/admin/register/getList').then(res => {
          if (res.isOk) {
            this.registerDict = res.list;
          }
        })
      },
      //打开弹窗
      open(option) {
        const pageFrom = option.pageFrom;
        const disabled = pageFrom == 'view' ? true : false;
        this.showDialog(option.title, disabled);
        this.getRegisterDict();//加载【用户姓名】
        if (pageFrom == 'edit' || pageFrom == 'view') {
          this.getIntegral(option.id);
        }
      },
      //显示弹窗
      showDialog(title, disabled) {
        this.resetForm(this.$refs.integralForm);//重置表单
        this.dialog.open = true;//打开弹窗
        this.dialog.title = title;//设置标题
        this.dialog.disabled = disabled;//是否可编辑
      },
      //取消弹窗
      cancel() {
        this.dialog.open = false;
      },
      //根据id获取数据
      getIntegral(id) {
        this.$request.doGet("/admin/integral/get?id=" + id).then(res => {
          if (res.isOk) {
            this.integral = res.obj;
          }
        });
      },
      //提交表单
      submitForm() {
        const that = this;
        that.$refs.integralForm.validate((valid) => {
          if (valid) {//表单校验
            that.$request.doPost('/admin/integral/sub', that.integral).then(res => {
              if (res.isOk) {
                that.cancel();
                that.showSuccess("提交成功");
                that.$emit('reloadList');
              }
            });
          }
        });
      }

    }
  }
</script>

<style scoped>

</style>
