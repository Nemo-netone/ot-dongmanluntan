<template>
  <div :id="formatUrl(value)">
    <sui-view ref="view"></sui-view>
    <el-image @click="handlePreview" class="column-image" :src="formatUrl(value)">
      <div slot="error" class="image-slot">
        <i class="el-icon-picture-outline" style="font-size: 25px"></i>
      </div>
    </el-image>
  </div>
</template>
<script>
  export default {
    props:{
      value: {
        type: String,
        default: ''
      },
    },
    methods:{
      //预览文件
      handlePreview() {
        const that = this;
        let filePath = this.formatUrl(this.value);
        const name = filePath.toLowerCase();
        if (/.(gif|jpg|jpeg|png|webp)$/.test(name)) {
          that.$refs.view.imgViewer({name:"图片预览",url:filePath},filePath)
        }
      },
    }
  };
</script>
