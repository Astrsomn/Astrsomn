import { loadLayoutFromConfig, getLayoutConfigMetadata } from './config'

export function exampleUsage() {
  console.log('=== 控制台布局配置示例 ===')
  
  const metadata = getLayoutConfigMetadata()
  console.log('配置元数据:', metadata)
  
  const layoutItems = loadLayoutFromConfig()
  console.log('布局项数量:', layoutItems.length)
  
  layoutItems.forEach((item, index) => {
    console.log(`模块 ${index + 1}:`, {
      kind: item.kind,
      position: `(${item.x}, ${item.y})`,
      size: `${item.w}x${item.h}`,
      route: 'route' in item ? item.route : 'N/A',
    })
  })
  
  return layoutItems
}

export function integrateWithExistingStorage() {
  const configItems = loadLayoutFromConfig()
  
  console.log('配置加载完成，可以替换现有的默认布局生成逻辑')
  console.log('建议在 dashboardLayoutStorage.ts 中使用此配置')
  
  return configItems
}
