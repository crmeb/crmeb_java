<template>
  <div class="components-container">
    <el-form ref="editPram" :model="editPram" label-width="100px">
      <el-form-item label="父级">
        <!--          <span>{{ prent.name}}</span>-->
        <el-cascader
          v-model="editPram.pid"
          :options="allTreeList"
          :props="categoryProps"
          disabled
          style="width:100%"
        />
      </el-form-item>
      <el-form-item
        label="分类名称"
        prop="name"
        :rules="[{ required:true,message:'请输入分类名称',trigger:['blur','change'] }]"
      >
        <el-input v-model="editPram.name" placeholder="分类名称" />
      </el-form-item>
      <el-form-item label="英文名称" prop="url" :rules="[{required:true,message:'英文名称不能为空',trigger:['blur','change']}]">
        <el-input v-model="editPram.url" placeholder="URL" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="editPram.sort" :min="1" :max="10" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="editPram.status" :active-value="true" :inactive-value="false" />
      </el-form-item>
      <!--        这里的类型是无限极分类的类型不能当做配置分类的类型，使用下面的扩展字段实现原有业务中的类型-->
      <!--        <el-form-item label="类型" prop="type" :rules="[{required:true,message:'请选择类型',trigger:['blur']}]">-->
      <!--          <el-select v-model="editPram.type" disabled>-->
      <!--            <el-option v-for="item in constants.categoryType" :key="item.value"-->
      <!--                       :label="item.name" :value="item.value"></el-option>-->
      <!--          </el-select>-->
      <!--        </el-form-item>-->
      <!--      <el-form-item label="类型">-->
      <!--        <el-radio-group v-model="editPram.extra">-->
      <!--          <el-radio v-for="item,index in constants.configCategory" :label="item.value">-->
      <!--            {{ item.label }}-->
      <!--          </el-radio>-->
      <!--        </el-radio-group>-->
      <!--        &lt;!&ndash;          <el-input type="textarea" v-model="editPram.extra" placeholder="扩展字段"/>&ndash;&gt;-->
      <!--      </el-form-item>-->
      <el-form-item>
        <el-button size="mini" type="primary" @click="handlerSubmit('editPram')">确定</el-button>
        <el-button size="mini" @click="close">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import * as categoryApi from '@/api/categoryApi.js'
import * as selfUtil from '@/utils/ZBKJIutil.js'
import {Debounce} from '@/utils/validate'
export default {
  // name: "configCategotyEdit"
  props: {
    prent: {
      type: Object,
      default: 0
    },
    isCreate: {
      type: Number,
      default: 0
    },
    editData: {
      type: Object
    },
    allTreeList: {
      type: Array
    }
  },
  data() {
    return {
      constants: this.$constants,
      editPram: {
        extra: null, // 关联表单id
        name: null,
        pid: null,
        sort: 0,
        status: true,
        type: this.$constants.categoryType[5].value,
        url: null,
        id: 0
      },
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false
      },
      parentOptions: []
    }
  },
  mounted() {
    this.initEditData()
  },
  methods: {
    close() {
      this.$emit('hideEditDialog')
    },
    initEditData() {
      this.parentOptions = selfUtil.addTreeListLabelForCasCard(this.allTreeList)
      if (this.isCreate !== 1) {
        const { id } = this.prent
        this.editPram.pid = id
      } else {
        const { extra, name, pid, sort, status, type, url, id } = this.editData
        // this.editPram.extra = extra
        this.editPram.name = name
        this.editPram.pid = pid
        this.editPram.sort = sort
        this.editPram.status = status
        this.editPram.type = type
        this.editPram.url = url
        this.editPram.id = id
        this.editPram.extra = extra
      }
    },
    handlerSubmit:Debounce(function(formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) return
        this.handlerSaveOrUpdate(this.isCreate === 0)
      })
    }),
    handlerSaveOrUpdate(isSave) {
      if (isSave) {
        this.editPram.pid = this.prent.id
        categoryApi.addCategroy(this.editPram).then(data => {
          this.$emit('hideEditDialog')
          this.$message.success('创建分类成功')
        })
      } else {
        this.editPram.pid = Array.isArray(this.editPram.pid) ? this.editPram.pid[0] : this.editPram.pid
        categoryApi.updateCategroy(this.editPram).then(data => {
          this.$emit('hideEditDialog')
          this.$message.success('更新分类成功')
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
