import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import App from './App.vue'
import router from './router'
import 'ant-design-vue/dist/reset.css'
import '@vue-flow/core/dist/style.css'
import '@vue-flow/core/dist/theme-default.css'
import './styles/theme.css'
import './styles/scrollbar.css'

const app = createApp(App)

app.use(Antd)
app.use(router)

app.mount('#app')
