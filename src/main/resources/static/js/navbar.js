/**
 * 공통 네비게이션 바 컴포넌트
 * 모든 페이지에서 사용 가능
 */

// 네비게이션 바 HTML 생성
function createNavbar() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const isLoggedIn = !!localStorage.getItem('access_token');
    const isAdmin = user.role === 'ROLE_ADMIN';

    const navbarHTML = `
        <nav class="navbar navbar-expand-lg navbar-dark" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); position: fixed; top: 0; left: 0; right: 0; z-index: 1000;">
            <div class="container">
                <a class="navbar-brand fw-bold" href="/">
                    ☕ Spring Quiz
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarContent">
                    <ul class="navbar-nav ms-auto align-items-center">
                        ${isLoggedIn ? `
                            <li class="nav-item">
                                <a class="nav-link" href="/statistics">📊 통계</a>
                            </li>
                            ${isAdmin ? `
                                <li class="nav-item">
                                    <a class="nav-link" href="/admin">🛠️ 관리자</a>
                                </li>
                            ` : ''}
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                                    👤 ${user.loginId || '사용자'}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><span class="dropdown-item-text"><small>역할: ${user.role === 'ROLE_ADMIN' ? '관리자' : '사용자'}</small></span></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="#" onclick="logout()">로그아웃</a></li>
                                </ul>
                            </li>
                        ` : `
                            <li class="nav-item">
                                <a class="nav-link" href="/login">로그인</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link btn btn-light text-primary px-3 ms-2" href="/signup">회원가입</a>
                            </li>
                        `}
                    </ul>
                </div>
            </div>
        </nav>
        <div style="height: 60px;"></div> <!-- Spacer for fixed navbar -->
    `;

    return navbarHTML;
}

// 네비게이션 바 삽입
function insertNavbar() {
    const body = document.body;
    const navbarDiv = document.createElement('div');
    navbarDiv.innerHTML = createNavbar();
    body.insertBefore(navbarDiv, body.firstChild);
}

// 로그아웃 함수
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        // TODO: 서버에 로그아웃 요청
        // await fetch('/api/users/logout', { method: 'POST' });

        localStorage.removeItem('access_token');
        localStorage.removeItem('user');
        window.location.href = '/';
    }
}

// 페이지 로드 시 자동 삽입 (옵션)
// document.addEventListener('DOMContentLoaded', insertNavbar);

// 전역으로 export
window.createNavbar = createNavbar;
window.insertNavbar = insertNavbar;
window.logout = logout;