const BASE_URL = 'http://localhost:8080';

// --- 리다이렉트 제외 경로 ---
// 이 경로들에서 401/403이 와도 자동 리다이렉트하지 않음
// (호출한 쪽에서 직접 에러를 처리해야 하는 API)
const SKIP_REDIRECT_PATHS = ['/api/auth/'];

function shouldSkipRedirect(path: string): boolean {
  return SKIP_REDIRECT_PATHS.some((prefix) => path.startsWith(prefix));
}

// --- 공통 에러 처리 ---
function handleErrorResponse(res: Response, path: string): void {
  if (shouldSkipRedirect(path)) return;

  if (res.status === 401) {
    window.location.href = '/login';
  }

  if (res.status === 403) {
    window.location.href = '/locked';
  }
}

// --- API 응답 타입 ---
export interface ApiResponse<T = unknown> {
  ok: boolean;
  status: number;
  data: T | null;
  message: string | null;
}

// --- 응답 파싱 ---
async function parseResponse<T>(res: Response): Promise<ApiResponse<T>> {
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (res.ok) {
    return { ok: true, status: res.status, data, message: null };
  }

  return {
    ok: false,
    status: res.status,
    data: null,
    message: data?.message ?? '알 수 없는 오류가 발생했습니다.',
  };
}

// --- 핵심 요청 함수 ---
async function request<T>(path: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
  const res = await fetch(`${BASE_URL}${path}`, {
    credentials: 'include',
    ...options,
  });

  handleErrorResponse(res, path);

  return parseResponse<T>(res);
}

// --- HTTP 메서드 헬퍼 ---
export const api = {
  get<T>(path: string): Promise<ApiResponse<T>> {
    return request<T>(path);
  },

  post<T>(path: string, body?: unknown): Promise<ApiResponse<T>> {
    return request<T>(path, {
      method: 'POST',
      ...(body && {
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      }),
    });
  },

  put<T>(path: string, body?: unknown): Promise<ApiResponse<T>> {
    return request<T>(path, {
      method: 'PUT',
      ...(body && {
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      }),
    });
  },

  delete<T>(path: string): Promise<ApiResponse<T>> {
    return request<T>(path, { method: 'DELETE' });
  },
};
