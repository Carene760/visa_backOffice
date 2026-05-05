<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .menu-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 70vh;
        background: linear-gradient(135deg, #f5f5f5 0%, #f0f0f0 100%);
        padding: 40px 20px;
    }

    .menu-card {
        background: white;
        border-radius: 12px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        padding: 60px 40px;
        max-width: 600px;
        text-align: center;
    }

    .menu-title {
        font-size: 32px;
        font-weight: 700;
        color: var(--text-main);
        margin-bottom: 10px;
        text-transform: uppercase;
        letter-spacing: 1px;
    }

    .menu-subtitle {
        font-size: 16px;
        color: var(--text-soft);
        margin-bottom: 40px;
        line-height: 1.6;
    }

    .menu-buttons {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .menu-btn {
        display: block;
        padding: 20px 30px;
        font-size: 16px;
        font-weight: 600;
        text-decoration: none;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s ease;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        text-align: center;
    }

    .btn-nouveau {
        background: var(--accent-dark);
        color: white;
    }

    .btn-nouveau:hover {
        background: #0f2a52;
        box-shadow: 0 6px 20px rgba(23, 63, 112, 0.3);
        transform: translateY(-2px);
    }

    .btn-duplicata {
        background: var(--accent-blue);
        color: white;
    }

    .btn-duplicata:hover {
        background: #2980b9;
        box-shadow: 0 6px 20px rgba(52, 152, 219, 0.3);
        transform: translateY(-2px);
    }

    .btn-transfert {
        background: var(--accent-teal);
        color: white;
    }

    .btn-transfert:hover {
        background: #16a085;
        box-shadow: 0 6px 20px rgba(26, 188, 156, 0.3);
        transform: translateY(-2px);
    }

    .btn-description {
        display: block;
        font-size: 13px;
        font-weight: 400;
        margin-top: 8px;
        opacity: 0.9;
    }
</style>

<div class="menu-container">
    <div class="menu-card">
        <h1 class="menu-title">Créer une demande</h1>
        <p class="menu-subtitle">Sélectionnez le type de demande que vous souhaitez effectuer</p>
        
        <div class="menu-buttons">
            <a href="/demande/nouveau?type=NOUVEAU_TITRE" class="menu-btn btn-nouveau">
                Nouveau Titre
                <span class="btn-description">Nouvelle demande de visa</span>
            </a>
            
            <a href="/demande/duplicata" class="menu-btn btn-duplicata">
                Duplicata
                <span class="btn-description">Demande de duplicata de carte résidente</span>
            </a>
            
            <a href="/demande/transfert" class="menu-btn btn-transfert">
                Transfert de Visa
                <span class="btn-description">Transférer le visa vers un nouveau passeport</span>
            </a>
        </div>
    </div>
</div>
