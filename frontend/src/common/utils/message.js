import { Notification, MessageBox, Message } from 'element-ui'

// 通知框
export const $notify = ({
  title = '通知 Title',
  type = 'info',
  message = '',
  duration = 3000
} = {}) => {
  Notification({
    title,
    duration,
    message,
    type,
    position: 'bottom-left'
  })
}

// 确认框
export const $confirm = ({
  title = '确认框 Title',
  message = '确认框 Message',
  type = 'info',
  confirmText = '确定',
  cancelText = '再想想'
}) => {
  return MessageBox.confirm(message, title, {
    confirmButtonText: confirmText,
    cancelButtonText: cancelText,
    distinguishCancelAndClose: true,
    type
  })
    .then(confirm => confirm) // 'confirm'
    .catch(cancel => cancel) // 'cancel'(不保存) | 'close'(取消)
}

// 消息提示
export const $message = (message, type = 'info') => {
  Message[type]({ message })
}
// 组输出
export const consoleGroup = function(name, cb) {
  console.group(name)
  cb()
  console.groupEnd(name)
}
