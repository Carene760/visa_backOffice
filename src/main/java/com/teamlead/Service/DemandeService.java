package com.teamlead.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamlead.Model.*;
import com.teamlead.Repository.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private TypeDocumentRepository typeDocumentRepository;

    @Autowired
    private StatutDemandeRepository statutDemandeRepository;

    public Demande creerDemande(Demande demande) {
        // Définir le statut initial à "DOSSIER_CREE"
        StatutDemande statusDossierCree = statutDemandeRepository.findByLibelle("DOSSIER_CREE");
        if (statusDossierCree == null) {
            throw new RuntimeException("Statut 'DOSSIER_CREE' non trouvé");
        }

        demande.setStatutDemande(statusDossierCree);
        demande.setDateDemande(LocalDateTime.now());
        demande.setDateModification(LocalDateTime.now());

        // Vérifier les documents obligatoires
        verifierDocumentsObliqatoires(demande.getTypeMotif());

        // Sauvegarder la demande
        Demande demandeSauvegardee = demandeRepository.save(demande);

        // Créer les entrées pour les pièces à fournir
        creerPiecesAFournir(demandeSauvegardee);

        return demandeSauvegardee;
    }

    private void verifierDocumentsObliqatoires(TypeMotif typeMotif) {
        // Récupérer tous les documents obligatoires (communs et spécifiques)
        List<TypeDocument> documentsObliges = typeDocumentRepository.findByObligatoire(true);
        // La vérification complète se fera au niveau du formulaire côté client
    }

    private void creerPiecesAFournir(Demande demande) {
        // Récupérer tous les types de documents (communs + spécifiques au motif)
        List<TypeDocument> documentsCommuns = typeDocumentRepository.findByIdTypeMotifIsNull();
        List<TypeDocument> documentsSpecifiques = typeDocumentRepository.findByIdTypeMotif(demande.getTypeMotif().getId());

        // Créer les entrées pour les documents communs
        for (TypeDocument doc : documentsCommuns) {
            PieceAFournir piece = new PieceAFournir();
            piece.setDemande(demande);
            piece.setTypeDocument(doc);
            piece.setPresent(false);
            piece.setValide(false);
            piece.setDateModification(LocalDateTime.now());
            pieceAFournirRepository.save(piece);
        }

        // Créer les entrées pour les documents spécifiques
        for (TypeDocument doc : documentsSpecifiques) {
            PieceAFournir piece = new PieceAFournir();
            piece.setDemande(demande);
            piece.setTypeDocument(doc);
            piece.setPresent(false);
            piece.setValide(false);
            piece.setDateModification(LocalDateTime.now());
            pieceAFournirRepository.save(piece);
        }
    }

    public Demande getDemande(Integer id) {
        return demandeRepository.findById(id).orElse(null);
    }

    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll();
    }

    public Demande updateDemande(Demande demande) {
        demande.setDateModification(LocalDateTime.now());
        return demandeRepository.save(demande);
    }
}
