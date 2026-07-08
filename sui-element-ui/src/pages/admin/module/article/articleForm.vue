<!-- 【文章信息】弹窗页面 -->
<template>
  <div class="body">
    <el-dialog :title="dialog.title" :close-on-click-modal="false" :visible.sync="dialog.open">
      <el-form :model="article" ref="articleForm" :rules="rules" label-width="120px" class="edit-form">
        <el-form-item label="主键id" class="hidden" prop="id">
          <el-input type="hidden" v-model="article.id"/>
        </el-form-item>
        <el-form-item label="文章图片" prop="picture">
          <sui-photo type="article" v-model="article.picture" :disabled="dialog.disabled" class="sui-input"/>
        </el-form-item>
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="article.title" placeholder="请输入文章标题" :disabled="dialog.disabled" class="sui-input"/>
        </el-form-item>
        <el-form-item label="文章作者" prop="userId">
          <el-select v-model="article.userId" placeholder="请选择文章作者" filterable :disabled="dialog.disabled" class="sui-input">
            <el-option v-for="register in registerDict" :key="register.id" :value="register.id" :label="register.loginName"/>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker v-model="article.publishTime" placeholder="请选择发布时间" :disabled="dialog.disabled" type="date" value-format="yyyy-MM-dd" class="sui-input"/>
        </el-form-item>
        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="article.categoryId" placeholder="请选择文章分类" filterable :disabled="dialog.disabled" class="sui-input">
            <el-option v-for="category in categoryDict" :key="category.id" :value="category.id" :label="category.name"/>
          </el-select>
        </el-form-item>
        <el-form-item label="文章内容" prop="content" class="sui-editor-item">
          <sui-editor v-model="article.content" placeholder="请输入文章内容" :disabled="dialog.disabled"/>
        </el-form-item>
        <el-form-item class="hidden" label="审核状态" prop="auditStatus">
          <el-select v-model="article.auditStatus" placeholder="请选择审核状态" filterable :disabled="dialog.disabled" class="sui-input">
            <el-option v-for="dict in auditStatusDict" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"/>
          </el-select>
        </el-form-item>
        <el-form-item class="hidden" label="审核结果" prop="auditResult">
          <el-input type="textarea" v-model="article.auditResult" placeholder="请输入审核结果" :disabled="dialog.disabled" class="sui-textarea"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="cancel">取 消</el-button>
        <el-button size="small" v-if="!dialog.disabled" type="primary" @click="submitForm()">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "articleForm",
    data() {
      return {
        registerDict:[],
        categoryDict:[],
        //弹窗设置
        dialog: {
          title: "",
          open: false,
          disabled: false
        },
        //表单数据
        article: {
          picture: "",
          title: "",
          userId: "",
          publishTime: "",
          categoryId: "",
          content: "",
          auditStatus: "",
          auditResult: "",
        },
        //表单校验
        rules: {
          picture: [{required: true, message: '请选择文章图片', trigger: 'change'}],
          title: [{required: true, message: '请输入文章标题', trigger: 'blur'},{validator:this.$validator.unique,form:this,model:'article',tableName:'article',column:'title',message:'文章标题已存在',trigger:'blur'}],
          userId: [{required: true, message: '请选择文章作者', trigger: 'change'}],
          publishTime: [{required: true, message: '请选择发布时间', trigger: 'change'}],
          categoryId: [{required: true, message: '请选择文章分类', trigger: 'change'}],
          content: [{required: true, message: '请输入文章内容', trigger: 'blur'}],
          //auditStatus: [{required: true, message: '请选择审核状态', trigger: 'change'}],
          //auditResult: [{required: true, message: '请输入审核结果', trigger: 'blur'}],
        }
      }
    },
    computed: {
      //【审核状态】
      auditStatusDict() {
        return this.$store.getters.getDictArray('audit_status');
      },
    },
    methods: {
      //【文章作者】
      getRegisterDict() {
        this.$request.doGet('/admin/register/getList').then(res => {
          if (res.isOk) {
            this.registerDict = res.list;
          }
        })
      },
      //【文章分类】
      getCategoryDict() {
        this.$request.doGet('/admin/category/getList').then(res => {
          if (res.isOk) {
            this.categoryDict = res.list;
          }
        })
      },
      //打开弹窗
      open(option) {
        const pageFrom = option.pageFrom;
        const disabled = pageFrom == 'view' ? true : false;
        this.showDialog(option.title, disabled);
        this.getRegisterDict();//加载【文章作者】
        this.getCategoryDict();//加载【文章分类】
        if (pageFrom == 'edit' || pageFrom == 'view') {
          this.getArticle(option.id);
        }
      },
      //显示弹窗
      showDialog(title, disabled) {
        this.resetForm(this.$refs.articleForm);//重置表单
        this.dialog.open = true;//打开弹窗
        this.dialog.title = title;//设置标题
        this.dialog.disabled = disabled;//是否可编辑
      },
      //取消弹窗
      cancel: function () {
        this.dialog.open = false;
      },
      //根据id获取数据
      getArticle(id) {
        this.$request.doGet("/admin/article/get?id=" + id).then(res => {
          if (res.isOk) {
            this.article = res.obj;
          }
        });
      },
      //提交表单
      submitForm: function () {
        const that = this;
        that.$refs.articleForm.validate((valid) => {
          if (valid) {//表单校验
            that.$request.doPost('/admin/article/sub', that.article).then(res => {
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
