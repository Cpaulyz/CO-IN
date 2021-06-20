<template>
  <keep-alive>
    <div class="chatbox">
      <JwChat
        :taleList="taleList"
        @enter="bindEnter"
        v-model="text"
        :toolConfig="tool"
        class="jwchat"
      />
    </div>
  </keep-alive>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'

export default {
  name: 'Chat',
  data() {
    return {
      height: '80vh',
      text: '',
      tool: {
        callback: this.toolEvent
      }
    }
  },
  computed: {
    ...mapGetters(['taleList', 'userInfo'])
  },
  methods: {
    ...mapMutations(['pushTaleList']),
    ...mapActions(['sendQuestionChat']),
    toolEvent(type /* 当前点击的按钮类型 */) {
      alert(type)
    },
    async bindEnter() {
      const data = {
        text: this.text,
        username: this.userInfo.username
      }
      const { reqTale, resTale } = await this.sendQuestionChat(data)
      this.pushTaleList(reqTale)
      this.$nextTick(() => {
        setTimeout(() => {
          this.pushTaleList(resTale)
        }, 1000)
      })
    }
  }
}
</script>

<style>
.chatbox {
  width: 100%;
  height: 100%;
  /* background-color: hsl(207, 100%, 91%); */
  display: flex;
  align-items: center;
  justify-content: center;
}
.chatbox .jwchat {
  width: 70vw !important;
  height: 80vh !important;
}
.wrapper {
  width: 100% !important;
  height: 100% !important;
}
.chatPage[style] {
  border-radius: 20px;
  box-shadow: 1px 1px 1px 1px #454948;
  margin: 5px;
  background-color: #fdfcf8;
}
.web__msg-input[placeholder] {
  background-color: #fffafa;
}
.wrapper .scroller .web__main-user img[data-v-422b11e3] {
  border-radius: 0;
}
/* 超小型设备（电话，600px 及以下） */
@media only screen and (max-width: 600px) {
  .wrapper .scroller[style] {
    width: 95vw;
  }
  .chatbox {
    width: 100%;
    height: 100%;
    /* background-color: hsl(207, 100%, 91%); */
    display: flex;
    align-items: flex-end;
    justify-content: center;
  }
  .chatbox .jwchat {
    height: 88vh !important;
    width: 100vw !important;
    border-radius: 0;
    margin: 0;
  }
}

/* 小型设备（纵向平板电脑和大型手机，600 像素及以上）
@media only screen and (min-width: 600px) {
} */

/* 中型设备（横向平板电脑，768 像素及以上） */
@media only screen and (min-width: 768px) {
  .wrapper .scroller[style] {
    width: calc(100% - 2rem);
    padding: 1rem;
  }
  .chatbox {
    width: 100%;
    height: 100%;
    /* background-color: hsl(207, 100%, 91%); */
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .chatbox .jwchat {
    height: 80vh !important;
  }
}

/* 大型设备（笔记本电脑/台式机，992px 及以上）
@media only screen and (min-width: 992px) {
}

/* 超大型设备（大型笔记本电脑和台式机，1200px 及以上） 
@media only screen and (min-width: 1200px) {
} */
</style>
