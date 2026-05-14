# AITodo (AI-Todo-Procrastination-Cure)

AITodo 是一款旨在解决拖延症的智能待办事项管理应用。通过结合专注模式、排行榜竞争机制以及直观的数据可视化，帮助用户更高效地管理任务并保持专注。

## ✨ 功能特性

* **智能任务管理**：支持灵活的任务创建、编辑与状态管理，并支持拖拽排序（基于 `vuedraggable`）。
* **专注模式 (Focus Mode)**：沉浸式的工作流体验，帮助用户在特定时间内保持高效。
* **积分排行榜 (Leaderboard)**：引入游戏化机制，通过排行榜激发用户的积极性。
* **数据可视化看板**：内置丰富的数据图表（基于 `ECharts`），直观展示任务完成情况与效率统计。
* **极简 UI 设计**：采用清爽、高颜值的界面风格，带来极致的用户体验。

## 🛠️ 技术栈

本项目前端基于时下主流的现代化技术栈构建：

* **核心框架**：[Vue 3](https://vuejs.org/) (Composition API)
* **构建工具**：[Vite](https://vitejs.dev/) - 提供极速的冷启动与热更新
* **路由管理**：[Vue Router 4](https://router.vuejs.org/)
* **网络请求**：[Axios](https://axios-http.com/)
* **数据可视化**：[ECharts 6](https://echarts.apache.org/)
* **拖拽组件**：[Vue.Draggable](https://github.com/SortableJS/vue.draggable.next)

## 🚀 快速开始

请确保您的本地环境已安装 [Node.js](https://nodejs.org/)。

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd aitodo

2. 安装依赖
npm install

3. 本地开发运行
npm run dev

4. 生产环境构建
npm run build

5. 本地预览构建产物
npm run preview
