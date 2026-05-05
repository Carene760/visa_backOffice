<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<style>
    .accueil-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 60vh;
        background: linear-gradient(135deg, #f5f5f5 0%, #f0f0f0 100%);
        padding: 40px 20px;
        text-align: center;
    }

    .accueil-card {
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        padding: 60px 40px;
        max-width: 700px;
    }

    .accueil-title {
        font-size: 36px;
        font-weight: 700;
        color: var(--text-main);
        margin-bottom: 16px;
        text-transform: uppercase;
        letter-spacing: 1px;
    }

    .accueil-subtitle {
        font-size: 18px;
        color: var(--text-soft);
        margin-bottom: 32px;
        line-height: 1.8;
    }

    .accueil-instructions {
        background: #f9f9f9;
        border-left: 4px solid var(--accent-blue);
        padding: 20px;
        text-align: left;
        border-radius: 4px;
        margin-top: 30px;
    }

    .accueil-instructions h3 {
        color: var(--text-main);
        margin-bottom: 12px;
        font-size: 16px;
    }

    .accueil-instructions p {
        color: var(--text-soft);
        margin-bottom: 8px;
        font-size: 14px;
        line-height: 1.6;
    }

    .accueil-instructions strong {
        color: var(--accent-blue);
    }

    .accueil-separator {
        margin: 30px 0;
        border-top: 1px solid #e0e0e0;
    }

    .footer-note {
        font-size: 13px;
        color: var(--text-muted);
        margin-top: 20px;
    }
</style>

<div class="accueil-container">
    <div class="accueil-card">
        <h1 class="accueil-title">Bienvenue</h1>
        <p class="accueil-subtitle">
            Système de Gestion des Demandes de Visa
        </p>

        <div class="accueil-separator"></div>

        <div class="accueil-instructions">
            <h3>Démarrer une nouvelle demande</h3>
            <p>
                <strong>➕ Nouveau Titre</strong><br>
                Créer une nouvelle demande de visa standard
            </p>
            <p style="margin-top: 12px;">
                <strong>📋 Duplicata</strong><br>
                Demander un duplicata de carte résidente
            </p>
            <p style="margin-top: 12px;">
                <strong>🔄 Transfert Visa</strong><br>
                Transférer un visa vers un nouveau passeport
            </p>
        </div>

        <div class="accueil-separator"></div>

        <p class="footer-note">
            Utilisez le menu du sidebar pour sélectionner votre type de demande
        </p>
    </div>
</div>
