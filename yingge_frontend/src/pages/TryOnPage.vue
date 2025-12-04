<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import {
  ApiError,
  fetchRecommend,
  fetchRecords,
  fetchTask,
  fetchTemplates,
  submitTryOn,
  uploadFile,
  type TemplateVO,
  type TryOnRecord,
  type TryOnTask,
} from '../api'

type PromptPreset = {
  label: string
  value: string
}

const personUrl = ref('')
const clothUrl = ref('')
const selectedTemplate = ref<TemplateVO | null>(null)
const promptEnabled = ref(false)
const promptText = ref('')
const keyword = ref('')

const promptPresets: PromptPreset[] = [
  {
    label: '英歌舞 · 传统队员',
    value:
      'Chaoshan Yingge folk dance costume, traditional red and black warrior uniform, bold golden embroidery, wide belt, layered shoulder armor details, dynamic yet ceremonial stage outfit',
  },
  {
    label: '英歌舞 · 豪杰领队',
    value:
      'heroic Yingge warrior inspired by Water Margin heroes, heavier armor-like costume, darker red and black palette, metal-like trims, commanding presence, rich historical details',
  },
  {
    label: '英歌舞 · 城市改造',
    value:
      'modern streetwear reinterpretation of Chaoshan Yingge costume, bomber jacket with Yingge patterns, sporty pants, sneakers, neon accent colors, fusion of tradition and urban fashion',
  },
  {
    label: '英歌舞 · 校园联名',
    value:
      'youthful Yingge-inspired casual outfit, varsity jacket with Yingge embroidery, relaxed fit pants, fresh and bright colors, suitable for campus and daily wear',
  },
  {
    label: '英歌舞 · 女将轻甲',
    value:
      'female Yingge warrior inspired outfit, light armor dress silhouette, waist-emphasized tailoring, flowing lower part, auspicious red and gold color scheme, elegant yet powerful',
  },
]

const templatePrompt = computed(() => selectedTemplate.value?.prompt || '')

const templates = ref<TemplateVO[]>([])
const recommend = ref<TemplateVO[]>([])
const records = ref<TryOnRecord[]>([])
const page = ref(1)
const total = ref(0)
const pageSize = 10

const loadingTemplates = ref(false)
const loadingRecommend = ref(false)
const loadingRecords = ref(false)

const personUploading = ref(false)
const clothUploading = ref(false)
const submitLoading = ref(false)
const error = ref('')

const currentTask = ref<TryOnTask | null>(null)
const pollTimer = ref<number | null>(null)

const selectedPresetLabels = ref<string[]>([])

const filteredTemplates = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return templates.value
  return templates.value.filter((t) => {
    const name = (t.name || '').toLowerCase()
    const category = (t.category || '').toLowerCase()
    return name.includes(kw) || category.includes(kw)
  })
})

const presetPrompt = computed(() => {
  const parts: string[] = []
  for (const label of selectedPresetLabels.value) {
    const preset = promptPresets.find((p) => p.label === label)
    if (preset?.value) {
      parts.push(preset.value)
    }
  }
  return parts.join(' ')
})

const promptPreview = computed(() => {
  const parts: string[] = []
  if (templatePrompt.value) {
    parts.push(templatePrompt.value)
  }
  if (presetPrompt.value) {
    parts.push(presetPrompt.value)
  }
  if (promptEnabled.value && promptText.value.trim()) {
    parts.push(promptText.value.trim())
  }
  if (!parts.length) {
    return ''
  }
  return parts.join(' ')
})

const readyForSubmit = computed(() => !!personUrl.value && !!clothUrl.value)

const togglePreset = (label: string) => {
  const list = selectedPresetLabels.value
  const index = list.indexOf(label)
  if (index >= 0) {
    selectedPresetLabels.value = [...list.slice(0, index), ...list.slice(index + 1)]
  } else {
    selectedPresetLabels.value = [...list, label]
  }
}

const loadTemplates = async () => {
  loadingTemplates.value = true
  try {
    const res = await fetchTemplates({ page: 1, size: 20, status: 1 })
    templates.value = res.records
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '获取模板失败'
  } finally {
    loadingTemplates.value = false
  }
}

const loadRecommend = async () => {
  loadingRecommend.value = true
  try {
    recommend.value = await fetchRecommend(10)
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '获取推荐失败'
  } finally {
    loadingRecommend.value = false
  }
}

const loadRecords = async () => {
  loadingRecords.value = true
  try {
    const res = await fetchRecords(page.value, pageSize)
    records.value = res.records
    total.value = res.total
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '获取记录失败'
  } finally {
    loadingRecords.value = false
  }
}

const validateFile = (file: File) => {
  const okType = ['image/jpeg', 'image/png'].includes(file.type)
  if (!okType) throw new Error('仅支持 jpg/png')
  if (file.size > 5 * 1024 * 1024) throw new Error('文件不能超过 5MB')
}

const uploadPerson = async (file: File) => {
  try {
    validateFile(file)
  } catch (e: any) {
    error.value = e?.message || '文件不合法'
    return
  }
  personUploading.value = true
  error.value = ''
  try {
    personUrl.value = await uploadFile(file, 'person')
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '上传人像失败'
  } finally {
    personUploading.value = false
  }
}

const uploadCloth = async (file: File) => {
  try {
    validateFile(file)
  } catch (e: any) {
    error.value = e?.message || '文件不合法'
    return
  }
  clothUploading.value = true
  error.value = ''
  try {
    clothUrl.value = await uploadFile(file, 'cloth')
    selectedTemplate.value = null
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '上传服装失败'
  } finally {
    clothUploading.value = false
  }
}

const selectTemplate = (tpl: TemplateVO) => {
  selectedTemplate.value = tpl
  clothUrl.value = tpl.imageUrl
  promptText.value = tpl.prompt || ''
}

const startPoll = (id: number) => {
  clearPoll()
  pollTimer.value = window.setInterval(async () => {
    try {
      const t = await fetchTask(id)
      currentTask.value = t
      if (t.status === 'done' || t.status === 'failed') {
        clearPoll()
        loadRecords()
      }
    } catch (err) {
      error.value = err instanceof ApiError ? err.message : '轮询失败'
      clearPoll()
    }
  }, 2000)
}

const clearPoll = () => {
  if (pollTimer.value) {
    clearInterval(pollTimer.value)
    pollTimer.value = null
  }
}

const submit = async () => {
  if (!personUrl.value || !clothUrl.value) {
    error.value = '请检查是否已上传人像并选择/上传服装'
    return
  }
  submitLoading.value = true
  error.value = ''
  try {
    const mergedPrompt = promptPreview.value
    const task = await submitTryOn(personUrl.value, clothUrl.value, mergedPrompt)
    currentTask.value = task
    startPoll(task.id)
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '提交失败'
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadTemplates()
  loadRecommend()
  loadRecords()
})

onUnmounted(() => clearPoll())
</script>

<template>
  <div class="layout">
    <section class="hero">
      <div>
        <p class="eyebrow">在线试衣 · 英歌舞</p>
        <h1>上传人像，选择英歌舞服装，提交试装</h1>
        <p class="desc">后端 Session 同域；personUrl + clothUrl + 可选 prompt</p>
        <div class="tags">
          <span>上传校验 ≤5MB</span>
          <span>英歌舞模板</span>
          <span>轮询状态</span>
        </div>
      </div>
    </section>

    <div v-if="error" class="error-banner">{{ error }}</div>

    <div class="columns">
      <div class="main">
        <section class="grid two">
          <div class="card">
            <div class="card-head">
              <div>
                <p class="eyebrow">Step 1</p>
                <h3>上传人像</h3>
              </div>
              <span class="pill" v-if="personUrl">已上传</span>
            </div>
            <div class="upload">
              <input
                id="person"
                type="file"
                accept="image/*"
                :disabled="personUploading"
                @change="(e: Event) => {
                  const files = (e.target as HTMLInputElement).files
                  if (files && files[0]) uploadPerson(files[0])
                }"
              />
              <label class="upload-frame" for="person">
                <template v-if="personUploading">
                  <p>上传中...</p>
                  <small>请稍候</small>
                </template>
                <template v-else-if="personUrl">
                  <img :src="personUrl" alt="person" />
                </template>
                <template v-else>
                  <p>点击或拖拽上传人像</p>
                  <small>支持 jpg/png，≤5MB</small>
                </template>
              </label>
            </div>
          </div>

          <div class="card">
            <div class="card-head">
              <div>
                <p class="eyebrow">Step 2</p>
                <h3>选择服装或自定义上传</h3>
              </div>
              <span class="pill alt" v-if="clothUrl">已选</span>
            </div>
            <div class="upload">
              <input
                id="cloth"
                type="file"
                accept="image/*"
                :disabled="clothUploading"
                @change="(e: Event) => {
                  const files = (e.target as HTMLInputElement).files
                  if (files && files[0]) uploadCloth(files[0])
                }"
              />
              <label class="upload-frame outline" for="cloth">
                <template v-if="clothUploading">
                  <p>上传中...</p>
                  <small>请稍候</small>
                </template>
                <template v-else-if="clothUrl">
                  <img :src="clothUrl" alt="cloth" />
                </template>
                <template v-else>
                  <p>自定义上传服装图</p>
                  <small>不合适可直接上传你的服装图</small>
                </template>
              </label>
            </div>
          </div>
        </section>

        <section class="grid two templates-wrap">
          <div class="card">
            <div class="card-head">
              <div>
                <p class="eyebrow">模板推荐</p>
                <h3>猜你想穿</h3>
              </div>
              <span v-if="loadingRecommend">加载中...</span>
            </div>
            <div class="h-scroll">
              <button
                v-for="tpl in recommend"
                :key="tpl.id"
                class="template"
                :class="{ active: selectedTemplate?.id === tpl.id }"
                @click="selectTemplate(tpl)"
              >
                <img :src="tpl.imageUrl" :alt="tpl.name" />
                <div class="tpl-info">
                  <p class="name">{{ tpl.name }}</p>
                  <small>{{ tpl.category || '推荐' }}</small>
                </div>
              </button>
            </div>
            <p v-if="!recommend.length && !loadingRecommend" class="empty">暂无推荐</p>
          </div>

          <div class="card">
            <div class="card-head">
              <div>
                <p class="eyebrow">模板列表</p>
                <h3>全部可选</h3>
              </div>
              <div class="actions">
                <input
                  v-model="keyword"
                  class="search"
                  type="text"
                  placeholder="搜索名称/类别"
                  aria-label="搜索模板"
                />
                <span v-if="loadingTemplates">加载中...</span>
              </div>
            </div>
            <div class="h-scroll">
              <button
                v-for="tpl in filteredTemplates"
                :key="tpl.id"
                class="template"
                :class="{ active: selectedTemplate?.id === tpl.id }"
                @click="selectTemplate(tpl)"
              >
                <img :src="tpl.imageUrl" :alt="tpl.name" />
                <div class="tpl-info">
                  <p class="name">{{ tpl.name }}</p>
                  <small>{{ tpl.category || '模板' }}</small>
                </div>
              </button>
            </div>
            <p v-if="!filteredTemplates.length && !loadingTemplates" class="empty">暂无模板</p>
          </div>
        </section>

        <section class="card prompt-card">
          <div class="card-head">
            <div>
              <p class="eyebrow">Step 3</p>
              <h3>Prompt（可选）</h3>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="promptEnabled" />
              <span>自定义</span>
            </label>
          </div>
          <div class="presets">
            <p class="muted">快捷预设</p>
            <div class="chips">
              <button
                v-for="preset in promptPresets"
                :key="preset.label"
                type="button"
                :class="{ active: selectedPresetLabels.includes(preset.label) }"
                @click="togglePreset(preset.label)"
              >
                {{ preset.label }}
              </button>
            </div>
          </div>
          <div class="prompt-box">
            <textarea
              v-model="promptText"
              :disabled="!promptEnabled"
              placeholder="开启后可编辑，默认使用模板/后端侧 prompt"
              rows="3"
            />
            <p class="preview-text">将提交的 prompt：{{ promptPreview || '（空）' }}</p>
          </div>
        </section>

        <section class="card">
          <div class="card-head">
            <div>
              <p class="eyebrow">记录</p>
              <h3>最近提交</h3>
            </div>
            <div class="actions">
              <button class="link" @click="loadRecords" :disabled="loadingRecords">
                {{ loadingRecords ? '刷新中...' : '刷新' }}
              </button>
              <span class="muted">共 {{ total }} 条</span>
            </div>
          </div>
          <div class="records">
            <div v-for="rec in records" :key="rec.id" class="record">
              <div class="thumbs">
                <img :src="rec.personImageUrl" alt="person" />
                <img :src="rec.clothImageUrl" alt="cloth" />
              </div>
              <div class="meta">
                <p>#{{ rec.id }} {{ rec.status || 'done' }}</p>
                <small>{{ rec.message || '完成' }}</small>
              </div>
              <a v-if="rec.resultImageUrl" :href="rec.resultImageUrl" target="_blank" rel="noreferrer">结果</a>
            </div>
            <p v-if="!records.length && !loadingRecords" class="empty">暂无记录</p>
          </div>
        </section>
      </div>

      <aside class="aside">
        <div class="card sticky-card glam">
          <div class="panel-head">
            <div>
              <p class="eyebrow">右侧操作</p>
              <h3>试穿快照</h3>
              <p class="subtle">提交前快速核对素材与 prompt</p>
            </div>
            <span class="badge">实时</span>
          </div>

          <div class="glance">
            <div class="glance-item">
              <p class="label">人像</p>
              <div class="mini-frame large" :class="{ empty: !personUrl }">
                <img v-if="personUrl" :src="personUrl" alt="person" />
                <span v-else>未上传</span>
              </div>
            </div>
            <div class="glance-item">
              <p class="label">服装</p>
              <div class="mini-frame large" :class="{ empty: !clothUrl }">
                <img v-if="clothUrl" :src="clothUrl" alt="cloth" />
                <span v-else>未选择</span>
              </div>
            </div>
            <div class="glance-item">
              <p class="label">Prompt</p>
              <p
                class="ellipsis two-lines bubble"
                :title="promptPreview || '未填写（使用模板/默认）'"
              >
                {{ promptPreview || '未填写（使用模板/默认）' }}
              </p>
            </div>
          </div>

          <button class="primary wide" @click="submit" :disabled="submitLoading || !readyForSubmit">
            {{ submitLoading ? '提交中...' : readyForSubmit ? '提交试穿' : '缺少人像/服装' }}
          </button>
          <p class="hint">{{ readyForSubmit ? '请确保已登录' : '需上传人像和服装才可提交' }}</p>

          <div class="divider"></div>

          <div class="status-block">
            <div class="status-head">
              <div>
                <p class="eyebrow">状态</p>
                <h4>任务结果</h4>
              </div>
              <span v-if="currentTask" class="badge small">{{ currentTask.status }}</span>
              <span v-else class="badge small neutral">待提交</span>
            </div>
            <div class="status">
              <p v-if="!currentTask">尚未提交</p>
              <div v-else>
                <p>ID: {{ currentTask.id }}</p>
                <p v-if="currentTask.errorMsg" class="error-text">错误: {{ currentTask.errorMsg }}</p>
                <p v-if="currentTask.resultImageUrl">
                  <a :href="currentTask.resultImageUrl" target="_blank" rel="noreferrer">查看原图</a>
                </p>
                <div v-if="currentTask.resultImageUrl" class="preview framed">
                  <img :src="currentTask.resultImageUrl" alt="result" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 18px 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ecf2ff, #fdf7f2);
  border: 1px solid #e5e7eb;
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #6b7280;
}

.hero h1 {
  margin: 6px 0;
  font-size: 26px;
}

.desc {
  color: #4b5563;
}

.tags {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.tags span {
  background: #0f172a;
  color: #fff;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 12px;
}

.error-banner {
  background: #fef2f2;
  border: 1px solid #fecdd3;
  color: #b91c1c;
  padding: 10px 12px;
  border-radius: 10px;
}

.columns {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.grid.two {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.pill {
  padding: 6px 10px;
  background: #e0f2fe;
  color: #0ea5e9;
  border-radius: 999px;
  font-weight: 700;
  font-size: 12px;
}

.pill.alt {
  background: #ecfdf3;
  color: #16a34a;
}

.upload {
  margin-top: 4px;
}

.upload input[type='file'] {
  display: none;
}

.upload-frame {
  height: 240px;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 6px;
  text-align: center;
  cursor: pointer;
  overflow: hidden;
  padding: 10px;
}

.upload-frame.outline {
  background: #fff;
}

.upload-frame img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 10px;
}

.h-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 6px;
}

.h-scroll::-webkit-scrollbar {
  height: 6px;
}

.h-scroll::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 999px;
}

.template {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  padding: 8px;
  cursor: pointer;
  text-align: left;
  min-width: 150px;
  flex: 0 0 150px;
}

.template.active {
  border-color: #0f172a;
  box-shadow: 0 0 0 2px #0f172a inset;
}

.template img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
}

.tpl-info {
  margin-top: 6px;
}

.tpl-info .name {
  font-weight: 700;
}

.tpl-info small {
  color: #6b7280;
}

.prompt-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.prompt-box textarea {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  font-size: 14px;
  min-height: 90px;
  resize: vertical;
}

.prompt-box textarea:disabled {
  background: #f8fafc;
  color: #94a3b8;
}

.presets .muted,
.preview-text,
.hint,
.muted {
  color: #6b7280;
}

.preview-text {
  margin-top: 6px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 6px;
}

.chips button {
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  border-radius: 10px;
  padding: 6px 10px;
  cursor: pointer;
}

.chips button.active {
  background: #0f172a;
  color: #fff;
  border-color: #0f172a;
}

.chips button:hover {
  border-color: #0f172a;
}

.switch {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
}

.status {
  min-height: 160px;
}

.status .preview {
  max-height: 320px;
}

.status .preview img {
  max-height: 320px;
  object-fit: contain;
}

.records {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.record {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.thumbs {
  display: grid;
  grid-template-columns: repeat(2, 56px);
  gap: 4px;
}

.thumbs img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
}

.meta small {
  color: #6b7280;
}

.empty {
  color: #94a3b8;
}

.error-text {
  color: #b91c1c;
}

.link {
  background: transparent;
  border: none;
  color: #0f172a;
  cursor: pointer;
  font-weight: 700;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search {
  padding: 8px 10px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  min-width: 140px;
}

.primary {
  padding: 12px 18px;
  border: none;
  border-radius: 12px;
  background: #0f172a;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
}

.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.primary.wide {
  width: 100%;
}

.aside {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.sticky-card {
  position: sticky;
  top: 12px;
}

.glam {
  background: linear-gradient(145deg, #ffffff, #f9fafb);
  color: #0f172a;
  border: 1px solid #e5e7eb;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.panel-head h3 {
  color: #0f172a;
}

.panel-head .subtle {
  color: #6b7280;
  margin-top: 2px;
}

.badge {
  padding: 6px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-weight: 700;
  font-size: 12px;
}

.badge.neutral {
  background: #e5e7eb;
  color: #374151;
}

.badge.small {
  padding: 4px 8px;
}

.summary {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.glance {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 10px;
}

.glance-item .label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.mini-frame {
  flex: 1;
  height: 80px;
  border: 1px dashed #cbd5e1;
  border-radius: 10px;
  background: #f9fafb;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 6px;
  text-align: center;
}

.mini-frame img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.mini-frame.empty {
  color: #9ca3af;
}

.mini-frame.large {
  height: 120px;
}

.ellipsis {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ellipsis.two-lines {
  white-space: normal;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

.bubble {
  padding: 8px 10px;
  border-radius: 10px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 10px 0;
}

.status-block h4 {
  margin: 0;
  color: #0f172a;
}

.status-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.status .preview {
  max-height: 280px;
}

.status .preview img {
  max-height: 280px;
}

.status .preview.framed {
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

@media (max-width: 900px) {
  .columns {
    grid-template-columns: 1fr;
  }
  .grid.two {
    grid-template-columns: 1fr;
  }
  .sticky-card {
    position: static;
  }
}
</style>
