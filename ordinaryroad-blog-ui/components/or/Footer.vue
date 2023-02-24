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
  <v-footer
    :app="app"
    padless
    inset
  >
    <v-container>
      <div
        class="d-flex justify-center align-center"
        :class="$vuetify.breakpoint.smAndDown?'flex-column':null"
      >
        <div
          class="d-flex"
          :class="$vuetify.breakpoint.smAndDown?null:'me-2'"
        >
          <v-btn
            icon
            target="_blank"
            href="https://github.com/1962247851"
          >
            <v-icon>
              mdi-github
            </v-icon>
          </v-btn>
        </div>
        <div class="d-flex text--grey">
          ©2019 - {{ new Date().getFullYear() }} 苗锦洲 {{ $t('allRightsReserved') }}
        </div>
      </div>
      <div
        class="d-flex justify-center align-center font-weight-light"
        :class="$vuetify.breakpoint.smAndDown?'flex-column':null"
      >
        <div class="d-flex my-1">
          Frontend:<span class="ms-1">{{ $config.CI.VERSION.FRONTEND }}</span><a
            class="d-flex mx-2"
            href="https://ordinaryroad.coding.net/p/ordinaryroad/ci/job"
          ><img
            style="min-width: 120px"
            alt="build status"
            src="https://ordinaryroad.coding.net/badges/ordinaryroad/job/1495289/build.svg"
          ></a>
        </div>
        <div class="d-flex my-1">
          Backend:<span class="ms-1">{{ $config.CI.VERSION.BACKEND }}</span><a
            class="d-flex ms-2"
            href="https://ordinaryroad.coding.net/p/ordinaryroad/ci/job"
          ><img
            style="min-width: 120px"
            alt="build status"
            src="https://ordinaryroad.coding.net/badges/ordinaryroad/job/1483779/build.svg"
          ></a>
        </div>
      </div>
      <div
        class="d-flex justify-center align-center font-weight-light"
        :class="$vuetify.breakpoint.smAndDown?'flex-column':null"
      >
        <or-link
          hover-able
          :class="$vuetify.breakpoint.smAndDown?null:'me-2'"
          :href="$config.BEIAN.GONG_AN_HREF"
          hide-icon
        >
          <span class="d-flex justify-center align-center"><img
            alt="备案图标"
            class="me-1"
            :src="require('~/assets/img/gongan.png')"
          >{{ $config.BEIAN.GONG_AN_NUMBER }}</span>
        </or-link>
        <or-link
          hover-able
          href="https://beian.miit.gov.cn"
          hide-icon
        >
          {{ $config.BEIAN.ICP_NUMBER }}
        </or-link>
      </div>
    </v-container>
  </v-footer>
</template>

<script>
export default {
  name: 'OrFooter',
  props: {
    app: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style scoped>

</style>
