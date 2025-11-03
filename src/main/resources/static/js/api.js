// API 베이스 URL
const API_BASE_URL = window.location.hostname === 'localhost'
    ? 'http://localhost:8080'  // 로컬 개발
    : 'https://grius.kro.kr';   // 운영 서버

// API 요청 헬퍼 함수
async function apiRequest(url, options = {}) {
    const token = localStorage.getItem('access_token');

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        }
    };

    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, mergedOptions);

        // 토큰 만료 처리
        if (response.status === 401) {
            const refreshToken = getCookie('refresh_token');
            if (refreshToken) {
                const newToken = await refreshAccessToken(refreshToken);
                if (newToken) {
                    localStorage.setItem('access_token', newToken);
                    // 재시도
                    mergedOptions.headers.Authorization = `Bearer ${newToken}`;
                    return await fetch(`${API_BASE_URL}${url}`, mergedOptions);
                }
            }
            // 리프레시 실패 시 로그인 페이지로
            location.href = '/login';
            return null;
        }

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Unknown error' }));
            throw new Error(error.message || `HTTP ${response.status}`);
        }

        return response;
    } catch (error) {
        console.error('API Request Error:', error);
        throw error;
    }
}

// GET 요청
async function apiGet(url) {
    const response = await apiRequest(url, { method: 'GET' });
    return response ? await response.json() : null;
}

// POST 요청
async function apiPost(url, data) {
    const response = await apiRequest(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
    return response ? await response.json() : null;
}

// PUT 요청
async function apiPut(url, data) {
    const response = await apiRequest(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
    return response ? await response.json() : null;
}

// DELETE 요청
async function apiDelete(url) {
    const response = await apiRequest(url, { method: 'DELETE' });
    return response ? await response.json() : null;
}

// 토큰 리프레시
async function refreshAccessToken(refreshToken) {
    try {
        const response = await fetch('/api/token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('access_token')}`
            },
            body: JSON.stringify({ refreshToken })
        });

        if (response.ok) {
            const result = await response.json();
            return result.accessToken;
        }
        return null;
    } catch (error) {
        console.error('Token refresh failed:', error);
        return null;
    }
}

// 쿠키 가져오기
function getCookie(key) {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === key) {
            return value;
        }
    }
    return null;
}

// 로딩 표시
function showLoading() {
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.id = 'loadingOverlay';
    overlay.innerHTML = `
        <div class="spinner-border text-light" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    `;
    document.body.appendChild(overlay);
}

// 로딩 숨기기
function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

// 토스트 메시지 표시
function showToast(message, type = 'info') {
    const toastContainer = document.querySelector('.toast-container') || createToastContainer();

    const toastId = `toast-${Date.now()}`;
    const bgColor = {
        'success': 'bg-success',
        'error': 'bg-danger',
        'warning': 'bg-warning',
        'info': 'bg-info'
    }[type] || 'bg-info';

    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgColor} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHTML);

    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();

    // 토스트가 숨겨진 후 DOM에서 제거
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// 토스트 컨테이너 생성
function createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

// 에러 처리
function handleError(error, customMessage) {
    console.error('Error:', error);
    const message = customMessage || error.message || '오류가 발생했습니다.';
    showToast(message, 'error');
}

// URL 파라미터 가져오기
function getUrlParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// 날짜 포맷팅
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 숫자 포맷팅
function formatNumber(num) {
    return new Intl.NumberFormat('ko-KR').format(num);
}

// 디바운스 함수
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 로컬 스토리지 헬퍼
const storage = {
    set: (key, value) => {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (error) {
            console.error('Storage set error:', error);
            return false;
        }
    },
    get: (key) => {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (error) {
            console.error('Storage get error:', error);
            return null;
        }
    },
    remove: (key) => {
        localStorage.removeItem(key);
    },
    clear: () => {
        localStorage.clear();
    }
};

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    // 모든 폼에 submit 이벤트 기본 동작 방지 (필요시)
    document.querySelectorAll('form[data-ajax]').forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
        });
    });
});