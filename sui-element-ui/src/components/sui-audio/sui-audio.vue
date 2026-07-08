<template>
  <el-dialog width="600px" center :title="file.name" :visible.sync="open" :append-to-body="true" v-dialogDrag>
    <el-image :title="file.src" src="/static/images/admin/config/audio/audio.gif" style="width: 100%"/>
    <audio style="margin: 0 auto;display: block" v-if="open" width="100%" height="100%" autoplay controls>
      <source :src="file.src" type="video/mp4">
      您的浏览器不支持 audio 标签。
    </audio>
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
