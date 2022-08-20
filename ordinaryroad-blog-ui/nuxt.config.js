import en from 'vuetify/lib/locale/en'
import zhHans from 'vuetify/lib/locale/zh-Hans'

export default {
  // Global page headers: https://go.nuxtjs.dev/config-head
  head: {
    titleTemplate: '%s - ordinaryroad-blog-ui',
    title: 'ordinaryroad-blog-ui',
    htmlAttrs: {
      lang: 'zh-Hans'
    },
    meta: [
      { charset: 'utf-8' },
      {
        name: 'viewport',
        content: 'width=device-width, initial-scale=1'
      },
      {
        hid: 'description',
        name: 'description',
        content: ''
      },
      {
        name: 'format-detection',
        content: 'telephone=no'
      }
    ],
    link: [
      {
        rel: 'icon',
        type: 'image/x-icon',
        href: '/favicon.ico'
      }
    ]
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: [
    '@/assets/css/ordinaryroad.css'
  ],
  less: [
    '@/assets/vditor-custom.less'
  ],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
    // 权限插件
    '~/plugins/access/index.js',
    // api插件
    '@/plugins/api/index',
    // api插件 Server端
    {
      src: '@/plugins/api/server/index',
      mode: 'server'
    },
    // axios拦截器等
    '~/plugins/axios/index.js',
    // dayjs
    '~/plugins/dayjs/index.js',
    // 国际化插件
    '~/plugins/i18n/index.js',
    // 自定义常量 工具类等
    '~/plugins/ordinaryroad/index.js',
    // vuetify client mode
    {
      src: '~/plugins/vuetify/index.js',
      mode: 'client'
    }
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
    // https://go.nuxtjs.dev/eslint
    '@nuxtjs/eslint-module',
    // https://go.nuxtjs.dev/vuetify
    '@nuxtjs/vuetify'
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [
    // https://go.nuxtjs.dev/axios
    '@nuxtjs/axios',
    '@nuxtjs/dayjs'
  ],

  // Axios module configuration: https://go.nuxtjs.dev/config-axios
  axios: {
    // Workaround to avoid enforcing hard-coded localhost:3000: https://github.com/nuxt-community/axios-module/issues/308
    baseURL: process.env.DOMAIN,
    // prefix: '/api',
    // https://axios.nuxtjs.org/options/#proxy
    proxy: true // Can be also an object with default options
  },

  proxy: {
    '/api/blog': {
      target: process.env.BLOG_BASE_URL,
      pathRewrite: {
        '^/api/blog': '/'
      }
    },
    '/api/auth': {
      target: process.env.AUTH_BASE_URL,
      pathRewrite: {
        '^/api/auth': '/'
      }
    },
    '/api': {
      target: process.env.BASE_URL,
      pathRewrite: {
        '^/api': '/'
      }
    }
  },

  // Vuetify module configuration: https://go.nuxtjs.dev/config-vuetify
  vuetify: {
    lang: {
      locales: {
        en,
        'zh-Hans': zhHans
      },
      current: 'zh-Hans'
    },
    customVariables: ['~/assets/variables.scss'],
    treeShake: true
  },

  dayjs: {
    locales: ['en', 'zh-cn'],
    defaultLocale: 'zh-cn'
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {},

  // https://www.nuxtjs.cn/guide/runtime-config#runtime-config-213
  publicRuntimeConfig: {
    BASE_URL: process.env.BASE_URL,
    // 文件下载地址
    FILE_DOWNLOAD_BASE_URL: process.env.FILE_DOWNLOAD_BASE_URL,

    OAUTH2: {
      REDIRECT_URI: `${process.env.DOMAIN}/user/authorized`,
      ordinaryroad: {
        CLIENT_ID: 'ordinaryroad-blog',
        SCOPE: 'openid,userinfo',
        AUTHORIZE_ENDPOINT: `${process.env.AUTH_BASE_URL}/oauth2/authorize?response_type=code`,
        ACCESS_TOKEN_ENDPOINT: '/api/auth/oauth2/token'
      },
      github: {
        CLIENT_ID: 'c0615d2a28cfb7a20a84',
        SCOPE: 'read:user',
        AUTHORIZE_ENDPOINT: 'https://github.com/login/oauth/authorize?1=1',
        ACCESS_TOKEN_ENDPOINT: 'https://github.com/login/oauth/access_token'
      },
      gitee: {
        CLIENT_ID: 'f6c5eb5a40981cfb3dd235686ecad5b233c49c646b0b7d71131d0dff29bb8882',
        SCOPE: 'user_info',
        AUTHORIZE_ENDPOINT: 'https://gitee.com/oauth/authorize?response_type=code',
        ACCESS_TOKEN_ENDPOINT: 'https://gitee.com/oauth/token?grant_type=authorization_code'
      }
    }

  },
  privateRuntimeConfig: {
    OAUTH2: {
      ordinaryroad: {
        CLIENT_SECRET: process.env.CLIENT_SECRET_ORDINARYROAD
      },
      github: {
        CLIENT_SECRET: process.env.CLIENT_SECRET_GIHUB
      },
      gitee: {
        CLIENT_SECRET: process.env.CLIENT_SECRET_GITEE
      }
    }
  },

  // https://www.nuxtjs.cn/api/configuration-env
  env: {},

  // https://www.nuxtjs.cn/api/configuration-router
  router: {
    extendRoutes (routes, resolve) {
      // console.log('extendRoutes', routes)
    }
  }
}
