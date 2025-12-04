const API_BASE = import.meta.env.VITE_API_BASE ?? ''

type Json = Record<string, unknown> | unknown[]

export class ApiError extends Error {
  status: number
  detail?: string

  constructor(message: string, status: number, detail?: string) {
    super(message)
    this.status = status
    this.detail = detail
  }
}

const parseErrorMessage = async (res: Response) => {
  try {
    const data = await res.json()
    if (data?.message) return String(data.message)
  } catch {
    /* ignore json parse errors */
  }
  try {
    const text = await res.text()
    if (text) return text
  } catch {
    /* ignore text parse errors */
  }
  return `HTTP ${res.status}`
}

export const apiFetch = async <T>(
  path: string,
  init: RequestInit = {},
  parseJson = true,
): Promise<T> => {
  const res = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: {
      ...(init.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
      ...(init.headers || {}),
    },
    ...init,
  })

  if (!res.ok) {
    const message = await parseErrorMessage(res)
    throw new ApiError(message, res.status)
  }

  if (!parseJson) {
    // @ts-expect-error caller promises correct type
    return res.text()
  }
  return (await res.json()) as T
}

export const postJson = <T>(path: string, payload: Json) =>
  apiFetch<T>(path, {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const getJson = <T>(path: string) => apiFetch<T>(path)

export const uploadFile = async (file: File, type: string) => {
  const form = new FormData()
  form.append('file', file)
  form.append('type', type)
  return apiFetch<string>('/api/storage/upload', { method: 'POST', body: form }, false)
}
