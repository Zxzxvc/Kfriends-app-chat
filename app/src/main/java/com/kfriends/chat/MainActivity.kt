package com.kfriends.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraPhotoPath: String? = null
    
    companion object {
        private const val REQUEST_SELECT_FILE = 100
        private const val REQUEST_PERMISSION_CODE = 101
        private const val CAMERA_REQUEST = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        webView = findViewById(R.id.webView)
        
        // Request permissions
        requestRequiredPermissions()
        
        // Configure WebView
        configureWebView()
        
        // Load HTML content
        loadWebContent()
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }
        
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    private fun configureWebView() {
        webView.apply {
            // WebViewClient
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                        return false
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }
            }
            
            // WebChromeClient for file picker
            webChromeClient = CustomWebChromeClient()
            
            // Settings
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                
                // Enable localStorage
                databasePath = applicationContext.getDir("databases", MODE_PRIVATE).path
                
                // User agent
                userAgentString = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) AppleWebKit/537.36"
                
                // Dark mode support (API 29+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                        WebSettingsCompat.setForceDark(this, WebSettingsCompat.FORCE_DARK_ON)
                    }
                }
            }
            
            // Enable debugging in development
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
    }

    private fun loadWebContent() {
        val htmlContent = getHtmlContent()
        webView.loadDataWithBaseURL(
            "file:///android_asset/",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun getHtmlContent(): String {
        // Read the HTML file from assets or use inline HTML
        return try {
            assets.open("index.html").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            // Fallback: use inline HTML (you can paste the full HTML here)
            getInlineHtmlContent()
        }
    }

    private fun getInlineHtmlContent(): String {
        // This is the full HTML content from the user's file
        return """
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Kfriends Chat</title>
    
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: #0a1014;
            color: #e9edef;
            height: 100vh;
            overflow: hidden;
            -webkit-tap-highlight-color: transparent;
            -webkit-user-select: none;
            user-select: none;
        }
        
        #chatsListPage {
            display: flex;
            flex-direction: column;
            height: 100vh;
        }
        
        #chatViewPage {
            display: none;
            flex-direction: column;
            height: 100vh;
            position: relative;
        }
        
        .header {
            background: #202c33;
            padding: 10px 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            min-height: 56px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.4);
            z-index: 100;
        }
        
        .header h1 {
            font-size: 20px;
            font-weight: 500;
            color: #e9edef;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .back-btn {
            background: none;
            border: none;
            color: #00a884;
            font-size: 24px;
            cursor: pointer;
            padding: 5px 15px;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .back-btn:active {
            background: rgba(255,255,255,0.1);
        }
        
        .chats-list {
            flex: 1;
            overflow-y: auto;
            background: #111b21;
            -webkit-overflow-scrolling: touch;
        }
        
        .chat-item {
            display: flex;
            align-items: center;
            padding: 14px 16px;
            border-bottom: 1px solid #202c33;
            cursor: pointer;
            transition: background 0.15s;
        }
        
        .chat-item:active {
            background: #2a3942;
        }
        
        .chat-avatar {
            width: 52px;
            height: 52px;
            background: linear-gradient(135deg, #00a884, #005c4b);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            font-weight: 600;
            color: white;
            margin-left: 15px;
            flex-shrink: 0;
        }
        
        .chat-info {
            flex: 1;
            min-width: 0;
        }
        
        .chat-name {
            font-size: 17px;
            font-weight: 500;
            margin-bottom: 4px;
        }
        
        .chat-preview {
            font-size: 14px;
            color: #8696a0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .chat-meta {
            text-align: left;
            margin-right: 10px;
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 5px;
        }
        
        .chat-date {
            font-size: 12px;
            color: #8696a0;
        }
        
        .message-count-badge {
            background: #00a884;
            color: white;
            border-radius: 12px;
            padding: 2px 8px;
            font-size: 11px;
            font-weight: 600;
            min-width: 20px;
            text-align: center;
        }
        
        .chat-messages {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
            background: #0b141a;
            background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAOxAAADsQBlSsOGwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAA5SURBVGje7dAxEQAACMMw8L9qyYIBBthBZWVlZWVlZWVlZWVlZWVlZWVlZWVlZWVlZWVlZWVlZWUFYOAHRRIKCqs7WJAAAAAASUVORK5CYII=');
            -webkit-overflow-scrolling: touch;
        }
        
        .message-container {
            display: flex;
            margin-bottom: 4px;
        }
        
        .message-container.me {
            justify-content: flex-end;
        }
        
        .message-bubble {
            max-width: 80%;
            padding: 6px 8px 6px 8px;
            border-radius: 8px;
            position: relative;
            word-wrap: break-word;
            box-shadow: 0 1px 1px rgba(0,0,0,0.1);
            font-size: 14.5px;
            line-height: 1.4;
        }
        
        .message-container.me .message-bubble {
            background: #005c4b;
            border-top-right-radius: 2px;
        }
        
        .message-container.other .message-bubble {
            background: #202c33;
            border-top-left-radius: 2px;
        }
        
        .sender-name {
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 2px;
        }
        
        .message-container.me .sender-name {
            color: #7bffdb;
        }
        
        .message-container.other .sender-name {
            color: #06cf9c;
        }
        
        .message-text {
            color: #e9edef;
            word-break: break-word;
        }
        
        .message-time {
            font-size: 11px;
            color: rgba(255,255,255,0.4);
            text-align: right;
            margin-top: 3px;
            float: left;
            margin-right: 5px;
        }
        
        .empty-state {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            color: #8696a0;
            text-align: center;
            padding: 20px;
        }
        
        .empty-state-icon {
            font-size: 80px;
            margin-bottom: 20px;
            opacity: 0.8;
        }
        
        .empty-state-title {
            font-size: 22px;
            margin-bottom: 10px;
            font-weight: 500;
        }
        
        .empty-state-subtitle {
            font-size: 14px;
            color: #6a7a84;
            margin-bottom: 20px;
        }
        
        .btn {
            background: #00a884;
            color: white;
            border: none;
            padding: 14px 35px;
            border-radius: 25px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            margin: 8px;
            font-family: inherit;
            transition: background 0.15s;
            box-shadow: 0 2px 8px rgba(0,168,132,0.3);
        }
        
        .btn:active {
            background: #008f6f;
            transform: scale(0.98);
        }
        
        .btn-secondary {
            background: #1f2c34;
            box-shadow: 0 2px 8px rgba(0,0,0,0.3);
        }
        
        .btn-secondary:active {
            background: #2a3942;
        }
        
        .btn-small {
            padding: 8px 20px;
            font-size: 13px;
            margin: 4px;
        }
        
        .loading {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            font-size: 16px;
            color: #8696a0;
        }
        
        .loading-spinner {
            width: 40px;
            height: 40px;
            border: 3px solid #202c33;
            border-top: 3px solid #00a884;
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
            margin-bottom: 20px;
        }
        
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        
        ::-webkit-scrollbar {
            width: 5px;
        }
        
        ::-webkit-scrollbar-track {
            background: transparent;
        }
        
        ::-webkit-scrollbar-thumb {
            background: #374045;
            border-radius: 3px;
        }
        
        .date-separator {
            text-align: center;
            margin: 15px 0;
        }
        
        .date-separator span {
            background: #182229;
            color: #8696a0;
            padding: 5px 12px;
            border-radius: 8px;
            font-size: 12px;
            box-shadow: 0 1px 1px rgba(0,0,0,0.3);
        }
        
        .header-actions {
            display: flex;
            gap: 5px;
        }
        
        .scroll-buttons {
            position: absolute;
            bottom: 20px;
            right: 20px;
            display: flex;
            flex-direction: column;
            gap: 10px;
            z-index: 50;
        }
        
        .scroll-btn {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            background: #202c33;
            border: none;
            color: #00a884;
            font-size: 22px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.5);
            transition: all 0.2s;
        }
        
        .scroll-btn:active {
            background: #005c4b;
            transform: scale(0.9);
        }
        
        .scroll-btn-top {
            opacity: 0.7;
        }
        
        .scroll-btn-bottom {
            opacity: 0.9;
        }
        
        .message-counter {
            position: absolute;
            bottom: 120px;
            right: 27px;
            background: #202c33;
            color: #8696a0;
            padding: 4px 10px;
            border-radius: 15px;
            font-size: 11px;
            z-index: 50;
            box-shadow: 0 2px 10px rgba(0,0,0,0.5);
        }
    </style>
</head>
<body>

    <div id="chatsListPage">
        <div class="header">
            <h1>💬 Kfriends Chat</h1>
            <div class="header-actions">
                <button class="btn btn-small" style="margin:0;" onclick="pickMultipleFiles()">📂 إضافة</button>
                <button class="btn btn-small btn-secondary" style="margin:0;" onclick="clearAllChats()">🗑️</button>
            </div>
        </div>
        <div class="chats-list" id="chatsList">
            <div class="loading">
                <div class="loading-spinner"></div>
                <p>جاري التحميل...</p>
            </div>
        </div>
    </div>

    <div id="chatViewPage">
        <div class="header">
            <button class="back-btn" onclick="goBackToChatsList()">⬅️</button>
            <h1 id="currentChatName" style="flex:1; text-align:center;">محادثة</h1>
            <div style="width:40px;"></div>
        </div>
        <div class="chat-messages" id="chatMessages"></div>
        
        <div class="scroll-buttons" id="scrollButtons" style="display:none;">
            <button class="scroll-btn scroll-btn-top" onclick="scrollToTop()" title="الأعلى">⬆️</button>
            <button class="scroll-btn scroll-btn-bottom" onclick="scrollToBottom()" title="الأسفل">⬇️</button>
        </div>
        <div class="message-counter" id="messageCounter" style="display:none;"></div>
    </div>

    <input type="file" id="filePicker" accept=".json" style="display: none;" onchange="handleFileSelection(event)">
    <input type="file" id="multiFilePicker" multiple accept=".json" style="display: none;" onchange="handleMultipleFiles(event)">

    <script>
        const MY_ID = "YIBbpV5P_us";
        let allChats = [];
        let currentChat = null;
        let savedChats = {};
        let scrollInterval = null;
        
        function initApp() {
            const saved = localStorage.getItem('kfriends_chats');
            if (saved) {
                try {
                    savedChats = JSON.parse(saved);
                    const chats = Object.values(savedChats);
                    if (chats.length > 0) {
                        allChats = chats;
                        displayChatsList(chats);
                        return;
                    }
                } catch (e) {
                    localStorage.removeItem('kfriends_chats');
                }
            }
            showWelcomeScreen();
        }
        
        function showWelcomeScreen() {
            document.getElementById('chatsList').innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">💬</div>
                    <div class="empty-state-title">مرحباً بك في Kfriends Chat</div>
                    <div class="empty-state-subtitle">اختر ملفات JSON لعرض المحادثات</div>
                    <button class="btn" onclick="pickMultipleFiles()">📂 اختيار ملفات JSON</button>
                    <div class="empty-state-subtitle" style="margin-top:15px; font-size:12px;">يعمل بدون إنترنت</div>
                </div>
            `;
        }
        
        function pickMultipleFiles() {
            document.getElementById('multiFilePicker').click();
        }
        
        function handleMultipleFiles(event) {
            const files = event.target.files;
            if (!files || files.length === 0) return;
            
            document.getElementById('chatsList').innerHTML = `
                <div class="loading">
                    <div class="loading-spinner"></div>
                    <p>جاري معالجة ${'${files.length}'} ملف...</p>
                </div>
            `;
            
            let loaded = 0;
            const total = files.length;
            
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                const reader = new FileReader();
                
                reader.onload = (function(fileName) {
                    return function(e) {
                        try {
                            const data = JSON.parse(e.target.result);
                            
                            if (!data.messages || !Array.isArray(data.messages)) {
                                console.error('Invalid file: ' + fileName);
                                loaded++;
                                if (loaded === total) {
                                    localStorage.setItem('kfriends_chats', JSON.stringify(savedChats));
                                    allChats = Object.values(savedChats);
                                    displayChatsList(allChats);
                                }
                                return;
                            }
                            
                            const chat = createChatObject(fileName, data);
                            savedChats[fileName] = chat;
                        } catch (error) {
                            console.error('Error parsing file: ' + fileName);
                        }
                        
                        loaded++;
                        if (loaded === total) {
                            localStorage.setItem('kfriends_chats', JSON.stringify(savedChats));
                            allChats = Object.values(savedChats);
                            displayChatsList(allChats);
                        }
                    };
                })(file.name);
                
                reader.readAsText(file);
            }
        }
        
        function handleFileSelection(event) {
            const file = event.target.files[0];
            if (!file) return;
            
            const reader = new FileReader();
            
            reader.onload = function(e) {
                try {
                    const data = JSON.parse(e.target.result);
                    
                    if (!data.messages || !Array.isArray(data.messages)) {
                        alert('الملف لا يحتوي على رسائل صالحة');
                        return;
                    }
                    
                    const chat = createChatObject(file.name, data);
                    savedChats[file.name] = chat;
                    localStorage.setItem('kfriends_chats', JSON.stringify(savedChats));
                    
                    allChats = Object.values(savedChats);
                    displayChatsList(allChats);
                    openChatDirectly(chat);
                    
                } catch (error) {
                    alert('خطأ في قراءة الملف: ' + error.message);
                }
            };
            
            reader.readAsText(file);
        }
        
        function createChatObject(fileName, data) {
            const lastMsg = data.messages[data.messages.length - 1];
            let preview = 'لا توجد رسائل';
            
            if (lastMsg) {
                preview = lastMsg.message || '📎 وسائط';
                if (preview.length > 50) {
                    preview = preview.substring(0, 50) + '...';
                }
            }
            
            return {
                file: fileName,
                data: data,
                name: data.roomId || fileName.replace('.json', ''),
                preview: preview,
                messageCount: data.messages.length,
                lastTime: lastMsg ? lastMsg.time : null
            };
        }
        
        function displayChatsList(chats) {
            const container = document.getElementById('chatsList');
            
            if (chats.length === 0) {
                showWelcomeScreen();
                return;
            }
            
            const sortedChats = [...chats].sort((a, b) => {
                if (!a.lastTime) return 1;
                if (!b.lastTime) return -1;
                return new Date(b.lastTime) - new Date(a.lastTime);
            });
            
            container.innerHTML = sortedChats.map(chat => {
                const date = chat.lastTime ? new Date(chat.lastTime) : null;
                const dateStr = date ? formatDate(date) : '';
                
                return `
                    <div class="chat-item" onclick="openChatDirectly('${'${chat.file}'}')">
                        <div class="chat-avatar">
                            ${'${(chat.name || "C").charAt(0).toUpperCase()}'}
                        </div>
                        <div class="chat-info">
                            <div class="chat-name">${'${chat.name || "محادثة"}'}</div>
                            <div class="chat-preview">${'${chat.preview || "لا توجد رسائل"}'}</div>
                        </div>
                        <div class="chat-meta">
                            <div class="chat-date">${'${dateStr}'}</div>
                            <span class="message-count-badge">${'${chat.messageCount || 0}'}</span>
                        </div>
                    </div>
                `;
            }).join('');
        }
        
        function formatDate(date) {
            const now = new Date();
            const diff = now - date;
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));
            
            if (days === 0) {
                return date.toLocaleTimeString('ar-SA', {hour: '2-digit', minute: '2-digit'});
            } else if (days === 1) {
                return 'أمس';
            } else if (days < 7) {
                return date.toLocaleDateString('ar-SA', {weekday: 'long'});
            } else {
                return date.toLocaleDateString('ar-SA', {month: 'short', day: 'numeric'});
            }
        }
        
        function openChatDirectly(fileName) {
            const chat = savedChats[fileName];
            if (!chat) return;
            
            currentChat = chat;
            document.getElementById('chatsListPage').style.display = 'none';
            document.getElementById('chatViewPage').style.display = 'flex';
            document.getElementById('currentChatName').textContent = chat.name || 'محادثة';
            
            renderMessages(chat.data.messages || []);
            
            document.getElementById('scrollButtons').style.display = 'flex';
        }
        
        function renderMessages(messages) {
            const container = document.getElementById('chatMessages');
            
            if (!messages || messages.length === 0) {
                container.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">💬</div>
                        <div class="empty-state-title">لا توجد رسائل</div>
                    </div>
                `;
                return;
            }
            
            const sortedMessages = [...messages].sort((a, b) => new Date(a.time) - new Date(b.time));
            
            let html = '';
            let lastDate = '';
            
            sortedMessages.forEach(msg => {
                const msgDate = new Date(msg.time);
                const dateStr = msgDate.toLocaleDateString('ar-SA', {year: 'numeric', month: 'long', day: 'numeric'});
                
                if (dateStr !== lastDate) {
                    html += `<div class="date-separator"><span>${'${dateStr}'}</span></div>`;
                    lastDate = dateStr;
                }
                
                const isMe = msg.sender_id === MY_ID || msg.sender === MY_ID;
                const senderName = msg.sender_name || (isMe ? 'أنت' : 'المستخدم');
                const time = msgDate.toLocaleTimeString('ar-SA', {hour: '2-digit', minute: '2-digit'});
                
                let content = msg.message || '';
                
                if (content.includes('<red>') || content.includes('<green>')) {
                    content = content.replace(/<red>/g, '<span style="color: #ff6b6b;">').replace(/<\/red>/g, '</span>');
                    content = content.replace(/<green>/g, '<span style="color: #51cf66;">').replace(/<\/green>/g, '</span>');
                }
                
                if (msg.data && msg.data.correctText) {
                    content += `<div style="margin-top:4px; font-size:11px; opacity:0.7;">${'${msg.data.correctText}'}</div>`;
                }
                
                html += `
                    <div class="message-container ${'${isMe ? "me" : "other"}'}">
                        <div class="message-bubble">
                            <div class="sender-name">${'${senderName}'}</div>
                            <div class="message-text">${'${content}'}</div>
                            <div class="message-time">${'${time}'}</div>
                        </div>
                    </div>
                `;
            });
            
            container.innerHTML = html;
            
            setTimeout(() => {
                container.scrollTop = container.scrollHeight;
                updateScrollInfo();
            }, 100);
            
            container.addEventListener('scroll', updateScrollInfo);
        }
        
        function updateScrollInfo() {
            const container = document.getElementById('chatMessages');
            const counter = document.getElementById('messageCounter');
            
            if (!container || !counter) return;
            
            const scrollTop = container.scrollTop;
            const scrollHeight = container.scrollHeight;
            const clientHeight = container.clientHeight;
            const maxScroll = scrollHeight - clientHeight;
            
            if (maxScroll <= 0) {
                counter.style.display = 'none';
                return;
            }
            
            const percent = Math.round((scrollTop / maxScroll) * 100);
            counter.style.display = 'block';
            counter.textContent = percent + '%';
        }
        
        function scrollToTop() {
            const container = document.getElementById('chatMessages');
            container.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        }
        
        function scrollToBottom() {
            const container = document.getElementById('chatMessages');
            container.scrollTo({
                top: container.scrollHeight,
                behavior: 'smooth'
            });
        }
        
        function goBackToChatsList() {
            document.getElementById('chatViewPage').style.display = 'none';
            document.getElementById('chatsListPage').style.display = 'flex';
            document.getElementById('scrollButtons').style.display = 'none';
            currentChat = null;
            
            allChats = Object.values(savedChats);
            displayChatsList(allChats);
        }
        
        function clearAllChats() {
            if (confirm('هل تريد حذف جميع المحادثات؟')) {
                savedChats = {};
                allChats = [];
                localStorage.removeItem('kfriends_chats');
                showWelcomeScreen();
            }
        }
        
        window.addEventListener('load', initApp);
    </script>

</body>
</html>
        """.trimIndent()
    }

    private inner class CustomWebChromeClient : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            this@MainActivity.filePathCallback = filePathCallback
            
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            
            startActivityForResult(
                Intent.createChooser(intent, "Select Files"),
                REQUEST_SELECT_FILE
            )
            
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_SELECT_FILE) {
            if (filePathCallback == null) return
            
            val results = if (resultCode == RESULT_OK && data != null) {
                if (data.data != null) {
                    arrayOf(data.data!!)
                } else if (data.clipData != null) {
                    val clipData = data.clipData!!
                    Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                } else {
                    null
                }
            } else {
                null
            }
            
            filePathCallback?.onReceiveValue(results)
            filePathCallback = null
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == REQUEST_PERMISSION_CODE) {
            // Permissions handled, continue with app
        }
    }
}
