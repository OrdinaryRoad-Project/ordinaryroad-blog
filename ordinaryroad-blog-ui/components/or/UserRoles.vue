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
  <span v-if="roles.length">
    <v-chip
      v-for="(role,index) in roles"
      :key="role.roleCode"
      :class="index!==roles.length-1?'me-2':null"
      small
      :color="roleColor(role.roleCode)"
      :outlined="roleOutlined(role.roleCode)"
      label
    >
      {{ role.roleName }}
    </v-chip>
  </span>
</template>

<script>
export default {
  name: 'OrUserRoles',
  props: {
    roles: {
      type: Array,
      default: () => ([])
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
        return !['DEVELOPER', 'ADMIN', 'SSSSSSVIP'].includes(roleCode)
      }
    }
  }
}
</script>

<style scoped>

</style>
