<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="/css/sidebar.css">
    <style>
        :root {
            --primary-dark: #1a1f2e;
            --primary-darker: #0f1219;
            --accent-blue: #3498db;
            --accent-teal: #1abc9c;
            --text-light: #ecf0f1;
            --text-muted: #95a5a6;
            --bg-light: #f5f5f5;
            --danger: #e74c3c;
            --warning: #f39c12;
            --success: #27ae60;
            /* Variables formulaire */
            --paper-bg: #f3f3ef;
            --card-bg: #ffffff;
            --line: #b5b5b5;
            --text-main: #171717;
            --text-soft: #4a4a4a;
            --accent: #d4ce84;
            --accent-dark: #173f70;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: var(--bg-light);
            color: #333;
        }

        .layout-container {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            width: 280px;
            background-color: var(--primary-dark);
            color: var(--text-light);
            padding: 20px;
            overflow-y: auto;
            box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);
        }

        .sidebar-logo {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .sidebar-logo h2 {
            font-size: 18px;
            color: var(--accent-teal);
        }

        .sidebar-menu {
            list-style: none;
        }

        .sidebar-menu > li {
            margin-bottom: 5px;
        }

        .sidebar-menu-item {
            display: block;
            padding: 12px 15px;
            color: var(--text-light);
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s;
            font-size: 14px;
        }

        .sidebar-menu-item:hover {
            background-color: rgba(52, 152, 219, 0.2);
            color: var(--accent-blue);
            padding-left: 20px;
        }

        .sidebar-menu-item.active {
            background-color: var(--accent-blue);
            color: white;
        }

        .sidebar-submenu {
            list-style: none;
            margin-left: 10px;
            display: none;
        }

        .sidebar-submenu.open {
            display: block;
        }

        .sidebar-submenu li {
            margin-bottom: 3px;
        }

        .sidebar-submenu-item {
            display: block;
            padding: 8px 15px;
            color: var(--text-muted);
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s;
            font-size: 13px;
            border-left: 2px solid transparent;
        }

        .sidebar-submenu-item:hover {
            border-left: 2px solid var(--accent-blue);
            color: var(--accent-blue);
            padding-left: 20px;
        }

        .sidebar-submenu-item.active {
            border-left: 2px solid var(--accent-teal);
            color: var(--accent-teal);
            background-color: rgba(26, 188, 156, 0.1);
        }

        .sidebar-toggle {
            display: none;
            background: none;
            border: none;
            color: var(--text-light);
            font-size: 20px;
            cursor: pointer;
        }

        /* Main content */
        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .header {
            background-color: white;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            color: var(--primary-dark);
            font-size: 24px;
        }

        .header-actions {
            display: flex;
            gap: 10px;
        }

        .content {
            flex: 1;
            padding: 30px;
            overflow-y: auto;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .sidebar {
                position: fixed;
                left: 0;
                top: 0;
                height: 100vh;
                z-index: 999;
                transform: translateX(-100%);
                transition: transform 0.3s;
            }

            .sidebar.open {
                transform: translateX(0);
            }

            .sidebar-toggle {
                display: block;
            }

            .layout-container {
                flex-direction: column;
            }

            .main-content {
                width: 100%;
            }

            .content {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
    <div class="layout-container">
        <!-- Sidebar -->
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-logo">
                <h2>Visa App</h2>
            </div>

            <ul class="sidebar-menu" id="sidebarMenu">
                <!-- SPRINT 2 - Menu de Sélection de Type de Demande -->
                <li style="margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid rgba(255,255,255,0.2);">
                    <a href="/demande/nouveau?type=NOUVEAU_TITRE" class="sidebar-menu-item">
                        ➕ Nouveau Titre
                    </a>
                    <p style="font-size: 12px; color: var(--text-muted); margin: 8px 15px 0 15px;">
                        Nouvelle demande
                    </p>
                </li>

                <li style="margin-bottom: 25px;">
                    <a href="/demande/duplicata" class="sidebar-menu-item">
                        📋 Duplicata
                    </a>
                    <p style="font-size: 12px; color: var(--text-muted); margin: 8px 15px 0 15px;">
                        Carte résidente
                    </p>
                </li>

                <li style="margin-bottom: 25px;">
                    <a href="/demande/transfert" class="sidebar-menu-item">
                        🔄 Transfert Visa
                    </a>
                    <p style="font-size: 12px; color: var(--text-muted); margin: 8px 15px 0 15px;">
                        Nouveau passeport
                    </p>
                </li>

                <!-- Séparateur -->
                <li style="margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid rgba(255,255,255,0.2); border-top: 1px solid rgba(255,255,255,0.2); padding-top: 15px;">
                </li>

                <!-- Documents Modifiables - Toujours Visible -->
                <li>
                    <a href="/demande/documents-modifiables" class="sidebar-menu-item" style="background-color: rgba(26, 188, 156, 0.15); border-left: 3px solid var(--accent-teal);">
                        ✎ Documents Modifiables
                    </a>
                    <p style="font-size: 12px; color: var(--text-muted); margin: 8px 15px 0 15px;">
                        Modifier vos demandes
                    </p>
                </li>

                <li style="margin-top: 20px; padding-top: 15px; border-top: 1px solid rgba(255,255,255,0.2);">
                    <a href="/demande/fiche-demande" class="sidebar-menu-item">
                        🧾 Fiche_demande
                    </a>
                    <p style="font-size: 12px; color: var(--text-muted); margin: 8px 15px 0 15px;">
                        Recherche et export PDF de la demande
                    </p>
                </li>

                <li style="margin-bottom: 5px;">
                    <a href="/demande/fiche-carte-resident" class="sidebar-menu-item">
                        🪪 Carte_Resident
                    </a>
                    <p style="font-size: 12px; color: var(--text-muted); margin: 8px 15px 0 15px;">
                        Recherche et export PDF de la carte resident
                    </p>
                </li>
            </ul>
        </aside>

        <!-- Main content -->
        <div class="main-content">
            <header class="header">
                <button class="sidebar-toggle" id="sidebarToggle" onclick="toggleSidebar()">☰</button>
                <h1>${pageTitle}</h1>
                <div class="header-actions">
                    <!-- Placeholder for future actions -->
                </div>
            </header>

            <div class="content">
                <jsp:include page="${contentPage}" />
            </div>
        </div>
    </div>

    <script>
        function toggleSubmenu(event, submenuId) {
            event.preventDefault();
            const submenu = document.getElementById(submenuId);
            const arrow = event.target.closest('a').querySelector('span');
            
            if (submenu) {
                submenu.classList.toggle('open');
                if (arrow) {
                    arrow.textContent = submenu.classList.contains('open') ? '▲' : '▼';
                }
            }
        }

        function toggleSidebar() {
            const sidebar = document.getElementById('sidebar');
            sidebar.classList.toggle('open');
        }

        // Close sidebar on menu item click (mobile)
        document.querySelectorAll('.sidebar-menu-item, .sidebar-submenu-item').forEach(item => {
            if (item.href && item.href !== '#') {
                item.addEventListener('click', function() {
                    const sidebar = document.getElementById('sidebar');
                    if (window.innerWidth <= 768) {
                        sidebar.classList.remove('open');
                    }
                });
            }
        });

        // Auto-open submenus if URL contains relevant parameters
        document.addEventListener('DOMContentLoaded', function() {
            const params = new URLSearchParams(window.location.search);
            if (params.has('mode') || params.has('type') || params.has('sousType')) {
                if (params.get('avecAntecedents') === '1') {
                    const avecAnterieursSubmenu = document.getElementById('avecAnterieursSubmenu');
                    if (avecAnterieursSubmenu) {
                        avecAnterieursSubmenu.classList.add('open');
                    }
                }
                if (params.get('avecAntecedents') === '0' || params.get('mode') === 'nouvelle_demande') {
                    const sansAnterieursSubmenu = document.getElementById('sansAnterieursSubmenu');
                    if (sansAnterieursSubmenu) {
                        sansAnterieursSubmenu.classList.add('open');
                    }
                }
            }
        });
    </script>
</body>
</html>
