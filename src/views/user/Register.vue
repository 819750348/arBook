<template>
  <div class="main">
    <a-form :form="form" class="user-layout-login" ref="formLogin" id="formLogin">
      <a-tabs
        :activeKey="customActiveKey"
        :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
        @change="handleTabClick">
        <a-tab-pane key="tab1" tab="用户注册">
          <a-form-item>
            <a-input
            size="large"
            v-decorator="['username',validatorRules.username,{validator:this.handleUsernameOrEmail}]"
            type="text"
            placeholder="请输入账号名">
              <a-icon slot="prefix" type="user" :style="{color: 'rgba(0,0,0,.25)'}"/>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-input
            v-decorator="['password',validatorRules.password]"
            size="large"
            type="password"
            autocomplete="false"
            placeholder="请输入密码">
              <a-icon slot="prefix" type="lock" :style="{color: 'rgba(0,0,0,.25)'}"/>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-input
              v-decorator="['confirm',{rules: [{required: true,message: '请确定你的密码!'},{validator: compareToFirstPassword}]}]"
              type="password"
              size="large"
              autocomplete="false"
              placeholder="请再次输入密码"
              @blur="handleConfirmBlur">
              <a-icon slot="prefix" type="lock" :style="{color: 'rgba(0,0,0,.25)'}"/>
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
        </a-tab-pane>
      </a-tabs>

    </a-form>
  </div>
</template>

<script>
    import AFormItem from "ant-design-vue/es/form/FormItem";
    import JGraphicCode from '@/components/jantd/JGraphicCode'
    import { encryption } from '@/utils/encryption/aesEncrypt'
    import { getAction} from '@/api/manage'
    import {addUser} from '@/api/api'
    import {register} from "../../api/register";
    export default {
      name: "Register",
      components: {AFormItem,JGraphicCode},
      data(){
          return{
            form: this.$form.createForm(this),
            verifiedCode:"",
            loginType: 0,
            inputCodeContent:"",
            formLogin: {
              username: "",
              password: "",
              captcha: "",
              mobile: "",
              // rememberMe: true
            },
            customActiveKey: "tab1",
            validatorRules:{
              username:{rules: [{ required: true, message: '请输入用户名!',validator: 'click'}]},
              password:{rules: [{ required: true, message: '请输入密码!',validator: 'click'}]},
              // passwordtwo:{rules: [{ required: true, message: '两次密码不同!',validator: 'click'}]},

              confirm:{rules: [{required: true,message: '请确定你的密码!'},{validator: this.compareToFirstPassword}]},
              captcha:{rule: [{ required: true, message: '请输入验证码!'}]},
              inputCode:{rules: [{ required: true, message: '请输入验证码!'},{validator: this.validateInputCode}]}
            }
          }
      },
      methods:{
        handleTabClick (key){
          this.customActiveKey = key
        },
        handleUsernameOrEmail (rule, value, callback) {
          const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
          if (regex.test(value)) {
            this.loginType = 0
          } else {
            this.loginType = 1
          }
          callback()
        },
        generateCode(value){
          this.verifiedCode = value.toLowerCase()
        },
        handleTabClick (key) {
          this.customActiveKey = key
          // this.form.resetFields()
        },
        handleSubmit () {
          let that = this
          let loginParams = {
            // remember_me: that.formLogin.rememberMe
          };

          // 使用账户密码登陆
          if (that.customActiveKey === 'tab1') {
            that.form.validateFields(['username', 'password', 'inputCode'], {force: true}, (err, values) => {
              if (!err) {
                getAction("/sys/getEncryptedString", {}).then((res) => {
                  loginParams.username = values.username
                  loginParams.password = encryption(values.password, res.result.key, res.result.iv)
                  loginParams.selectedroles='e51758fa916c881624b046d26bd09230'
                  register(loginParams).then((res) => {
                    if(res.success){
                        that.$store.commit('setRegisterSuccessUserName',values.username)
                        that.$router.push({
                          name:'/registerSuccess'
                        })
                    }else{
                      that.$message.warning(res.message);
                    }
                  }).catch((err) => {
                    that.requestFailed(err);
                  })
                }).catch((err) => {
                  that.requestFailed(err);
                });
              }
            })
          }
        },
        comparePassword(e){
          console.log(e)
        },
        handleConfirmBlur(e) {
          const value = e.target.value;
          this.confirmDirty = this.confirmDirty || !!value;
        },
        compareToFirstPassword(rule, value, callback) {
          const form = this.form;
          if (value && value !== form.getFieldValue('password')) {
            callback('两次密码不一样!');
          } else {
            callback();
          }
        }
      }
    }
</script>

<style scoped>
  button.login-button {
    padding: 0 15px;
    font-size: 16px;
    height: 40px;
    width: 100%;
  }
  </style>
<style>
  .ant-tabs-nav .ant-tabs-tab-active {
    color: #f5f5f5;
    font-weight: 500;
  }
</style>