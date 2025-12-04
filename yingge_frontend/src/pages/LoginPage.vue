<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ApiError } from '../api/client'
import { login, register } from '../api'

const router = useRouter()

const mode = ref<'login' | 'register'>('login')
const account = ref('')
const password = ref('')
const confirm = ref('')
const loading = ref(false)
const error = ref('')
const nick = ref('')

const submit = async () => {
  if (!account.value || !password.value) {
    error.value = '请输入账号和密码'
    return
  }
  if (mode.value === 'register' && password.value !== confirm.value) {
    error.value = '两次密码不一致'
    return
  }
  loading.value = true
  error.value = ''
  try {
    if (mode.value === 'login') {
      await login(account.value.trim(), password.value)
    } else {
      await register(account.value.trim(), password.value, confirm.value, nick.value || account.value.trim())
    }
    await router.push('/app')
  } catch (err) {
    if (err instanceof ApiError) {
      error.value = err.message
    } else {
      error.value = '请求失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-card">
    <div class="head">
      <p class="eyebrow">登录以开始试穿</p>
      <h1>欢迎回来</h1>
      <p class="sub">使用后端 Session，确保同域或正确的 API 基址</p>
    </div>
    <div class="tabs">
      <button :class="{ active: mode === 'login' }" @click="mode = 'login'">登录</button>
      <button :class="{ active: mode === 'register' }" @click="mode = 'register'">注册</button>
    </div>
    <form class="form" @submit.prevent="submit">
      <label>
        <span>账号</span>
        <input v-model="account" type="text" placeholder="手机号或邮箱" autocomplete="username" />
      </label>
      <label>
        <span>密码</span>
        <input
          v-model="password"
          type="password"
          placeholder="请输入密码"
          autocomplete="current-password"
        />
      </label>
      <label v-if="mode === 'register'">
        <span>昵称（可选）</span>
        <input v-model="nick" type="text" placeholder="昵称，不填默认用账号" />
      </label>
      <label v-if="mode === 'register'">
        <span>确认密码</span>
        <input
          v-model="confirm"
          type="password"
          placeholder="再次输入密码"
          autocomplete="new-password"
        />
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button class="submit" type="submit" :disabled="loading">
        {{ loading ? '处理中...' : mode === 'login' ? '登录' : '注册并登录' }}
      </button>
    </form>
    <p class="hint">默认走后端 Session，请确保浏览器允许 Cookie</p>
  </section>
</template>

<style scoped>
.auth-card {
  max-width: 420px;
  margin: 40px auto;
  padding: 28px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.08);
  border: 1px solid #e5e7eb;
}

.head h1 {
  margin: 6px 0 4px;
  font-size: 24px;
}

.sub,
.eyebrow,
.hint {
  color: #6b7280;
  font-size: 13px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.6px;
}

.tabs {
  display: inline-flex;
  gap: 6px;
  margin: 18px 0 10px;
  padding: 4px;
  border-radius: 10px;
  background: #f3f4f6;
}

.tabs button {
  border: none;
  background: transparent;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  color: #4b5563;
}

.tabs button.active {
  background: #111827;
  color: #fff;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #111827;
}

input {
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  font-size: 15px;
  outline: none;
}

input:focus {
  border-color: #111827;
}

.error {
  color: #b91c1c;
  font-size: 13px;
}

.submit {
  margin-top: 4px;
  padding: 12px;
  border: none;
  border-radius: 10px;
  background: #111827;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
}

.submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.hint {
  margin-top: 10px;
}
</style>
