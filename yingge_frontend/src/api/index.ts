import { apiFetch, getJson, postJson, uploadFile } from './client'
export { ApiError } from './client'

export type TemplateVO = {
  id: number
  name: string
  imageUrl: string
  maskUrl?: string
  category?: string
  styleTags?: string
  color?: string
  fit?: string
  status?: number
  sort?: number
  prompt?: string
  negativePrompt?: string
  strength?: number
  aspectRatio?: string
}

export type PageResult<T> = {
  records: T[]
  total: number
  current: number
  size: number
}

export type TryOnTask = {
  id: number
  personImageUrl: string
  clothImageUrl: string
  resultImageUrl?: string
  status: string
  errorMsg?: string
  prompt?: string
  createTime?: string
  updateTime?: string
}

export type TryOnRecord = {
  id: number
  personImageUrl: string
  clothImageUrl: string
  resultImageUrl?: string
  status?: string
  message?: string
  createTime?: string
}

export type UserVO = {
  id: number
  userAccount: string
  userName?: string
  userAvatar?: string
  userRole?: string
}

export const login = (account: string, password: string) =>
  postJson<UserVO>('/api/users/login', { userAccount: account, userPassword: password })

export const register = (
  account: string,
  password: string,
  confirmPassword: string,
  userName?: string,
) =>
  postJson<UserVO>('/api/users/register', {
    userAccount: account,
    userPassword: password,
    checkPassword: confirmPassword,
    ...(userName ? { userName } : {}),
  })

export const fetchTemplates = (params: {
  page?: number
  size?: number
  category?: string
  style?: string
  status?: number
}) => {
  const query = new URLSearchParams()
  if (params.page) query.set('page', String(params.page))
  if (params.size) query.set('size', String(params.size))
  if (params.category) query.set('category', params.category)
  if (params.style) query.set('style', params.style)
  if (params.status != null) query.set('status', String(params.status))
  return getJson<PageResult<TemplateVO>>(`/api/cloth/templates?${query.toString()}`)
}

export const fetchRecommend = (limit?: number) => {
  const query = limit ? `?limit=${limit}` : ''
  return getJson<TemplateVO[]>(`/api/cloth/recommend${query}`)
}

export const submitTryOn = (personUrl: string, clothUrl: string, prompt?: string) =>
  apiFetch<TryOnTask>('/api/tasks/tryon', {
    method: 'POST',
    body: new URLSearchParams({
      personUrl,
      clothUrl,
      ...(prompt ? { prompt } : {}),
    }),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })

export const fetchTask = (id: number) => getJson<TryOnTask>(`/api/tasks/tryon/${id}`)

export const fetchRecords = (page = 1, size = 10) =>
  getJson<PageResult<TryOnRecord>>(`/api/records?page=${page}&size=${size}`)

export { uploadFile }
