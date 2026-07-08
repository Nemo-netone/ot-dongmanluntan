<template>
    <el-dialog width="600px" center :title="file.name" :visible.sync="open" :append-to-body="true" v-dialogDrag>
      <video v-if="open" width="100%" height="100%" autoplay controls>
        <source :src="file.src" type="video/mp4">
        您的浏览器不支持 video 标签。
      </video>
    </el-dialog>
</template>
<script>
  //视频预览
  export default {
    props: {
      title: {
        type: String,
        default: ''
      },
      src: {
        type: String,
        default: ''
      },
    },
    data(){
      return{
        file:{
          name:"",
          src:""
        },
        //弹窗设置
        open: false,
      }
    },
    mounted() {
    },
    methods:{
      play(file) {
        this.open=true;
        this.file=file;
      },
    }
  }
</script>
