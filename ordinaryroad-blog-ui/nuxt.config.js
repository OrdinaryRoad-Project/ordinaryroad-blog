import en from 'vuetify/lib/locale/en'
import zhHans from 'vuetify/lib/locale/zh-Hans'

export default {
  // Global page headers: https://go.nuxtjs.dev/config-head
  head: {
    titleTemplate: '%s - OR博客',
    title: 'OR博客',
    htmlAttrs: {
      lang: 'zh-CN'
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
        content: 'OR博客，一个Markdown多人博客'
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
    '@/assets/css/ordinaryroad.css',
    '@/assets/jv-viewer.scss',
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
    // 路由插件
    '~/plugins/router/statistics/index.js',
    // rule插件
    '~/plugins/rules/index.js',
    // util插件
    '~/plugins/utils/index.js',
    { src: '~/plugins/router/access.js', mode: 'client' },
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
    '@nuxtjs/dayjs',
    // If you use other modules (eg. nuxt-i18n), always declare the sitemap module at end of array
    '@nuxtjs/sitemap'
  ],

  // Axios module configuration: https://go.nuxtjs.dev/config-axios
  axios: {
    // debug: true,
    // Workaround to avoid enforcing hard-coded localhost:3000: https://github.com/nuxt-community/axios-module/issues/308
    // baseURL: process.env.DOMAIN,
    prefix: '/api',
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
    theme: {
      options: {
        // https://vuetifyjs.com/zh-Hans/features/theme/#section-81ea5b9a4e495c5e6027
        customProperties: true
      }
    },
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
    STATISTICS: {
      BAIDU: {
        CODE: process.env.STATISTICS_BAIDU_CODE
      }
    },

    BASE_URL: process.env.BASE_URL,
    // 文件下载地址
    FILE_DOWNLOAD_BASE_URL: process.env.FILE_DOWNLOAD_BASE_URL,

    OAUTH2: {
      PROVIDERS: ['ordinaryroad', 'github', 'gitee']
    },

    APP_VERSION: require('./package.json').version,

    CI: {
      VERSION: {
        FRONTEND: process.env.CI_VERSION_FTONTEND,
        BACKEND: process.env.CI_VERSION_BACKEND
      }
    }
  },
  privateRuntimeConfig: {},

  // https://www.nuxtjs.cn/api/configuration-env
  env: {},

  // https://www.nuxtjs.cn/api/configuration-router
  router: {
    extendRoutes (routes, resolve) {
      // console.log('extendRoutes', routes)
    }
  },

  sitemap: {
    path: '/sitemap.xml',
    hostname: 'https://blog.ordinaryroad.tech',
    cacheTime: 8 * 3600 * 1000,
    exclude: [
      '/user/authorized',
      '/dashboard',
      '/dashboard/**'
    ],
    gzip: true,
    routes: async () => {
      const axios = require('axios')
      const { data } = await axios.get('https://blog.ordinaryroad.tech/api/blog/common/sitemap')
      return data
    }
  }
}
