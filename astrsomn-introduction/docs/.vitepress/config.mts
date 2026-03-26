import { defineConfig } from "vitepress";

export default defineConfig({
  title: "Astrsomn Docs",
  description: "Technical documentation for the Astrsomn project",
  lang: "zh-CN",
  lastUpdated: true,
  cleanUrls: true,
  themeConfig: {
    search: {
      provider: "local"
    },
    socialLinks: [{ icon: "github", link: "https://github.com" }]
  },
  locales: {
    root: {
      label: "简体中文",
      lang: "zh-CN",
      link: "/",
      themeConfig: {
        nav: [
          { text: "官网首页", link: "https://www.astrsomn.org" },
          { text: "项目介绍", link: "/guide/project-overview" },
          { text: "快速开始", link: "/guide/getting-started" }
        ],
        sidebar: [
          {
            text: "文档",
            items: [
              { text: "项目介绍", link: "/guide/project-overview" },
              { text: "快速开始", link: "/guide/getting-started" }
            ]
          }
        ],
        outline: {
          label: "本页目录"
        },
        docFooter: {
          prev: "上一页",
          next: "下一页"
        },
        lastUpdated: {
          text: "最后更新于"
        }
      }
    },
    en: {
      label: "English",
      lang: "en-US",
      link: "/en/",
      themeConfig: {
        nav: [
          { text: "Company Website", link: "https://www.astrsomn.org" },
          { text: "Overview", link: "/en/guide/project-overview" },
          { text: "Getting Started", link: "/en/guide/getting-started" }
        ],
        sidebar: [
          {
            text: "Documentation",
            items: [
              { text: "Overview", link: "/en/guide/project-overview" },
              { text: "Getting Started", link: "/en/guide/getting-started" }
            ]
          }
        ]
      }
    }
  }
});
