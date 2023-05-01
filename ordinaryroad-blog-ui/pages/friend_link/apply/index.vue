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
  <v-row>
    <v-col
      md="8"
      cols="12"
      order="2"
      order-md="1"
    >
      <base-material-card :title="$t('friendLink.applyFormTitle')">
        <div v-if="finish" class="text-center pa-5">
          <v-tab-transition>
            <div v-if="loading">
              <v-progress-circular size="50" indeterminate color="primary" />
              <div class="mt-2">
                {{ $t('friendLink.applySending') }}
              </div>
            </div>
            <div v-else>
              <v-icon :color="success?'success':'error'" size="50">
                {{ success ? 'mdi-check' : 'mdi-close' }}
              </v-icon>
              <div class="mt-2">
                <div v-if="success">
                  {{ $t('friendLink.applySuccessHint') }}<br>
                  <a style="cursor: pointer" @click="$router.back()">{{ $t('back') }}</a>
                </div>
                <div v-else>
                  {{ $t('friendLink.applyFailedHintPrefix') }}<a style="cursor: pointer" @click="finish=false">{{
                    $t('friendLink.applyFailedHintSuffix')
                  }}</a>
                </div>
              </div>
            </div>
          </v-tab-transition>
        </div>
        <v-form v-else ref="form" class="pa-5">
          <v-text-field
            v-model="friendLink.name"
            counter="50"
            :rules="[$or.rules.notBlank,$or.rules.max50Chars]"
            clearable
            :label="$t('friendLink.name')"
          />
          <v-text-field
            v-model="friendLink.description"
            counter="100"
            :rules="[$or.rules.notBlank,$or.rules.max100Chars]"
            clearable
            :label="$t('friendLink.description')"
          />
          <v-text-field
            v-model="friendLink.url"
            counter="500"
            :rules="urlRules"
            clearable
            :label="$t('friendLink.url')"
          />
          <v-text-field
            v-model="friendLink.logo"
            counter="500"
            :rules="logoRules"
            clearable
            :label="$t('friendLink.logo')"
          />
          <v-text-field
            v-model="friendLink.email"
            counter="500"
            :rules="emailRules"
            clearable
            :label="$t('friendLink.email')"
          />

          <div class="d-flex justify-end pt-5">
            <v-btn text class="mr-4" @click="$router.back()">
              {{ $t('back') }}
            </v-btn>
            <v-btn class="mr-4" @click="reset">
              {{ $t('reset') }}
            </v-btn>
            <v-btn color="primary" @click="apply">
              {{ $t('submit') }}
            </v-btn>
          </div>
        </v-form>
      </base-material-card>
    </v-col>
    <v-col
      md="4"
      cols="12"
      order="1"
      order-md="2"
    >
      <base-material-card
        :title="$t('friendLink.thisSiteInfo')"
        :subtitle="$t('friendLink.thisSiteInfoCanCopyHint')"
      >
        <div v-for="(item,index) in $config.FRIEND_LINK_INFO" :key="index">
          {{ $t(item.key) }}{{ $t('punctuation.colonWithSuffixSpace') }}<span
            :id="'websiteInfo_'+index"
            style="cursor: pointer"
            @click="copyToClipboard(index,item.value)"
          >{{ item.value }}</span>
        </div>
      </base-material-card>
    </v-col>
  </v-row>
</template>

<script>
export default {
  data: () => ({
    loading: false,
    finish: false,
    success: false,
    friendLink: {}
  }),

  head () {
    return {
      title: this.$t('friendLink.apply')
    }
  },

  computed: {
    urlRules () {
      return [
        this.$or.rules.notBlank,
        v => (v && v.startsWith('http')) || '请输入正确的网站地址',
        v => /(http|https):\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]/.test(v) || '请输入正确的网站地址',
        this.$or.rules.max500Chars
      ]
    },
    logoRules () {
      return [
        this.$or.rules.notBlank,
        v => (v && v.startsWith('http')) || '请输入正确的网站Logo地址',
        v => /(http|https):\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]/.test(v) || '请输入正确的网站Logo地址',
        this.$or.rules.max500Chars
      ]
    },
    emailRules () {
      return [
        this.$or.rules.notBlank,
        v => /.+@.+\..+/.test(v) || '请输入正确的站长Email地址',
        this.$or.rules.max500Chars
      ]
    }
  },
  mounted () {
  },
  methods: {
    copyToClipboard (index, value) {
      const transfer = document.createElement('input')
      document.body.appendChild(transfer)
      transfer.value = value // 这里表示想要复制的内容
      transfer.select()
      if (document.execCommand('copy')) {
        transfer.blur()
        document.body.removeChild(transfer)
        document.getElementById('websiteInfo_' + index).style.color = 'green'
        setTimeout(() => {
          document.getElementById('websiteInfo_' + index).style.color = null
        }, 1000)
      }
    },
    reset () {
      this.$refs.form.reset()
    },
    apply () {
      if (this.$refs.form.validate()) {
        this.loading = true
        this.finish = true
        this.$apis.blog.friend_link.apply(this.friendLink)
          .then(() => {
            this.loading = false
            this.success = true
          })
          .catch(() => {
            this.loading = false
            this.success = false
          })
      }
    }
  }
}
</script>

<style scoped>

</style>
