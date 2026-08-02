@echo off
REM ============================================
REM GitHub Pages 本地部署脚本
REM ============================================
REM 功能：构建前端并推送到 gh-pages 分支
REM 使用：双击运行 deploy-pages.bat
REM 前提：已安装 Node.js 和 Git

echo ============================================
echo    企业仓库管理系统 - GitHub Pages 部署
echo ============================================
echo.

REM 检查 Node.js 是否安装
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Node.js，请先安装 Node.js 18+
    echo 下载地址：https://nodejs.org/
    pause
    exit /b 1
)

REM 检查 Git 是否安装
where git >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Git，请先安装 Git
    echo 下载地址：https://git-scm.com/
    pause
    exit /b 1
)

echo [1/5] 安装前端依赖...
cd /d "%~dp0frontend"
call npm install
if %errorlevel% neq 0 (
    echo [错误] 依赖安装失败
    pause
    exit /b 1
)

echo [2/5] 构建前端（GitHub Pages 模式）...
set VITE_BASE=/Enterprise-Warehouse-Management-System/
call npm run build
if %errorlevel% neq 0 (
    echo [错误] 构建失败
    pause
    exit /b 1
)

echo [3/5] 创建 404.html（SPA 路由支持）...
copy dist\index.html dist\404.html

echo [4/5] 部署到 gh-pages 分支...
cd /d "%~dp0"

REM 保存当前分支名
for /f "delims=" %%i in ('git rev-parse --abbrev-ref HEAD') do set CURRENT_BRANCH=%%i

REM 切换到 gh-pages 分支，如果不存在则创建
git checkout gh-pages 2>nul
if %errorlevel% neq 0 (
    git checkout -b gh-pages
)

REM 清空 gh-pages 分支的旧文件（保留 .git）
for /f "delims=" %%i in ('dir /b /a-d ^| findstr /v ".git"') do del "%%i" 2>nul
for /d /f "delims=" %%i in ('dir /b /ad ^| findstr /v ".git"') do rmdir /s /q "%%i" 2>nul

REM 复制构建产物
xcopy /e /y /i /q frontend\dist\* .\

REM 复制 index.html 到 404.html
copy index.html 404.html

REM 添加 .nojekyll 文件（GitHub Pages 需要）
type nul > .nojekyll

REM 提交并推送
git add .
git commit -m "Deploy to GitHub Pages: %date% %time%"
git push origin gh-pages

REM 切回原分支
git checkout %CURRENT_BRANCH%

echo [5/5] 部署完成！
echo.
echo ============================================
echo    请在 GitHub 仓库 Settings -^> Pages
echo    Source 选择 "Deploy from a branch"
echo    Branch 选择 "gh-pages"
echo    部署地址：
echo    https://zhen237.github.io/Enterprise-Warehouse-Management-System/
echo ============================================
echo.
pause
