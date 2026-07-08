1.npm install //引入项目依赖

2.npm run dev //运行

3.npm run build //打包

<!--【收藏】按钮-->
<sui-collect v-if="item.id" :refId="item.id" :refContent="item.title" type="收藏"/>
<!--【点赞】按钮-->
<sui-like v-if="item.id" :refId="item.id" type="点赞"/>
<!--【评价|评论】按钮-->
<sui-evaluate style="text-align: right " v-if="item.id" :refId="item.id" @success="success" type="评论"/>
<!--【评价|评论】列表-->
<user-evaluate v-if="item.id" ref="userEvaluate" :refId="item.id" type="评论"></user-evaluate>
<!--富文本-->
<sui-editor v-model="item.content" placeholder="请输入内容" :disabled="dialog.disabled"/>
<!--文件列表-->
<el-form-item label="文件列表" prop="fileList">
  <sui-file type="notice" :refId="notice.id" v-model="notice.fileList" v-if="dialog.open" :disabled="dialog.disabled"/>
</el-form-item>
<!--百度地图-->
<sui-map :center="{lng: 116.403326, lat: 39.915241}" :zoom="15" keyword="北京市" :showLine="true" start="天安门" end="天坛公园"></sui-map>

