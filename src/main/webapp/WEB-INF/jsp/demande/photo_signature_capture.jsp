<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
        :root {
            --bg: #f3f3ef;
            --surface: #ffffff;
            --surface-soft: #fafaf7;
            --border: #c8c8c0;
            --text: #1f2430;
            --muted: #5c6472;
            --accent: #173f70;
            --accent-2: #d4ce84;
            --success: #1f7a4d;
            --danger: #b42318;
        }

        * { box-sizing: border-box; }

        .capture-page {
            width: 100%;
            padding: 0;
        }


        .hero {
            background: var(--surface);
            color: var(--text);
            padding: 24px;
            border: 1px solid #9a9a9a;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        }

        .hero-top {
            display: flex;
            justify-content: space-between;
            gap: 16px;
            align-items: flex-start;
            flex-wrap: wrap;
        }

        .hero h1 {
            margin: 0 0 8px;
            font-size: clamp(24px, 3vw, 34px);
            letter-spacing: 0.4px;
            color: var(--text-main, #171717);
        }

        .hero p {
            margin: 0;
            color: var(--text-soft, #4a4a4a);
            max-width: 760px;
            line-height: 1.6;
        }

        .badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 14px;
            border-radius: 4px;
            background: #f4f7fb;
            border: 1px solid #d9e2ef;
            color: #2c3e50;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 0.3px;
            white-space: nowrap;
        }

        .capture-content {
            margin-top: 20px;
            display: grid;
            grid-template-columns: 1.05fr 0.95fr;
            gap: 20px;
        }

        .card {
            background: var(--surface);
            border: 1px solid var(--border);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            overflow: hidden;
        }

        .card-head {
            padding: 16px 18px;
            background: linear-gradient(180deg, #ffffff 0%, #f5f5f1 100%);
            border-bottom: 1px solid #deded6;
        }

        .card-head h2 {
            margin: 0;
            font-size: 18px;
            color: var(--accent);
        }

        .card-head small {
            display: block;
            margin-top: 6px;
            color: var(--muted);
            line-height: 1.5;
        }

        .card-body { padding: 18px; }

        .step-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
            margin: 18px 0 0;
        }

        .step {
            padding: 12px 14px;
            border: 1px solid #d8d8cf;
            background: var(--surface-soft);
        }

        .step strong {
            display: block;
            color: var(--accent);
            margin-bottom: 4px;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.35px;
        }

        .step span {
            color: var(--muted);
            font-size: 13px;
            line-height: 1.45;
        }

        .panel {
            padding: 16px;
            background: #fff;
            border: 1px solid #d6d6cd;
        }

        .panel + .panel { margin-top: 16px; }

        .panel-title {
            margin: 0 0 12px;
            font-size: 15px;
            color: #20252f;
            text-transform: uppercase;
            letter-spacing: 0.4px;
        }

        .video-wrap,
        .canvas-wrap {
            width: 100%;
            border: 1px solid #bfc3c9;
            background: #0e1320;
            overflow: hidden;
        }

        video,
        canvas.signature-canvas,
        img.preview-photo {
            display: block;
            width: 100%;
            height: auto;
        }

        .preview-photo { background: #0e1320; }

        .hint {
            margin-top: 10px;
            color: var(--muted);
            font-size: 13px;
            line-height: 1.5;
        }

        .capture-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 14px;
        }

        button,
        .btn-link-action {
            border: 0;
            border-radius: 2px;
            padding: 11px 16px;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            transition: transform 0.15s ease, background-color 0.15s ease, opacity 0.15s ease;
            text-decoration: none;
            display: inline-block;
        }

        button:hover,
        .btn-link-action:hover { transform: translateY(-1px); }
        button:disabled { cursor: not-allowed; opacity: 0.6; transform: none; }

        .btn-primary { background: var(--accent); color: #fff; }
        .btn-secondary { background: #ecede8; color: #252a36; }
        .btn-accent { background: var(--accent-2); color: #1c1f24; }

        .status-box {
            margin-top: 14px;
            padding: 12px 14px;
            border: 1px solid #d8d8cf;
            background: #fbfbf8;
            color: var(--muted);
            font-size: 13px;
            line-height: 1.5;
        }

        .status-box.success { border-color: #b7dfc8; background: #eef8f1; color: var(--success); }
        .status-box.error { border-color: #f0b2ad; background: #fff2f1; color: var(--danger); }

        .footer-bar {
            margin-top: 20px;
            display: flex;
            justify-content: space-between;
            gap: 12px;
            flex-wrap: wrap;
            align-items: center;
            padding: 16px 18px;
            background: #fff;
            border: 1px solid var(--border);
            box-shadow: 0 8px 24px rgba(20, 27, 45, 0.06);
        }

        .footer-bar .meta {
            color: var(--muted);
            font-size: 13px;
            line-height: 1.5;
        }

        .signature-shell { position: relative; }

        .signature-empty {
            position: absolute;
            inset: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: rgba(255, 255, 255, 0.52);
            font-size: 14px;
            pointer-events: none;
            text-align: center;
        }

        @media (max-width: 960px) {
            .capture-content,
            .step-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
    <div class="capture-page">
        <section class="hero">
            <div class="hero-top">
                <div>
                    <h1>Capture photo et signature</h1>
                    <p>
                        Dossier n° <strong><c:out value="${demandeId}"/></strong> : capturez d'abord la photo webcam,
                        puis validez la signature au doigt ou à la souris avant l'envoi.
                    </p>
                </div>
            </div>

            <div class="step" style="margin-top: 16px; background: #fcfcfc; border-color: #b7b7b7; color: #333;">
                <strong style="color: #333;">État actuel</strong>
                <span style="color: #4a4a4a;">
                    <c:choose>
                        <c:when test="${photoTerminee and signatureTerminee}">Photo et signature sont déjà terminées.</c:when>
                        <c:when test="${photoTerminee}">Photo déjà terminée. Vous pouvez revenir plus tard pour compléter la signature.</c:when>
                        <c:when test="${signatureTerminee}">Signature déjà terminée. Vous pouvez revenir plus tard pour compléter la photo.</c:when>
                        <c:otherwise>Aucune partie n'est encore finalisée. Vous pouvez enregistrer la photo ou la signature séparément.</c:otherwise>
                    </c:choose>
                </span>
            </div>

            <div class="step-grid" aria-label="Workflow de saisie">
                <div class="step">
                    <strong>1. Photo</strong>
                    <span>Activez la webcam puis capturez l'image dans la zone de prévisualisation.</span>
                </div>
                <div class="step">
                    <strong>2. Signature</strong>
                    <span>Tracez la signature sur le canvas dédié, puis validez avant l'envoi.</span>
                </div>
                <div class="step">
                    <strong>3. Soumission</strong>
                    <span>Les deux images sont envoyées en base64 vers les endpoints Spring Boot.</span>
                </div>
            </div>
        </section>

        <div class="capture-content">
            <section class="card">
                <div class="card-head">
                    <h2>Capture webcam</h2>
                    <small>Le flux vidéo est lu directement depuis le navigateur. Un clic sur « Prendre Photo » fige l'image dans la prévisualisation.</small>
                </div>
                <div class="card-body">
                    <div class="panel">
                        <div class="video-wrap">
                            <video id="cameraVideo" autoplay playsinline muted></video>
                        </div>
                        <div class="capture-actions">
                            <button type="button" class="btn-primary" id="startCameraBtn">Activer la caméra</button>
                            <button type="button" class="btn-accent" id="capturePhotoBtn" disabled>Prendre Photo</button>
                            <button type="button" class="btn-secondary" id="retakePhotoBtn" disabled>Reprendre</button>
                            <button type="button" class="btn-primary" id="savePhotoBtn" disabled>Enregistrer Photo</button>
                        </div>
                        <p class="hint">Astuce : autorisez l'accès à la caméra si le navigateur le demande. La capture est stockée temporairement en base64 pour l'envoi AJAX.</p>
                    </div>

                    <div class="panel">
                        <h3 class="panel-title">Prévisualisation photo</h3>
                        <div class="canvas-wrap">
                            <img id="photoPreview" class="preview-photo" alt="Prévisualisation photo capturée" src="data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='960' height='540' viewBox='0 0 960 540'%3E%3Crect width='960' height='540' fill='%230e1320'/%3E%3Ctext x='50%25' y='50%25' fill='%23ffffff' font-family='Segoe UI, Arial, sans-serif' font-size='22' text-anchor='middle'%3EPas encore de photo capturée%3C/text%3E%3C/svg%3E" />
                        </div>
                        <div class="status-box" id="photoStatus">Aucune photo capturée pour le moment.</div>
                    </div>
                </div>
            </section>

            <section class="card">
                <div class="card-head">
                    <h2>Signature souris</h2>
                    <small>Tracez la signature sur le canvas. Le bouton « Valider Signature » fige la zone et prépare l'image pour l'envoi.</small>
                </div>
                <div class="card-body">
                    <div class="panel">
                        <div class="signature-shell">
                            <canvas id="signatureCanvas" class="signature-canvas" width="900" height="320"></canvas>
                            <div class="signature-empty" id="signaturePlaceholder">Dessinez ici votre signature</div>
                        </div>
                        <div class="capture-actions">
                            <button type="button" class="btn-primary" id="validateSignatureBtn">Valider Signature</button>
                            <button type="button" class="btn-secondary" id="clearSignatureBtn">Effacer</button>
                            <button type="button" class="btn-primary" id="saveSignatureBtn" disabled>Enregistrer Signature</button>
                        </div>
                        <p class="hint">Vous pouvez utiliser la souris, un stylet ou le toucher sur écran tactile.</p>
                    </div>

                    <div class="panel">
                        <h3 class="panel-title">État de signature</h3>
                        <div class="status-box" id="signatureStatus">Signature non validée.</div>
                    </div>
                </div>
            </section>
        </div>

        <div class="footer-bar">
            <div class="meta">
                <strong>Dossier :</strong> <c:out value="${demandeId}"/><br>
                <strong>Format d'envoi :</strong> base64 via AJAX vers `/demande/{id}/photo` puis `/demande/{id}/signature`
            </div>
            <div class="capture-actions" style="margin-top: 0;">
                <a href="/demande/${demandeId}/detail" class="btn-link-action btn-secondary">Retour au dossier</a>
                <button type="button" class="btn-secondary" id="resetAllBtn">Réinitialiser</button>
                <button type="button" class="btn-primary" id="submitAllBtn">Soumettre</button>
            </div>
        </div>

        <div id="globalStatus" class="status-box" style="margin-top: 16px; display: none;"></div>
    </div>

    <canvas id="photoCaptureCanvas" width="1280" height="720" hidden></canvas>

    <script>
        (() => {
            const demandeId = '<c:out value="${demandeId}"/>';
            const cameraVideo = document.getElementById('cameraVideo');
            const photoCanvas = document.getElementById('photoCaptureCanvas');
            const photoPreview = document.getElementById('photoPreview');
            const photoStatus = document.getElementById('photoStatus');
            const signatureCanvas = document.getElementById('signatureCanvas');
            const signaturePlaceholder = document.getElementById('signaturePlaceholder');
            const signatureStatus = document.getElementById('signatureStatus');
            const globalStatus = document.getElementById('globalStatus');
            const startCameraBtn = document.getElementById('startCameraBtn');
            const capturePhotoBtn = document.getElementById('capturePhotoBtn');
            const retakePhotoBtn = document.getElementById('retakePhotoBtn');
            const validateSignatureBtn = document.getElementById('validateSignatureBtn');
            const clearSignatureBtn = document.getElementById('clearSignatureBtn');
            const savePhotoBtn = document.getElementById('savePhotoBtn');
            const saveSignatureBtn = document.getElementById('saveSignatureBtn');
            const resetAllBtn = document.getElementById('resetAllBtn');
            const submitAllBtn = document.getElementById('submitAllBtn');
            const ctx = signatureCanvas.getContext('2d');
            const photoCtx = photoCanvas.getContext('2d');

            let mediaStream = null;
            let photoBase64 = null;
            let signatureBase64 = null;
            let signatureDrawn = false;
            let isDrawing = false;
            let lastPoint = null;

            const endpoints = {
                photo: `/demande/${demandeId}/photo`,
                signature: `/demande/${demandeId}/signature`
            };

            function showStatus(element, message, kind) {
                element.textContent = message;
                element.className = `status-box ${kind || ''}`.trim();
                element.style.display = 'block';
            }

            function setGlobalStatus(message, kind) {
                showStatus(globalStatus, message, kind);
            }

            function clearGlobalStatus() {
                globalStatus.style.display = 'none';
                globalStatus.className = 'status-box';
                globalStatus.textContent = '';
            }

            function ensureCameraSize() {
                const width = cameraVideo.videoWidth || 1280;
                const height = cameraVideo.videoHeight || 720;
                photoCanvas.width = width;
                photoCanvas.height = height;
            }

            async function startCamera() {
                clearGlobalStatus();
                if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
                    showStatus(photoStatus, 'La webcam n\'est pas disponible sur ce navigateur.', 'error');
                    return;
                }

                try {
                    stopCamera();
                    mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
                    cameraVideo.srcObject = mediaStream;
                    await cameraVideo.play();
                    capturePhotoBtn.disabled = false;
                    retakePhotoBtn.disabled = false;
                    showStatus(photoStatus, 'Caméra active. Cliquez sur « Prendre Photo » pour capturer l\'image.', 'success');
                } catch (error) {
                    console.error(error);
                    showStatus(photoStatus, 'Impossible d\'accéder à la caméra. Vérifiez les permissions du navigateur.', 'error');
                }
            }

            function stopCamera() {
                if (mediaStream) {
                    mediaStream.getTracks().forEach(track => track.stop());
                    mediaStream = null;
                }
                cameraVideo.srcObject = null;
            }

            function capturePhoto() {
                if (!cameraVideo.srcObject) {
                    showStatus(photoStatus, 'Activez la caméra avant de capturer une photo.', 'error');
                    return;
                }

                ensureCameraSize();
                photoCtx.drawImage(cameraVideo, 0, 0, photoCanvas.width, photoCanvas.height);
                photoBase64 = photoCanvas.toDataURL('image/jpeg', 0.92);
                photoPreview.src = photoBase64;
                showStatus(photoStatus, 'Photo capturée et prête pour envoi.', 'success');
                savePhotoBtn.disabled = false;
            }

            function resetPhoto() {
                photoBase64 = null;
                photoPreview.src = "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='960' height='540' viewBox='0 0 960 540'%3E%3Crect width='960' height='540' fill='%230e1320'/%3E%3Ctext x='50%25' y='50%25' fill='%23ffffff' font-family='Segoe UI, Arial, sans-serif' font-size='22' text-anchor='middle'%3EPas encore de photo capturée%3C/text%3E%3C/svg%3E";
                showStatus(photoStatus, 'Aucune photo capturée pour le moment.', '');
                savePhotoBtn.disabled = true;
            }

            function resizeSignatureCanvas() {
                const ratio = Math.max(window.devicePixelRatio || 1, 1);
                const rect = signatureCanvas.getBoundingClientRect();
                const width = Math.max(rect.width || 900, 320);
                const height = Math.max(rect.height || 320, 220);

                if (signatureCanvas.width !== Math.round(width * ratio) || signatureCanvas.height !== Math.round(height * ratio)) {
                    const snapshot = signatureCanvas.toDataURL();
                    signatureCanvas.width = Math.round(width * ratio);
                    signatureCanvas.height = Math.round(height * ratio);
                    signatureCanvas.style.width = `${width}px`;
                    signatureCanvas.style.height = `${height}px`;
                    ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
                    ctx.lineWidth = 2.5;
                    ctx.lineCap = 'round';
                    ctx.lineJoin = 'round';
                    ctx.strokeStyle = '#111827';

                    if (snapshot && !snapshot.endsWith('base64,') && signatureDrawn) {
                        const image = new Image();
                        image.onload = () => ctx.drawImage(image, 0, 0, width, height);
                        image.src = snapshot;
                    }
                }
            }

            function getPoint(event) {
                const rect = signatureCanvas.getBoundingClientRect();
                const clientX = event.clientX ?? (event.touches && event.touches[0] ? event.touches[0].clientX : 0);
                const clientY = event.clientY ?? (event.touches && event.touches[0] ? event.touches[0].clientY : 0);
                return {
                    x: clientX - rect.left,
                    y: clientY - rect.top
                };
            }

            function drawLine(from, to) {
                ctx.beginPath();
                ctx.moveTo(from.x, from.y);
                ctx.lineTo(to.x, to.y);
                ctx.stroke();
            }

            function startDraw(event) {
                event.preventDefault();
                resizeSignatureCanvas();
                signatureCanvas.setPointerCapture?.(event.pointerId);
                isDrawing = true;
                lastPoint = getPoint(event);
                signatureDrawn = true;
                signaturePlaceholder.style.display = 'none';
            }

            function draw(event) {
                if (!isDrawing) {
                    return;
                }
                event.preventDefault();
                const currentPoint = getPoint(event);
                drawLine(lastPoint, currentPoint);
                lastPoint = currentPoint;
            }

            function endDraw(event) {
                if (event) {
                    event.preventDefault();
                }
                if (!isDrawing) {
                    return;
                }
                isDrawing = false;
                lastPoint = null;
            }

            function isCanvasBlank(canvas) {
                return !canvas.getContext('2d').getImageData(0, 0, canvas.width, canvas.height).data.some(value => value !== 0);
            }

            function clearSignature() {
                ctx.clearRect(0, 0, signatureCanvas.width, signatureCanvas.height);
                signatureDrawn = false;
                signatureBase64 = null;
                signaturePlaceholder.style.display = 'flex';
                showStatus(signatureStatus, 'Signature non validée.', '');
            }

            function validateSignature() {
                resizeSignatureCanvas();
                if (isCanvasBlank(signatureCanvas)) {
                    showStatus(signatureStatus, 'La signature est vide. Veuillez dessiner avant de valider.', 'error');
                    signatureBase64 = null;
                    saveSignatureBtn.disabled = true;
                    return;
                }

                signatureBase64 = signatureCanvas.toDataURL('image/png');
                signaturePlaceholder.style.display = 'none';
                showStatus(signatureStatus, 'Signature validée et prête pour envoi.', 'success');
                saveSignatureBtn.disabled = false;
            }

            async function postBase64(url, fieldName, base64Value) {
                const formData = new FormData();
                formData.append(fieldName, base64Value);

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: formData
                });

                const contentType = response.headers.get('content-type') || '';
                if (contentType.includes('application/json')) {
                    return response.json();
                }

                const text = await response.text();
                return { success: response.ok, message: text };
            }

            async function submitPartial(kind) {
                clearGlobalStatus();

                const isPhoto = kind === 'photo';
                const payload = isPhoto ? photoBase64 : signatureBase64;
                const endpoint = isPhoto ? endpoints.photo : endpoints.signature;
                const fieldName = isPhoto ? 'photoBase64' : 'signatureBase64';

                if (!payload) {
                    setGlobalStatus(isPhoto ? 'Capturez d\'abord une photo.' : 'Validez d\'abord la signature.', 'error');
                    return;
                }

                const button = isPhoto ? savePhotoBtn : saveSignatureBtn;
                const originalLabel = button.textContent;
                button.disabled = true;
                button.textContent = 'Envoi...';

                try {
                    const result = await postBase64(endpoint, fieldName, payload);
                    if (result && result.success === false) {
                        throw new Error(result.message || 'Erreur lors de l\'enregistrement');
                    }

                    setGlobalStatus(isPhoto ? 'Photo enregistrée avec succès.' : 'Signature enregistrée avec succès.', 'success');
                    if (isPhoto) {
                        showStatus(photoStatus, 'Photo enregistrée. Vous pouvez compléter la signature plus tard.', 'success');
                        photoBase64 = null;
                    } else {
                        showStatus(signatureStatus, 'Signature enregistrée. Vous pouvez compléter la photo plus tard.', 'success');
                        signatureBase64 = null;
                    }
                } catch (error) {
                    console.error(error);
                    setGlobalStatus(error.message || 'Une erreur est survenue pendant l\'envoi.', 'error');
                } finally {
                    button.disabled = false;
                    button.textContent = originalLabel;
                }
            }

            async function submitAll() {
                const hasPhoto = Boolean(photoBase64);
                const hasSignature = Boolean(signatureBase64);

                if (!hasPhoto && !hasSignature) {
                    setGlobalStatus('Aucune donnée à soumettre. Capturez une photo ou validez une signature.', 'error');
                    return;
                }

                if (hasPhoto) {
                    await submitPartial('photo');
                }

                if (hasSignature) {
                    await submitPartial('signature');
                }

                if (hasPhoto && hasSignature) {
                    setGlobalStatus('Photo et signature enregistrées avec succès.', 'success');
                    setTimeout(() => {
                        window.location.href = `/demande/${demandeId}/upload-scanner`;
                    }, 650);
                }
            }

            function resetAll() {
                resetPhoto();
                clearSignature();
                clearGlobalStatus();
            }

            window.addEventListener('beforeunload', stopCamera);
            window.addEventListener('resize', () => resizeSignatureCanvas());

            startCameraBtn.addEventListener('click', startCamera);
            capturePhotoBtn.addEventListener('click', capturePhoto);
            retakePhotoBtn.addEventListener('click', resetPhoto);
            validateSignatureBtn.addEventListener('click', validateSignature);
            clearSignatureBtn.addEventListener('click', clearSignature);
            savePhotoBtn.addEventListener('click', () => submitPartial('photo'));
            saveSignatureBtn.addEventListener('click', () => submitPartial('signature'));
            resetAllBtn.addEventListener('click', resetAll);
            submitAllBtn.addEventListener('click', submitAll);

            signatureCanvas.addEventListener('pointerdown', startDraw);
            signatureCanvas.addEventListener('pointermove', draw);
            signatureCanvas.addEventListener('pointerup', endDraw);
            signatureCanvas.addEventListener('pointercancel', endDraw);
            signatureCanvas.addEventListener('pointerleave', endDraw);

            resizeSignatureCanvas();
            clearSignature();
            savePhotoBtn.disabled = true;
            saveSignatureBtn.disabled = true;
        })();
    </script>