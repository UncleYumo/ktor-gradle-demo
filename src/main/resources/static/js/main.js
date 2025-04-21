document.addEventListener('DOMContentLoaded', () => {
    const videoFrame = document.getElementById('video-frame');
    const processingCanvas = document.getElementById('processing-canvas');
    const noData = document.getElementById('no-data');
    const connectionStatus = document.getElementById('connection-status');
    const fpsCounter = document.getElementById('fps-counter');
    const resolution = document.getElementById('resolution');
    const mirrorBtn = document.getElementById('mirror-btn');
    const brightnessRange = document.getElementById('brightness');
    const brightnessValue = document.getElementById('brightness-value');
    const contrastRange = document.getElementById('contrast');
    const contrastValue = document.getElementById('contrast-value');

    let ws;
    let reconnectAttempts = 0;
    let frameCount = 0;
    let lastFpsCheck = Date.now();
    let host = '14.103.207.16:8085';
    let mirrorEnabled = false;
    let brightness = 0;
    let contrast = 100;

    const ctx = processingCanvas.getContext('2d');

    // 初始化控件事件
    mirrorBtn.addEventListener('click', () => {
        mirrorEnabled = !mirrorEnabled;
        mirrorBtn.textContent = mirrorEnabled ? '恢复原向' : '水平翻转';
    });

    brightnessRange.addEventListener('input', (e) => {
        brightness = parseInt(e.target.value);
        brightnessValue.value = brightness;
    });

    brightnessValue.addEventListener('input', (e) => {
        brightness = parseInt(e.target.value);
        brightnessRange.value = brightness;
    });

    contrastRange.addEventListener('input', (e) => {
        contrast = parseInt(e.target.value);
        contrastValue.value = contrast;
    });

    contrastValue.addEventListener('input', (e) => {
        contrast = parseInt(e.target.value);
        contrastRange.value = contrast;
    });

    const connect = () => {
        ws = new WebSocket(`ws://${host}/cam?key=client`);
        ws.binaryType = 'blob';

        ws.onopen = () => {
            reconnectAttempts = 0;
            connectionStatus.className = 'status-connected';
            connectionStatus.textContent = '网络状态：已连接';
            noData.style.display = 'none';
            console.log('WebSocket连接成功');
        };

        ws.onmessage = (event) => {
            if (event.data instanceof Blob) {
                const reader = new FileReader();

                reader.onload = function() {
                    const img = new Image();
                    img.onload = () => {
                        // 处理图像
                        processingCanvas.width = img.width;
                        processingCanvas.height = img.height;

                        // 应用镜像
                        if (mirrorEnabled) {
                            ctx.translate(img.width, 0);
                            ctx.scale(-1, 1);
                        }

                        // 应用亮度和对比度
                        ctx.filter = `brightness(${(brightness + 100)/100}) contrast(${contrast}%)`;

                        ctx.drawImage(img, 0, 0);
                        videoFrame.src = processingCanvas.toDataURL();

                        // 重置变换矩阵
                        ctx.setTransform(1, 0, 0, 1, 0, 0);

                        videoFrame.style.display = 'block';
                        noData.style.display = 'none';

                        // 更新分辨率
                        resolution.textContent = `分辨率：${img.width}x${img.height}`;
                    };

                    img.src = this.result;
                };

                reader.readAsDataURL(event.data);

                // 计算FPS
                frameCount++;
                if (Date.now() - lastFpsCheck >= 1000) {
                    fpsCounter.textContent = `帧率：${frameCount} FPS`;
                    frameCount = 0;
                    lastFpsCheck = Date.now();
                }
            }
        };

        ws.onerror = (error) => {
            console.error('WebSocket错误:', error);
            connectionStatus.className = 'status-disconnected';
            connectionStatus.textContent = '网络状态：连接出错';
        };

        ws.onclose = () => {
            connectionStatus.className = 'status-disconnected';
            connectionStatus.textContent = '网络状态：已断开';
            handleError();
        };
    };

    const handleError = () => {
        videoFrame.style.display = 'none';
        noData.style.display = 'flex';
        fpsCounter.textContent = '帧率：0 FPS';
        resolution.textContent = '分辨率：0x0';

        if (reconnectAttempts < 5) {
            reconnectAttempts++;
            setTimeout(connect, 1000 * reconnectAttempts);
        }
    };

    connect();
});