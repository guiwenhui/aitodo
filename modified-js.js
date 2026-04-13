    <script>
        // DOM元素
        const loadingIndicator = document.getElementById('loadingIndicator');
        const emptyState = document.getElementById('emptyState');
        const warningsList = document.getElementById('warningsList');
        const exampleWarnings = document.getElementById('exampleWarnings');
        const summaryContainer = document.getElementById('summaryContainer');
        const totalTasksEl = document.getElementById('totalTasks');
        const urgentTasksEl = document.getElementById('urgentTasks');
        const delayedTasksEl = document.getElementById('delayedTasks');
        const completedTasksEl = document.getElementById('completedTasks');
        const summaryTextEl = document.getElementById('summaryText');

        // 警告样式映射
        const styleConfig = {
            'urgent_warning': { class: 'urgent', icon: '🚨', label: '紧急' },
            'overdue': { class: 'urgent', icon: '⏰', label: '超时' },
            'delayed_humorous': { class: 'delayed', icon: '😅', label: '拖延' },
            'completed': { class: 'completed', icon: '🎉', label: '已完成' },
            'normal_reminder': { class: 'normal', icon: '📋', label: '正常' },
            'pending_no_deadline': { class: 'normal', icon: '📝', label: '待处理' },
            'unknown': { class: 'normal', icon: '❓', label: '未知' }
        };

        // 格式化时间
        function formatTime(date) {
            const now = new Date();
            const diffMs = now - new Date(date);
            const diffMins = Math.floor(diffMs / 60000);
            const diffHours = Math.floor(diffMs / 3600000);
            const diffDays = Math.floor(diffMs / 86400000);

            if (diffMins < 1) return '刚刚';
            if (diffMins < 60) return `${diffMins}分钟前`;
            if (diffHours < 24) return `${diffHours}小时前`;
            if (diffDays < 7) return `${diffDays}天前`;
            return new Date(date).toLocaleDateString();
        }

        // 加载当前用户的AI警告
        async function loadCurrentUserWarnings() {
            showLoading();
            hideExampleWarnings();

            try {
                const response = await fetch('/task/my-ai-warnings', {
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                if (!response.ok) {
                    // 尝试解析错误信息
                    let errorMsg = `HTTP错误! 状态码: ${response.status}`;
                    try {
                        const errorText = await response.text();
                        if (errorText) errorMsg = errorText.substring(0, 100);
                    } catch (e) {}
                    throw new Error(errorMsg);
                }

                // 检查响应内容类型
                const contentType = response.headers.get('content-type');
                if (!contentType || !contentType.includes('application/json')) {
                    throw new Error('服务器返回的不是JSON格式');
                }

                const data = await response.json();
                displayWarnings(data);
            } catch (error) {
                console.error('加载AI警告失败:', error);
                showError('加载失败：' + error.message);
                hideLoading();
                showExampleWarnings();
            }
        }

        // 显示警告数据
        function displayWarnings(data) {
            hideLoading();

            // 检查是否有警告数据
            if (!data.aiWarnings || data.aiWarnings.length === 0) {
                showEmptyState();
                return;
            }

            // 显示汇总数据
            if (data.totalTasks !== undefined) {
                showSummary(data);
            }

            // 生成警告卡片
            const warningsHTML = data.aiWarnings.map(warning => {
                const style = styleConfig[warning.warningStyle] || styleConfig.unknown;
                const timeText = warning.generatedTime ? formatTime(warning.generatedTime) : '刚刚';

                return `
                    <div class="warning-card ${style.class}">
                        <div class="warning-header">
                            <div class="warning-title">
                                <span class="warning-icon">${style.icon}</span>
                                ${style.label}提醒
                            </div>
                            <span class="warning-badge badge-${style.class}">${style.label}</span>
                        </div>
                        <div class="warning-content ${style.class}">
                            ${warning.aiWarning || 'AI提醒文本'}
                        </div>
                        <div class="warning-meta">
                            <span class="warning-task">任务：${warning.title || '未知任务'}</span>
                            <span class="warning-time">生成时间：${timeText}</span>
                        </div>
                    </div>
                `;
            }).join('');

            warningsList.innerHTML = warningsHTML;
            warningsList.style.display = 'block';
            emptyState.style.display = 'none';
            exampleWarnings.style.display = 'none';
        }

        // 显示汇总数据
        function showSummary(data) {
            summaryContainer.style.display = 'block';
            totalTasksEl.textContent = data.totalTasks || 0;
            urgentTasksEl.textContent = data.urgentTasks || 0;
            delayedTasksEl.textContent = data.delayedTasks || 0;
            completedTasksEl.textContent = data.completedTasks || 0;
            summaryTextEl.textContent = data.summary || '暂无汇总信息';
        }

        // 显示加载状态
        function showLoading() {
            loadingIndicator.style.display = 'block';
            warningsList.style.display = 'none';
            emptyState.style.display = 'none';
            summaryContainer.style.display = 'none';
        }

        // 隐藏加载状态
        function hideLoading() {
            loadingIndicator.style.display = 'none';
        }

        // 显示空状态
        function showEmptyState() {
            warningsList.style.display = 'none';
            emptyState.style.display = 'block';
            summaryContainer.style.display = 'none';
        }

        // 显示错误
        function showError(message) {
            warningsList.innerHTML = `<div class="card" style="background: #ffebee; color: #c62828; padding: 20px; text-align: center;">
                <i class="fas fa-exclamation-triangle"></i> ${message}
            </div>`;
            warningsList.style.display = 'block';
            emptyState.style.display = 'none';
        }

        // 隐藏示例警告
        function hideExampleWarnings() {
            exampleWarnings.style.display = 'none';
        }

        // 显示示例警告
        function showExampleWarnings() {
            exampleWarnings.style.display = 'block';
            warningsList.style.display = 'none';
            emptyState.style.display = 'none';
            summaryContainer.style.display = 'none';
        }

        // 页面加载时自动加载当前用户的警告
        document.addEventListener('DOMContentLoaded', () => {
            // 立即显示加载状态
            showLoading();
            // 开始加载数据
            loadCurrentUserWarnings();
        });
    </script>