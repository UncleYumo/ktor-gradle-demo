document.addEventListener('DOMContentLoaded', () => {
    const videoFrame = document.getElementById('video-frame');
    const noData = document.getElementById('no-data');
    let ws;
    let reconnectAttempts = 0;

    const connect = () => {
        ws = new WebSocket('ws://localhost:8085/cam?key=client');
        ws.binaryType = 'blob';

        ws.onopen = () => {
            reconnectAttempts = 0;
            noData.style.display = 'none';
            console.log('WebSocket connected');
        };

        // 修改后的main.js onmessage处理
        ws.onmessage = (event) => {
            if (event.data instanceof Blob) {
                const reader = new FileReader();
                reader.onload = function() {
                    videoFrame.src = this.result;
                    videoFrame.style.display = 'block';
                    noData.style.display = 'none';
                };
                reader.readAsDataURL(event.data); // 直接处理二进制Blob
            }
        };

        ws.onerror = (error) => {
            console.error('Invalid data format:', error);
            // 添加格式校验逻辑
            if (!event.data.startsWith('data:image/jpeg;base64,')) {
                console.warn('Invalid data format received');
            }
        };

        ws.onclose = () => {
            console.log('WebSocket closed');
            handleError();
        };
    };

    const handleError = () => {
        videoFrame.style.display = 'none';
        noData.style.display = 'flex';
        if (reconnectAttempts < 5) {
            reconnectAttempts++;
            setTimeout(connect, 1000 * reconnectAttempts);
        }
    };

    connect();
});