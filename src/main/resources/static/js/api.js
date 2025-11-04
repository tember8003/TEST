// API 기본 URL
const API_BASE_URL = '/api';

// 쿠키 유틸리티
const CookieUtil = {
    get: function(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    },

    set: function(name, value, days) {
        let expires = '';
        if (days) {
            const date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = `; expires=${date.toUTCString()}`;
        }
        document.cookie = `${name}=${value}${expires}; path=/`;
    },

    delete: function(name) {
        document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
    }
};

// API 호출 헬퍼
const ApiClient = {
    get: async function(endpoint, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = `${API_BASE_URL}${endpoint}${queryString ? '?' + queryString : ''}`;

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    },

    post: async function(endpoint, data = {}) {
        const url = `${API_BASE_URL}${endpoint}`;

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }
};

// 로딩 표시 유틸리티
const LoadingUtil = {
    show: function(message = '로딩 중...') {
        const existingOverlay = document.getElementById('loadingOverlay');
        if (existingOverlay) {
            existingOverlay.remove();
        }

        const overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
        `;

        overlay.innerHTML = `
            <div style="background: white; padding: 30px; border-radius: 15px; text-align: center;">
                <div class="spinner-border text-primary mb-3" role="status"></div>
                <p class="mb-0">${message}</p>
            </div>
        `;

        document.body.appendChild(overlay);
    },

    hide: function() {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.remove();
        }
    }
};

// 알림 유틸리티
const AlertUtil = {
    success: function(message) {
        alert('✓ ' + message);
    },

    error: function(message) {
        alert('✗ ' + message);
    },

    info: function(message) {
        alert('ℹ ' + message);
    }
};

// 날짜 포맷 유틸리티
const DateUtil = {
    format: function(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}`;
    },

    relative: function(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diff = now - date;

        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (days > 0) return `${days}일 전`;
        if (hours > 0) return `${hours}시간 전`;
        if (minutes > 0) return `${minutes}분 전`;
        return '방금 전';
    }
};

// 문제 유틸리티
const ProblemUtil = {
    getCategoryName: function(category) {
        const names = {
            'SPRING_CORE': 'Spring Core',
            'SPRING_BOOT': 'Spring Boot',
            'SPRING_MVC': 'Spring MVC',
            'SPRING_DATA_JPA': 'Spring Data JPA',
            'SPRING_SECURITY': 'Spring Security'
        };
        return names[category] || category;
    },

    getDifficultyName: function(difficulty) {
        const names = {
            'BEGINNER': '초급',
            'INTERMEDIATE': '중급',
            'ADVANCED': '고급'
        };
        return names[difficulty] || difficulty;
    },

    getTypeName: function(type) {
        const names = {
            'MULTIPLE_CHOICE': '객관식',
            'SHORT_ANSWER': '단답형',
            'DESCRIPTIVE': '서술형'
        };
        return names[type] || type;
    },

    getDifficultyColor: function(difficulty) {
        const colors = {
            'BEGINNER': 'success',
            'INTERMEDIATE': 'warning',
            'ADVANCED': 'danger'
        };
        return colors[difficulty] || 'secondary';
    }
};

// Guest ID 관리
const GuestUtil = {
    getId: function() {
        return CookieUtil.get('guest_id');
    },

    hasId: function() {
        return !!this.getId();
    }
};

/*
 현재 로그인한 사용자 프로필 조회
 @returns {Promise<Object|null>} 사용자 프로필 또는 null
 */
async function getCurrentUserProfile() {
    const accessToken = localStorage.getItem('access_token');

    if (!accessToken) {
        return null;
    }

    try {
        const response = await fetch('/api/users/profile', {
            headers: {
                'Authorization': 'Bearer ' + accessToken
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                // 세션 만료
                localStorage.clear();
                window.location.href = '/login';
                return null;
            }
            throw new Error('프로필 로드 실패');
        }

        return await response.json();

    } catch (error) {
        console.error('프로필 조회 오류:', error);
        return null;
    }
}

/*
 현재 로그인한 사용자의 userId 조회
 @returns {Promise<number|null>} userId 또는 null
 */
async function getCurrentUserId() {
    const profile = await getCurrentUserProfile();
    return profile ? profile.userId : null;
}

// 전역으로 export
window.CookieUtil = CookieUtil;
window.ApiClient = ApiClient;
window.LoadingUtil = LoadingUtil;
window.AlertUtil = AlertUtil;
window.DateUtil = DateUtil;
window.ProblemUtil = ProblemUtil;
window.GuestUtil = GuestUtil;
window.getCurrentUserProfile = getCurrentUserProfile;
window.getCurrentUserId = getCurrentUserId;