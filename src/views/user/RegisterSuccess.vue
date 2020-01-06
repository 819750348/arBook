<template>
    <div>
      <a-row type="flex" justify="center" style="margin-top: 20%;font-weight: bold" >
        <a-col>
          {{registerSuccessUserName}}
        </a-col>
        <a-col>
          {{"用户,注册成功"}}
        </a-col>
        <a-col>
          {{skipNum}}
          {{"秒后进入登入页面..."}}
        </a-col>
      </a-row>
    </div>
</template>

<script>
  import {mapState} from  'vuex'
    export default {
        name: "RegisterSuccess.vue",
      data(){
          return{
            userName:'',
            skipNum: 5,
            timerN:''
          }
      },
      methods:{
        setTimer(){
            let that = this
          that.skipNum = 5
             this.timerN=setInterval(function () {
            that.skipNum=that.skipNum-1
              if(that.skipNum<0){
                that.$router.push({
                  name:'/user/login'
                })
              }
          },1000)
        }
      },
      computed:{
         ...mapState({
          registerSuccessUserName:state=> state.user.registerSuccessUserName
        })
      },
      created(){
        /**
         * 计时器
         *
         * @Author 尘埃Friend
         * @date 2019-12-03
         */
        this.setTimer()
      },
      beforeDestroy(){
        clearInterval(this.timerN)
        this.timerN = null
      }
    }
</script>

<style scoped>

</style>