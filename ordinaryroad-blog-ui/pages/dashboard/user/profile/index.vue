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
  <base-material-card
    icon="mdi-account"
    :title="$t('userMenuTitles.profile')"
  >
    <v-card flat outlined>
      <v-card-title>{{ $t('basicInfo') }}</v-card-title>
      <v-form ref="avatarForm" class="mx-4">
        <div class="d-flex align-center">
          <v-file-input
            :loading="avatarForm.loading"
            truncate-length="100"
            accept="image/*"
            prepend-icon=""
            :rules="[$or.rules.maxFileSize10MB]"
            show-size
            :label="$t('avatar')"
            :clearable="false"
            @change="onPictureSelected"
          />
          <or-blog-user-avatar
            avatar-class="ms-2"
            disable-menu
            :size="40"
            :user="userInfo.user"
          />
          <v-btn
            v-if="userInfo.user.avatar"
            class="ms-3"
            icon
            @click="onPictureSelected(null)"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </div>
      </v-form>
      <v-form ref="usernameForm" class="mx-4">
        <div class="d-flex align-center">
          <v-text-field
            v-model="usernameTextField.input"
            :rules="[$or.rules.notBlank,$or.rules.max20Chars]"
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
      <v-card-title>{{ $t('userProfile.allAccounts') }}</v-card-title>
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
                {{ $t('createdTime') }}{{
                  $t('punctuation.colonWithSuffixSpace')
                }}{{ $dayjs(oAuthUser(provider).createdTime).format() }}
                <span v-if="oAuthUser(provider).updateTime">
                  <br>
                  {{ $t('updateTime') }}{{
                    $t('punctuation.colonWithSuffixSpace')
                  }}{{ $dayjs(oAuthUser(provider).updateTime).format() }}
                </span>
              </v-list-item-subtitle>
            </v-list-item-content>
          </template>

          <span class="ml-auto">
            <v-btn
              :disabled="oAuthUser(provider)&&oAuthUsers.length<=1"
              text
              :color="oAuthUser(provider)?null:'warning'"
              @click.stop="onClickProvider(provider,oAuthUser(provider)?'remove':'add')"
            >
              {{ oAuthUser(provider) ? $t('oAuthUser.actions.remove') : $t('oAuthUser.actions.add') }}
            </v-btn>
            <v-btn v-if="oAuthUser(provider)" text color="success" @click.stop="onClickProvider(provider,'update')">
              {{ $t('oAuthUser.actions.update') }}
            </v-btn>
          </span>
        </v-list-item>
      </v-list>
    </v-card>
  </base-material-card>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
  data: () => ({
    avatarForm: {
      value: '',
      input: '',
      disabled: true,
      loading: false
    },
    usernameTextField: {
      value: '',
      input: '',
      disabled: true,
      loading: false
    },
    oAuthUsers: []
  }),
  computed: {
    oAuthUser () {
      return (provider) => {
        const query = this.$or.util.query(this.oAuthUsers, 'provider', provider)
        return query[0]
      }
    },
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    })
  },
  created () {
    this.getAllOAuthUser()
    this.usernameTextField.value = this.userInfo.user.username
    this.usernameTextField.input = this.userInfo.user.username
  },
  methods: {
    ...mapActions('user', {
      updateAvatar: 'updateAvatar',
      updateUsername: 'updateUsername'
    }),

    onPictureSelected (file) {
      if (file == null) {
        if (this.userInfo.user.avatar) {
          this.$dialog({
            content: this.$t('areYouSureToDoWhat', [this.$t('clearAvatar')]),
            loading: true
          })
            .then((dialog) => {
              if (dialog.isConfirm) {
                this.avatarForm.loading = true
                this.updateAvatar({ avatar: '', $apis: this.$apis })
                  .then(() => {
                    this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('clearAvatar')]))
                    this.avatarForm.loading = false
                    dialog.cancel()
                  })
                  .catch(() => {
                    this.avatarForm.loading = false
                    dialog.cancel()
                  })
              }
            })
        } else {
          this.$snackbar.info('没有头像可以清空...')
        }
      } else if (this.$refs.avatarForm.validate()) {
        this.avatarForm.loading = true
        this.$apis.blog.upload(file)
          .then((data) => {
            this.updateAvatar({ avatar: data, $apis: this.$apis })
              .then(() => {
                this.$snackbar.success(this.$t('whatUpdateSuccessfully', [this.$t('avatar')]))
                this.avatarForm.loading = false
              })
              .catch(() => {
                this.avatarForm.loading = false
              })
          })
          .catch(() => {
            this.avatarForm.loading = false
          })
      }
    },
    getAllOAuthUser () {
      this.$apis.blog.oauth_user.all()
        .then((data) => {
          this.oAuthUsers = data
        })
    },
    /**
     * OAuth2账号相关操作
     * @param provider ordinaryroad|github|gitee
     * @param action add|remove|update
     */
    onClickProvider (provider, action) {
      if (action === 'remove') {
        this.$dialog({
          content: this.$t('areYouSureToDoWhat', [this.$t('oAuthUser.actions.remove')]),
          loading: true
        }).then((dialog) => {
          if (dialog.isConfirm) {
            this.$apis.blog.oauth_user.delete(provider)
              .then(() => {
                this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('oAuthUser.actions.remove')]))
                this.getAllOAuthUser()
                dialog.cancel()
              })
              .catch(() => {
                dialog.cancel()
              })
          } else {
            dialog.cancel()
          }
        })
      } else {
        const state = `${this.$dayjs().valueOf()}$${this.$route.path}$${provider}$${action}`
        if (action === 'add') {
          this.$dialog({
            content: this.$t('loginHint'),
            confirmText: this.$t('understandAnd', [this.$t('oAuthUser.actions.add')])
          })
            .then(({ isConfirm }) => {
              if (isConfirm) {
                this.$apis.blog.oauth2.authorize(provider, state)
                  .then((data) => {
                    window.open(data, '_self')
                  })
              }
            })
        } else {
          this.$apis.blog.oauth2.authorize(provider, state)
            .then((data) => {
              window.open(data, '_self')
            })
        }
      }
    },
    usernameClick () {
      if (this.usernameTextField.disabled) {
        this.usernameTextField.disabled = false
      } else if (this.usernameTextField.input === this.usernameTextField.value) {
        this.usernameTextField.disabled = true
      } else if (this.$refs.usernameForm.validate()) {
        this.usernameTextField.loading = true
        this.updateUsername({
          username: this.usernameTextField.input,
          $apis: this.$apis
        }).then(() => {
          this.usernameTextField.loading = false
          this.$snackbar.success(this.$t('whatUpdateSuccessfully', [this.$t('username')]))
          this.usernameTextField.value = this.usernameTextField.input
          this.usernameTextField.disabled = true
        }).catch(() => {
          this.usernameTextField.loading = false
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
