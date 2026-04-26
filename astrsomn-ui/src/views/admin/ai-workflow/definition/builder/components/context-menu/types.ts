export type ContextMenuAction = {
  key: string
  label: string
  icon?: string
  disabled?: boolean
  danger?: boolean
  children?: ContextMenuAction[]
}

export type ContextMenuPosition = {
  x: number
  y: number
}

