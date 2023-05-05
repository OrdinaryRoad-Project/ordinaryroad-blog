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
  <v-menu
    disabled
    open-delay="500"
    open-on-hover
  >
    <template #activator="{ on, attrs }">
      <v-chip
        :small="small"
        :color="roleColor(role.roleCode)"
        :outlined="roleOutlined(role.roleCode)"
        label
        :class="chipClass"
        v-bind="attrs"
        v-on="on"
      >
        {{ role.roleName }}
      </v-chip>
    </template>
    <v-card>
      <v-card-title>{{ role.roleName }}</v-card-title>
    </v-card>
  </v-menu>
</template>

<script>
export default {
  name: 'OrUserRoleChip',
  props: {
    small: {
      type: Boolean,
      default: true
    },
    role: {
      type: Object,
      required: true
    },
    chipClass: {
      type: String,
      default: ''
    }
  },
  computed: {
    roleColor () {
      return (roleCode) => {
        let color = null
        const primary = ['DEVELOPER', 'ADMIN', 'SSSSSSVIP']
        if (primary.includes(roleCode)) {
          color = 'primary'
        }
        return color
      }
    },
    roleOutlined () {
      return (roleCode) => {
        return !['DEVELOPER'].includes(roleCode)
      }
    }
  }
}
</script>

<style scoped>

</style>
