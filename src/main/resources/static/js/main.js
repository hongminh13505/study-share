// ============================================
// MAIN JAVASCRIPT
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.style.display = 'none';
            }, 300);
        }, 5000);
    });
    
    // Form validation
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.style.borderColor = 'var(--danger-color)';
                } else {
                    field.style.borderColor = 'var(--border-color)';
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                alert('Vui lòng điền đầy đủ thông tin!');
            }
        });
    });
    
    // File input custom styling
    const fileInputs = document.querySelectorAll('input[type="file"]');
    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            if (this.files.length > 0) {
                const fileName = this.files[0].name;
                const fileSize = (this.files[0].size / (1024 * 1024)).toFixed(2);
                console.log(`File selected: ${fileName} (${fileSize} MB)`);
            }
        });
    });
    
    // Smooth scroll
    const links = document.querySelectorAll('a[href^="#"]');
    links.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href !== '#') {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth'
                    });
                }
            }
        });
    });
    
    // Dropdown menu
    const dropdownToggles = document.querySelectorAll('.dropdown-toggle');
    dropdownToggles.forEach(toggle => {
        toggle.addEventListener('click', function(e) {
            e.preventDefault();
            const dropdown = this.parentElement;
            dropdown.classList.toggle('active');
        });
    });
    
    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.dropdown')) {
            document.querySelectorAll('.dropdown').forEach(dropdown => {
                dropdown.classList.remove('active');
            });
        }
    });
    
    // Table row hover effect
    const tableRows = document.querySelectorAll('tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('click', function() {
            const link = this.querySelector('a');
            if (link && !event.target.closest('button') && !event.target.closest('form')) {
                window.location.href = link.href;
            }
        });
    });
    
    // Search input debounce
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        let timeout;
        input.addEventListener('input', function() {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                console.log('Searching for:', this.value);
            }, 500);
        });
    });
    
    // Confirmation dialogs
    const confirmButtons = document.querySelectorAll('[data-confirm]');
    confirmButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const message = this.getAttribute('data-confirm');
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
    
    // Image lazy loading
    const images = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
                observer.unobserve(img);
            }
        });
    });
    
    images.forEach(img => imageObserver.observe(img));
    
    // Tooltip
    const tooltips = document.querySelectorAll('[data-tooltip]');
    tooltips.forEach(element => {
        element.addEventListener('mouseenter', function() {
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = this.getAttribute('data-tooltip');
            document.body.appendChild(tooltip);
            
            const rect = this.getBoundingClientRect();
            tooltip.style.top = (rect.top - tooltip.offsetHeight - 5) + 'px';
            tooltip.style.left = (rect.left + rect.width / 2 - tooltip.offsetWidth / 2) + 'px';
        });
        
        element.addEventListener('mouseleave', function() {
            const tooltip = document.querySelector('.tooltip');
            if (tooltip) {
                tooltip.remove();
            }
        });
    });
});

// ============================================
// UTILITY FUNCTIONS
// ============================================

// Format number
function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}

// Format date
function formatDate(date) {
    return new Intl.DateTimeFormat('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(new Date(date));
}

// Show toast notification
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 3000);
}

// Copy to clipboard
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showToast('Đã sao chép vào clipboard!', 'success');
    }).catch(err => {
        showToast('Không thể sao chép!', 'error');
    });
}

// Loading overlay
function showLoading() {
    const overlay = document.createElement('div');
    overlay.id = 'loadingOverlay';
    overlay.className = 'loading-overlay';
    overlay.innerHTML = '<div class="spinner"></div>';
    document.body.appendChild(overlay);
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

// AJAX helper
async function fetchData(url, options = {}) {
    try {
        showLoading();
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        showToast('Đã xảy ra lỗi!', 'error');
        return null;
    } finally {
        hideLoading();
    }
}

// ============================================
// NOTIFICATION FUNCTIONS
// ============================================

function toggleNotifications() {
    var dropdown = document.getElementById('notificationDropdown');
    if (dropdown) {
        dropdown.classList.toggle('show');
        // Close user dropdown
        var userDropdown = document.getElementById('userDropdown');
        if (userDropdown) userDropdown.classList.remove('show');
        if (dropdown.classList.contains('show')) {
            loadNotifications();
            // Mark all as read when opening
            markAllAsRead();
        }
    }
}

function loadNotifications() {
    fetch('/api/notifications')
        .then(response => response.json())
        .then(data => {
            updateNotificationBadge(data.unreadCount);
            renderNotifications(data.notifications);
        })
        .catch(err => console.error('Error loading notifications:', err));
}

function updateNotificationBadge(count) {
    var badge = document.getElementById('notificationBadge');
    if (badge) {
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }
}

function renderNotifications(notifications) {
    var list = document.getElementById('notificationList');
    if (!list) return;
    
    if (!notifications || notifications.length === 0) {
        list.innerHTML = '<div class="notification-empty">Không có thông báo</div>';
        return;
    }
    
    // Only show 5 most recent
    var recentNotifications = notifications.slice(0, 5);
    
    list.innerHTML = recentNotifications.map(n => {
        var icon = n.type === 'APPROVED' ? 'fa-check-circle text-success' : 'fa-times-circle text-danger';
        // Extract document name from message
        var docName = n.message.match(/"([^"]+)"/);
        docName = docName ? docName[1] : 'Tài liệu';
        // Truncate if too long
        if (docName.length > 25) docName = docName.substring(0, 25) + '...';
        var statusText = n.type === 'APPROVED' ? 'đã được duyệt' : 'đã bị từ chối';
        return `
            <div class="notification-item">
                <i class="fas ${icon}"></i>
                <div class="notification-content">
                    <p class="notification-text">Tài liệu <b>${docName}</b> ${statusText}</p>
                    <span class="notification-time">${formatNotificationTime(n.createdAt)}</span>
                </div>
            </div>
        `;
    }).join('');
}

function formatNotificationTime(dateStr) {
    var date = new Date(dateStr);
    var now = new Date();
    var diff = Math.floor((now - date) / 1000);
    if (diff < 60) return 'Vừa xong';
    if (diff < 3600) return Math.floor(diff / 60) + ' phút trước';
    if (diff < 86400) return Math.floor(diff / 3600) + ' giờ trước';
    return Math.floor(diff / 86400) + ' ngày trước';
}

function openNotification(id, documentId) {
    fetch('/api/notifications/' + id + '/read', { method: 'POST' })
        .then(() => {
            if (documentId) {
                window.location.href = '/documents/view/' + documentId;
            }
            loadNotifications();
        });
}

function markAllAsRead() {
    fetch('/api/notifications/read-all', { method: 'POST' })
        .then(() => {
            // Hide badge immediately
            var badge = document.getElementById('notificationBadge');
            if (badge) badge.style.display = 'none';
        });
}

// Close notification dropdown when clicking outside
document.addEventListener('click', function(e) {
    if (!e.target.closest('.notification-wrapper')) {
        var notifDropdown = document.getElementById('notificationDropdown');
        if (notifDropdown && notifDropdown.classList.contains('show')) {
            notifDropdown.classList.remove('show');
        }
    }
});

// Load notifications on page load (if user is logged in)
document.addEventListener('DOMContentLoaded', function() {
    var notificationBtn = document.querySelector('.notification-btn');
    if (notificationBtn) {
        loadNotifications();
    }
});

// Export functions
window.formatNumber = formatNumber;
window.formatDate = formatDate;
window.showToast = showToast;
window.copyToClipboard = copyToClipboard;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
window.fetchData = fetchData;
window.toggleNotifications = toggleNotifications;
window.loadNotifications = loadNotifications;
window.openNotification = openNotification;
window.markAllAsRead = markAllAsRead;

