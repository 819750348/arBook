<template>
  <div class="main">
    <a-form :form="form" class="user-layout-login" ref="formLogin" id="formLogin">
      <a-tabs
        :activeKey="customActiveKey"
        :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
        @change="handleTabClick">
        <a-tab-pane key="tab1" tab="账号密码登陆">
          <a-form-item>
            <a-input
              size="large"
              v-decorator="['username',validatorRules.username,{ validator: this.handleUsernameOrEmail }]"
              type="text"
              placeholder="请输入帐户名">
              <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-input
              v-decorator="['password',validatorRules.password]"
              size="large"
              type="password"
              autocomplete="false"
              placeholder="密码">
              <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>

          <a-row :gutter="0">
            <a-col :span="14">
              <a-form-item>
                <a-input
                  v-decorator="['inputCode',validatorRules.inputCode]"
                  size="large"
                  type="text"
                  @change="inputCodeChange"
                  placeholder="请输入验证码">
                  <a-icon slot="prefix" v-if=" inputCodeContent==verifiedCode " type="smile" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                  <a-icon slot="prefix" v-else type="frown" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col  :span="10">
              <j-graphic-code @success="generateCode" style="float: right"></j-graphic-code>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
      <a-form-item>
        <a-checkbox v-model="formLogin.rememberMe">自动登陆</a-checkbox>
        <router-link :to="{ name: 'register', params: { user: ''} }" class="forge-password" style="float: right;">
          没有账号？立即注册
        </router-link>
      </a-form-item>
      <a-form-item style="margin-top:24px">
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="login-button"
          :loading="loginBtn"
          @click.stop.prevent="handleSubmit"
          :disabled="loginBtn">确定
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  //import md5 from "md5"
  import api from '@/api'
  import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha'
  import { mapActions } from "vuex"
  import { timeFix } from "@/utils/util"
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import JGraphicCode from '@/components/jantd/JGraphicCode'
  import { putAction } from '@/api/manage'
  import { getAction} from '@/api/manage'
  import { encryption } from '@/utils/encryption/aesEncrypt'

  export default {
    components: {
      TwoStepCaptcha,
      JGraphicCode
    },
    data () {
      return {
        customActiveKey: "tab1",
        loginBtn: false,
        // login type: 0 email, 1 username, 2 telephone
        loginType: 0,
        requiredTwoStepCaptcha: false,
        stepCaptchaVisible: false,
        form: this.$form.createForm(this),
        state: {
          time: 60,
          smsSendBtn: false,
        },
        formLogin: {
          username: "",
          password: "",
          captcha: "",
          mobile: "",
          rememberMe: true
        },
        validatorRules:{
          username:{rules: [{ required: true, message: '请输入用户名!',validator: 'click'}]},
          password:{rules: [{ required: true, message: '请输入密码!',validator: 'click'}]},
          mobile:{rules: [{validator:this.validateMobile}]},
          captcha:{rule: [{ required: true, message: '请输入验证码!'}]},
          inputCode:{rules: [{ required: true, message: '请输入验证码!'},{validator: this.validateInputCode}]}
        },
        verifiedCode:"",
        inputCodeContent:"",
        inputCodeNull:true,

        departList:[],
        departVisible:false,
        departSelected:"",
        currentUsername:"",
        validate_status:""
      }
    },
    created () {
      Vue.ls.remove(ACCESS_TOKEN)
      // update-begin- --- author:scott ------ date:20190225 ---- for:暂时注释，未实现登录验证码功能
//      this.$http.get('/auth/2step-code')
//        .then(res => {
//          this.requiredTwoStepCaptcha = res.result.stepCode
//        }).catch(err => {
//          console.log('2step-code:', err)
//        })
      // update-end- --- author:scott ------ date:20190225 ---- for:暂时注释，未实现登录验证码功能
      // this.requiredTwoStepCaptcha = true

    },
    methods: {
      ...mapActions([ "Login", "Logout" ]),
      // handler
      handleUsernameOrEmail (rule, value, callback) {
        const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if (regex.test(value)) {
          this.loginType = 0
        } else {
          this.loginType = 1
        }
        callback()
      },
      handleTabClick (key) {
        this.customActiveKey = key
        // this.form.resetFields()
      },
      handleSubmit () {
        let that = this
        let loginParams = {
          remember_me: that.formLogin.rememberMe
        };

        // 使用账户密码登陆
        if (that.customActiveKey === 'tab1') {
          that.form.validateFields([ 'username', 'password','inputCode' ], { force: true }, (err, values) => {
            if (!err) {
              getAction("/sys/getEncryptedString",{}).then((res)=>{
                loginParams.username = values.username
                //loginParams.password = md5(values.password)
                loginParams.password = encryption(values.password,res.result.key,res.result.iv)
                that.Login(loginParams).then((res) => {
                  this.departConfirm(res)
                }).catch((err) => {
                  that.requestFailed(err);
                })
              }).catch((err) => {
                that.requestFailed(err);
              });
            }
          })
        } else {
          that.form.validateFields([ 'mobile', 'captcha' ], { force: true }, (err, values) => {
            if (!err) {
              loginParams = Object.assign(loginParams, values)
              that.loginBtn = true
              that.Login(loginParams).then(() => {
                if (that.requiredTwoStepCaptcha) {
                  that.stepCaptchaVisible = true
                } else {
                  that.loginSuccess()
                }
              }).catch((err) => {
                that.requestFailed(err);
              })
            }
          })
        }
      },
      getCaptcha (e) {
        e.preventDefault()
        let that = this

        this.form.validateFields([ 'mobile' ], { force: true },
          (err) => {
            if (!err) {
              this.state.smsSendBtn = true;

              let interval = window.setInterval(() => {
                if (that.state.time-- <= 0) {
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  window.clearInterval(interval);
                }
              }, 1000);

              const hide = this.$message.loading('验证码发送中..', 0);
              this.$http.post(api.SendSms, { mobile: that.formLogin.mobile })
                .then(res => {
                  setTimeout(hide, 2500);
                  this.$notification[ 'success' ]({
                    message: '提示',
                    description: '验证码获取成功，您的验证码为：' + res.result.captcha,
                    duration: 8
                  })
                })
                .catch(err => {
                  setTimeout(hide, 1);
                  clearInterval(interval);
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  this.requestFailed(err);
                });
            }
          }
        );
      },
      stepCaptchaSuccess () {
        this.loginSuccess()
      },
      stepCaptchaCancel () {
        this.Logout().then(() => {
          this.loginBtn = false
          this.stepCaptchaVisible = false
        })
      },
      loginSuccess () {
        this.loginBtn = false
        this.$router.push({ name: "dashboard" })
        this.$notification.success({
          message: '欢迎',
          description: `${timeFix()}，欢迎回来`,
        });
      },
      requestFailed (err) {
        this.$notification[ 'error' ]({
          message: '登录失败',
          description: ((err.response || {}).data || {}).message || err.message || "请求出现错误，请稍后再试",
          duration: 4,
        });
        this.loginBtn = false;
      },
      validateMobile(rule,value,callback){
        if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)){
          callback();
        }else{
          callback("您的手机号码格式不正确!");
        }

      },
      validateInputCode(rule,value,callback){
        if(!value || this.verifiedCode==this.inputCodeContent){
          callback();
        }else{
          callback("您输入的验证码不正确!");
        }
      },
      generateCode(value){
        this.verifiedCode = value.toLowerCase()
      },
      inputCodeChange(e){
        this.inputCodeContent = e.target.value
        if(!e.target.value||0==e.target.value){
          this.inputCodeNull=true
        }else{
          this.inputCodeContent = this.inputCodeContent.toLowerCase()
          this.inputCodeNull=false
        }
      },
      departConfirm(res){
        // if(res.success){
          // let multi_depart = res.result.multi_depart
          // //0:无部门 1:一个部门 2:多个部门
          // if(multi_depart==0){
          //   this.loginSuccess()
          //   this.$notification.warn({
          //     message: '提示',
          //     description: `您尚未归属部门,请确认账号信息`,
          //     duration:3
          //   });
          // }else if(multi_depart==2){
          //   this.departVisible=true
          //   this.currentUsername=this.form.getFieldValue("username")
          //   this.departList = res.result.departs
          // }else {
            this.loginSuccess()
          // }
        // }else{
        //   this.requestFailed(res)
        //   this.Logout();
        // }
      },
      departOk(){
        if(!this.departSelected){
          this.validate_status='error'
          return false
        }
       let obj = {
          orgCode:this.departSelected,
          username:this.form.getFieldValue("username")
        }
        putAction("/sys/selectDepart",obj).then(res=>{
          if(res.success){
            this.departClear()
            this.loginSuccess()
          }else{
            this.requestFailed(res)
            this.Logout().then(()=>{
              this.departClear()
            });
          }
        })
      },
      departClear(){
        this.departList=[]
        this.departSelected=""
        this.currentUsername=""
        this.departVisible=false
        this.validate_status=''
      },
      departChange(value){
        this.validate_status='success'
        this.departSelected = value
      }
    }
  }
</script>

<style lang="scss" scoped>

  .user-layout-login {
    label {
      font-size: 14px;
    }

    .getCaptcha {
      display: block;
      width: 100%;
      height: 40px;
    }

    .forge-password {
      color: white;
      font-size: 14px;
    }

    button.login-button {
      padding: 0 15px;
      font-size: 16px;
      height: 40px;
      width: 100%;
    }

    .user-login-other {
      text-align: left;
      margin-top: 24px;
      line-height: 22px;

      .item-icon {
        font-size: 24px;
        color: rgba(0,0,0,.2);
        margin-left: 16px;
        vertical-align: middle;
        cursor: pointer;
        transition: color .3s;

        &:hover {
          color: #1890ff;
        }
      }

      .register {
        float: right;
      }
    }
  }

</style>
<style>
  .valid-error .ant-select-selection__placeholder{
    color: #f5222d;
  }
  .ant-tabs-nav .ant-tabs-tab-active {
    color: #f5f5f5;
    font-weight: 500;
  }
</style>