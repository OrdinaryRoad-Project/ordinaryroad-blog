<!--
  - MIT License
  -
  - Copyright (c) 2021 苗锦洲
  -
  - Permission is hereby granted, free of charge, to any person obtaining a copy
  - of this software and associated documentation files (the "Software"), to deal
  - in the Software without restriction, including without limitation the rights
  - to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  - copies of the Software, and to permit persons to whom the Software is
  - furnished to do so, subject to the following conditions:
  -
  - The above copyright notice and this permission notice shall be included in all
  - copies or substantial portions of the Software.
  -
  - THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  - IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  - FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  - AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  - LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  - OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  - SOFTWARE.
  -->

<template>
  <base-material-card title="个人中心">
    <v-card flat outlined>
      <v-card-title>基本信息</v-card-title>
      <v-form ref="usernameForm" class="mx-4">
        <div class="d-flex align-center">
          <v-text-field
            v-model="usernameTextField.input"
            :rules="[$rules.notBlank,$rules.max10Chars]"
            :loading="usernameTextField.loading"
            :disabled="usernameTextField.disabled"
            type="text"
            :label="$t('username')"
          />
          <v-btn class="ms-3" icon @click="usernameClick">
            <v-icon>
              mdi-{{
                usernameTextField.disabled ? 'pencil'
                : usernameTextField.input === usernameTextField.value ? 'close'
                  : 'check'
              }}
            </v-icon>
          </v-btn>
        </div>
      </v-form>
    </v-card>
    <v-card flat outlined class="mt-2">
      <v-card-title>所有账号</v-card-title>
      <v-list>
        <v-list-item
          v-for="(provider) in $config.OAUTH2.PROVIDERS"
          :key="provider"
        >
          <span class="me-5">{{ provider }}</span>

          <template v-if="oAuthUser(provider)">
            <v-avatar class="my-2 me-4">
              <v-img :src="$apis.blog.getFileUrl(oAuthUser(provider).avatar)" />
            </v-avatar>
            <v-list-item-content>
              <v-list-item-title>{{ oAuthUser(provider).username }}</v-list-item-title>
              <v-list-item-subtitle>
                添加时间：{{ oAuthUser(provider).createdTime }}
                <span v-if="oAuthUser(provider).updateTime">
                  <br>
                  更新时间：{{ oAuthUser(provider).updateTime }}
                </span>
              </v-list-item-subtitle>
            </v-list-item-content>
          </template>

          <span class="ml-auto">
            <v-btn
              text
              :color="oAuthUser(provider)?null:'warning'"
              @click.stop="onClickRemoveOrAdd(provider,oAuthUser(provider))"
            >
              {{ oAuthUser(provider) ? $t('remove') : $t('add') }}
            </v-btn>
            <v-btn v-if="oAuthUser(provider)" text color="success" @click.stop="onClickUpdate(provider)">
              {{ $t('update') }}
            </v-btn>
          </span>
        </v-list-item>
      </v-list>
    </v-card>
  </base-material-card>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data: () => ({
    usernameTextField: {
      value: '',
      input: '',
      disabled: true,
      loading: false
    },
    oauthUsers: []
  }),
  computed: {
    oAuthUser () {
      return (provider) => {
        const query = this.$util.query(this.oauthUsers, 'provider', provider)
        return query[0]
      }
    },
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    })
  },
  created () {
    this.$apis.blog.oauth_user.all()
      .then((data) => {
        this.oauthUsers = data
      })
    this.usernameTextField.value = this.userInfo.user.username
    this.usernameTextField.input = this.userInfo.user.username
  },
  methods: {
    onClickRemoveOrAdd (provider, oAuthUser) {
      if (oAuthUser) {
        // remove
        this.$snackbar.info('开发中...')
      } else {
        // TODO add
        const state = `${this.$dayjs().valueOf()}_${this.$route.path}_${provider}_add`
        this.$apis.blog.oauth2.authorize(provider, state)
          .then((data) => {
            window.open(data, '_self')
          })
      }
    },
    onClickUpdate (provider) {
      this.$snackbar.info('开发中...')
    },
    usernameClick () {
      if (this.usernameTextField.disabled) {
        this.usernameTextField.disabled = false
      } else if (this.usernameTextField.input === this.usernameTextField.value) {
        this.usernameTextField.disabled = true
      } else if (this.$refs.usernameForm.validate()) {
        this.usernameTextField.loading = true

        this.$snackbar.info('开发中...')
        this.usernameTextField.loading = false

        /* TODO this.updateUsername({
           username: this.usernameTextField.input,
           $apis: this.$apis
         }).then(() => {
           this.usernameTextField.loading = false
           this.$snackbar.success(this.$t('whatUpdateSuccessfully', [this.$t('username')]))
           this.usernameTextField.value = this.usernameTextField.input
           this.usernameTextField.disabled = true
         }).catch(() => {
           this.usernameTextField.loading = false
         }) */
      }
    }
  }
}
</script>

<style scoped>

</style>
