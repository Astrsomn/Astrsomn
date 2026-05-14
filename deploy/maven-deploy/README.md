既然是发布到 Maven 中央仓库 (Maven Central)，并且已经完成了域名验证（即 Sonatype JIRA 工单状态已变为
RESOLVED），接下来的步骤与发布到普通私服有很大区别。

发布到中央仓库不仅要求配置正确，还有严格的构件要求（必须包含源码、Javadoc 和 GPG 签名）。以下是完整的操作流程：

配置 Maven 认证信息（settings.xml）
打开你本地 Maven 的 settings.xml 文件，在 标签内添加 Sonatype 的账号密码（即你注册 JIRA 时用的账号密码）：

    ossrh 
    你的Sonatype账号
    你的Sonatype密码

完善项目的 POM 配置（pom.xml）
Maven 中央仓库对发布的项目有严格要求，你的 pom.xml 必须包含以下核心配置：

项目元信息：必须包含 name、description、url、licenses（开源协议）、developers（开发者信息）和 scm（源码管理地址）。
生成源码和 Javadoc：必须配置 maven-source-plugin 和 maven-javadoc-plugin。
GPG 签名：中央仓库强制要求所有构件必须经过 GPG 签名。你需要先安装 GPG 工具（Windows 推荐 Gpg4win），生成密钥对，并将公钥上传到密钥服务器（如
hkp://keyserver.ubuntu.com:80）。然后在 pom.xml 中配置 maven-gpg-plugin。
发布地址：配置 distributionManagement 指向 Sonatype 的快照和发布仓库。

pom.xml 核心配置参考：

你的项目名称
项目描述
你的项目主页地址（如 GitHub/Gitee 仓库地址）

      The Apache Software License, Version 2.0
      http://www.apache.org/licenses/LICENSE-2.0.txt
    
  
  
    
      你的名字
      你的邮箱
    
  
  
    scm:git:你的git仓库地址.git
    你的git仓库地址
  

  
  
    
      ossrh 
      https://s01.oss.sonatype.org/content/repositories/snapshots
    
    
      ossrh
      https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
    
  

  
    
      
      
        org.apache.maven.plugins
        maven-source-plugin
        2.2.1
        
          
            attach-sources
            jar-no-fork
          
        
      
      
      
        org.apache.maven.plugins
        maven-javadoc-plugin
        2.9.1
        
          
            attach-javadocs
            jar
          
        
      
      
      
        org.apache.maven.plugins
        maven-gpg-plugin
        1.5
        
          
            sign-artifacts
            verify
            sign

执行部署命令
在项目根目录下打开终端，执行以下命令：
mvn clean deploy

如果你的项目版本号是 x.x.x-SNAPSHOT，构件会被直接发布到 Snapshots 仓库，不需要人工审核，几分钟内即可使用。
如果你的项目版本号是 x.x.x（正式版），构件会被推送到 Staging（暂存）仓库，需要你手动去网页端操作发布。执行过程中，插件会弹窗或提示你输入
GPG 密钥的密码。

登录 Sonatype 网页端操作（仅限正式版 Release）
如果你发布的是正式版（非 SNAPSHOT），命令执行成功后，还需要进行以下操作：
登录 Sonatype OSSRH 网页端（使用你的 JIRA 账号密码）。
在左侧菜单栏点击 Staging Repositories。
在列表中找到你刚刚上传的仓库（可以根据上传时间或 Owner 来筛选）。
选中该仓库，点击上方的 Close 按钮。系统会自动校验你的构件（包括 GPG 签名、Javadoc 等是否合规）。
如果校验通过，仓库状态会变为 Closed。此时再次选中它，点击 Release 按钮，确认发布。

验证与同步
点击 Release 后，你可以回到之前的 JIRA 工单下评论一句“已发布第一个版本”。通常等待 2-4 个小时左右，你的 JAR 包就会自动同步到
Maven 中央仓库，届时全球开发者都可以通过 Maven 坐标直接引用你的项目了。