<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Suivi de Demande de Visa</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 900px;
            margin: 0 auto;
        }
        
        .header {
            text-align: center;
            color: white;
            margin-bottom: 40px;
        }
        
        .header h1 {
            font-size: 2.2em;
            font-weight: 700;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
            margin-bottom: 10px;
        }
        
        .header p {
            font-size: 1.05em;
            opacity: 0.95;
        }
        
        .card {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            margin-bottom: 20px;
        }
        
        .status-block {
            text-align: center;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        
        .status-block h2 {
            margin-bottom: 15px;
            color: #333;
        }
        
        .status-display {
            display: inline-block;
            padding: 20px 40px;
            border-radius: 8px;
            font-size: 1.3em;
            font-weight: 600;
        }
        
        .status-approved {
            background: #d4edda;
            color: #155724;
        }
        
        .status-refused {
            background: #f8d7da;
            color: #721c24;
        }
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .info-section h3 {
            margin: 20px 0 15px 0;
            color: #333;
            font-size: 1.3em;
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .info-item {
            padding: 12px;
            background: #f8f9fa;
            border-radius: 6px;
            border-left: 3px solid #667eea;
        }
        
        .info-label {
            font-weight: 600;
            color: #666;
            font-size: 0.9em;
            margin-bottom: 4px;
        }
        
        .info-value {
            color: #333;
            font-size: 1em;
        }
        
        .error {
            background-color: #fee;
            color: #c33;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #c33;
        }
        
        .loading {
            text-align: center;
            padding: 40px;
            font-size: 1.1em;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <header class="header">
            <h1>📱 Suivi de Demande de Visa -  3371- 3265 - 3273</h1>
            <p>Consultez l'état de votre demande</p>
        </header>
        
        <div id="content"></div>
    </div>
    
    <script>
        // Utiliser l'URL actuelle comme base pour l'API
        const protocol = window.location.protocol;
        const host = window.location.host;
        const API_BASE_URL = `${protocol}//${host}`;
        const numeroDemande = '<%= request.getAttribute("numeroDemande") %>';
        
        // Récupérer les données de la demande
        async function loadDemande() {
            const content = document.getElementById('content');
            
            try {
                content.innerHTML = '<div class="loading">⏳ Chargement...</div>';
                
                const response = await fetch(`${API_BASE_URL}/api/demandes/track/${numeroDemande}`);
                
                if (!response.ok) {
                    throw new Error('Demande non trouvée');
                }
                
                const demande = await response.json();
                displayDemande(demande);
            } catch (err) {
                content.innerHTML = `<div class="card"><div class="error">⚠️ ${err.message}</div></div>`;
            }
        }
        
        // Afficher les données de la demande
        function displayDemande(demande) {
            const content = document.getElementById('content');
            const statusClass = getStatusClass(demande.statutActuel);
            
            const html = `
                <div class="card">
                    <div class="status-block">
                        <h2>${demande.numeroDemande}</h2>
                        <div class="status-display ${statusClass}">
                            ${getStatusIcon(demande.statutActuel)} ${demande.statutActuel}
                        </div>
                    </div>
                </div>
                
                <div class="card info-section">
                    <h3>📋 Informations</h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">Demandeur</div>
                            <div class="info-value">${demande.prenomDemandeur} ${demande.nomDemandeur}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Téléphone</div>
                            <div class="info-value">${demande.telephone || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Email</div>
                            <div class="info-value">${demande.email || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Passeport</div>
                            <div class="info-value">${demande.numeroPasport || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Nationalité</div>
                            <div class="info-value">${demande.nationalite || 'N/A'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Type de Demande</div>
                            <div class="info-value">${demande.typeDemande || 'N/A'}</div>
                        </div>
                    </div>
                </div>
            `;
            
            content.innerHTML = html;
        }
        
        function getStatusClass(statut) {
            if (!statut) return 'status-pending';
            const normalized = statut.toUpperCase();
            if (normalized.includes('APPROUVE')) return 'status-approved';
            if (normalized.includes('REFUSE')) return 'status-refused';
            return 'status-pending';
        }
        
        function getStatusIcon(statut) {
            if (!statut) return '⏳';
            const normalized = statut.toUpperCase();
            if (normalized.includes('APPROUVE')) return '✅';
            if (normalized.includes('REFUSE')) return '❌';
            return '⏳';
        }
        
        // Charger les données au chargement de la page
        loadDemande();
    </script>
</body>
</html>
