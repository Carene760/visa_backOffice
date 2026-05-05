<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/jsp/layout.jsp">
    <jsp:param name="pageTitle" value="Erreur" />
</jsp:include>

<div class="main-content">
    <div class="container" style="max-width: 600px; margin-top: 40px;">
        <div style="background: #f8d7da; border: 2px solid #f5c6cb; border-radius: 4px; padding: 20px; text-align: center;">
            <h2 style="color: #721c24; margin-bottom: 15px;">⚠️ Erreur</h2>
            <p style="color: #721c24; font-size: 16px; margin-bottom: 10px;">${erreur}</p>
            
            <c:if test="${not empty erreurs}">
                <div style="background: #fff; border: 1px solid #f5c6cb; border-radius: 3px; padding: 15px; margin: 15px 0; text-align: left;">
                    <c:forEach var="err" items="${erreurs}">
                        <p style="color: #721c24; margin: 5px 0;">• ${err}</p>
                    </c:forEach>
                </div>
            </c:if>
            
            <div style="margin-top: 20px;">
                <a href="/demande/nouveau?type=NOUVEAU_TITRE" style="display: inline-block; background: #3498db; color: white; padding: 10px 20px; border-radius: 3px; text-decoration: none; margin-right: 10px;">Nouvelle Demande</a>
                <a href="/" style="display: inline-block; background: #95a5a6; color: white; padding: 10px 20px; border-radius: 3px; text-decoration: none;">Accueil</a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
