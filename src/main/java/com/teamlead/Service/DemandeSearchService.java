package com.teamlead.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.teamlead.DTO.DemandeDetailDTO;
import com.teamlead.DTO.DemandeSearchResponseDTO;
import com.teamlead.DTO.HistoriqueStatutDTO;
import com.teamlead.DTO.JournalActiviteDTO;
import com.teamlead.Model.DecisionDocument;
import com.teamlead.Model.Demande;
import com.teamlead.Model.Demandeur;
import com.teamlead.Model.HistoriqueStatutDemande;
import com.teamlead.Model.JournalActivite;
import com.teamlead.Model.Passeport;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.HistoriqueStatutDemandeRepository;
import com.teamlead.Repository.JournalActiviteRepository;
import com.teamlead.Repository.DecisionDocumentRepository;
import com.teamlead.Repository.PasseportRepository;

/**
 * Service pour la recherche de demandes
 * Détermine automatiquement le type de recherche (numéro demande ou passeport)
 */
@Service
public class DemandeSearchService {

    private final DemandeRepository demandeRepository;
    private final PasseportRepository passeportRepository;
    private final HistoriqueStatutDemandeRepository historiqueStatutRepository;
    private final JournalActiviteRepository journalActiviteRepository;
    private final DecisionDocumentRepository decisionDocumentRepository;

    public DemandeSearchService(
            DemandeRepository demandeRepository,
            PasseportRepository passeportRepository,
            HistoriqueStatutDemandeRepository historiqueStatutRepository,
            JournalActiviteRepository journalActiviteRepository,
            DecisionDocumentRepository decisionDocumentRepository) {
        this.demandeRepository = demandeRepository;
        this.passeportRepository = passeportRepository;
        this.historiqueStatutRepository = historiqueStatutRepository;
        this.journalActiviteRepository = journalActiviteRepository;
        this.decisionDocumentRepository = decisionDocumentRepository;
    }

    /**
     * Recherche automatique: détermine si c'est un numéro de demande ou de passeport
     * Stratégie: essayer passeport d'abord, puis demande
     */
    public DemandeSearchResponseDTO search(String searchCriteria) {
        if (searchCriteria == null || searchCriteria.trim().isEmpty()) {
            throw new IllegalArgumentException("Le critère de recherche ne peut pas être vide");
        }

        String normalized = searchCriteria.trim();

        // Stratégie 1: Essayer comme numéro de passeport (peut être numérique ou alphanumérique)
        try {
            return searchByNumeroPasport(normalized);
        } catch (IllegalArgumentException e) {
            // Passeport non trouvé, essayer comme numéro de demande
        }

        // Stratégie 2: Essayer comme numéro de demande (doit être numérique)
        try {
            Integer demandeId = Integer.parseInt(normalized);
            return searchByDemandeId(demandeId);
        } catch (NumberFormatException e) {
            // Pas un nombre
        }

        // Rien trouvé
        throw new IllegalArgumentException("Aucune demande ou passeport trouvé pour: " + searchCriteria);
    }

    /**
     * Recherche par numéro de demande
     * Retourne la demande en évidence + autres demandes du demandeur
     */
    private DemandeSearchResponseDTO searchByDemandeId(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée: " + demandeId));

        DemandeDetailDTO demandeEnEvidence = convertToDetailDTO(demande);

        // Récupérer toutes les autres demandes du même demandeur (triées DESC)
        List<Demande> autresDemandes = demandeRepository
                .findByDemandeurIdOrderByDateDemandeDesc(demande.getDemandeur().getId())
                .stream()
                .filter(d -> !d.getId().equals(demandeId))
                .collect(Collectors.toList());

        List<DemandeDetailDTO> autresDemandesDTO = autresDemandes.stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());

        // Calculer statistiques à partir de la table decision_document
        List<Demande> toutesDemandes = new ArrayList<>();
        toutesDemandes.add(demande);
        toutesDemandes.addAll(autresDemandes);

        int total = toutesDemandes.size();
        int approuvees = countByDecision(toutesDemandes, 1);
        int refusees = countByDecision(toutesDemandes, 2);
        int enCours = countByDecision(toutesDemandes, 3);

        return new DemandeSearchResponseDTO(
            demandeEnEvidence,
            autresDemandesDTO,
            total,
            approuvees,
            refusees,
            enCours,
            "NUMERO_DEMANDE",
            String.valueOf(demandeId)
        );
    }

    /**
     * Recherche par numéro de passeport
     * Retourne toutes les demandes du demandeur
     */
    private DemandeSearchResponseDTO searchByNumeroPasport(String numeroPasport) {
        Passeport passeport = passeportRepository.findByNumero(numeroPasport);
        if (passeport == null) {
            throw new IllegalArgumentException("Passeport non trouvé: " + numeroPasport);
        }

        Demandeur demandeur = passeport.getDemandeur();

        // Récupérer toutes les demandes du demandeur (triées DESC)
        List<Demande> demandes = demandeRepository
                .findByDemandeurIdOrderByDateDemandeDesc(demandeur.getId());

        List<DemandeDetailDTO> demandesDTO = demandes.stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());

        // Calculer statistiques à partir de la table decision_document
        int approuvees = countByDecision(demandes, 1);
        int refusees = countByDecision(demandes, 2);
        int enCours = countByDecision(demandes, 3);

        return new DemandeSearchResponseDTO(
            null,  // Pas de demande mise en évidence
            demandesDTO,
            demandesDTO.size(),
            approuvees,
            refusees,
            enCours,
            "NUMERO_PASSEPORT",
            numeroPasport
        );
    }

    /**
     * Convertir Demande en DTO avec historique et journal d'activité
     */
    private DemandeDetailDTO convertToDetailDTO(Demande demande) {
        List<HistoriqueStatutDemande> historiques = historiqueStatutRepository
                .findByDemandeIdOrderByDateChangementDesc(demande.getId());

        List<HistoriqueStatutDTO> historiqueDTO = historiques.stream()
                .map(h -> new HistoriqueStatutDTO(
                    h.getId(),
                    h.getStatut().getLibelle(),
                    h.getDateChangement()
                ))
                .collect(Collectors.toList());

        Demandeur demandeur = demande.getDemandeur();
        
        // Récupérer le dernier passeport du demandeur
        String numeroPasport = "N/A";
        Passeport passeportRecent = passeportRepository.findFirstByDemandeurOrderByDateCreationDesc(demandeur);
        if (passeportRecent != null) {
            numeroPasport = passeportRecent.getNumero();
        }
        
        // Récupérer le journal d'activité du demandeur
        List<JournalActivite> activites = journalActiviteRepository
                .findByDemandeurIdOrderByDateActionDesc(demandeur.getId());
        
        List<JournalActiviteDTO> activitesDTO = activites.stream()
                .map(a -> new JournalActiviteDTO(
                    a.getId(),
                    a.getTypeEvenement().getCode(),
                    a.getDateAction()
                ))
                .collect(Collectors.toList());
        
        // récupérer la dernière décision enregistrée dans la table decision_document
        String typeDecision = null;
        try {
            DecisionDocument last = decisionDocumentRepository.findTopByDemandeOrderByIdDesc(demande);
            if (last != null && last.getDecision() != null) {
                typeDecision = last.getDecision().getLibelle();
            } else if (last != null) {
                typeDecision = last.getTypeDecision();
            }
        } catch (Exception e) {
            // ignore - leave typeDecision null
        }

        return new DemandeDetailDTO(
            demande.getId(),
            formatNumeroDemande(demande.getId()),
            demandeur.getId(),
            demandeur.getNom(),
            demandeur.getPrenom(),
            numeroPasport,
            demandeur.getNomNaissance(),
            demandeur.getDateNaissance(),
            demandeur.getLieuNaissance(),
            demandeur.getEmail(),
            demandeur.getTelephone(),
            demandeur.getAdresseMadagascar(),
            demandeur.getNationalite() != null ? demandeur.getNationalite().getLibelle() : "N/A",
            demandeur.getSituationMatrimoniale() != null ? demandeur.getSituationMatrimoniale().getLibelle() : "N/A",
            demande.getTypeDemande().getLibelle(),
            null,  // sousTypeDemande
            demande.getTypeMotif().getLibelle(),
            demande.getStatutDemande().getLibelle(),
            typeDecision,
            demande.getAvecDonneesAnterieures(),
            demande.getSansDonneesAnterieures(),
            demande.getDateDemande(),
            demande.getDateTraitement(),
            demande.getDateModification(),
            demande.getQrCodeUrl(),
            null,  // qrCodeBase64 (calculé si needed)
            demande.getQrCodeGenerated(),
            demande.getDateGenerationQrCode(),
            historiqueDTO,
            activitesDTO
        );
    }

    private String formatNumeroDemande(Integer id) {
        return "DEMANDE-" + String.format("%06d", id);
    }

    private int countByDecision(List<Demande> demandes, int decisionId) {
        return (int) demandes.stream()
            .filter(demande -> {
                if (demande == null) {
                    return false;
                }

                DecisionDocument last = decisionDocumentRepository.findTopByDemandeOrderByIdDesc(demande);
                if (last == null || last.getDecision() == null || last.getDecision().getId() == null) {
                    return false;
                }

                return Integer.valueOf(decisionId).equals(last.getDecision().getId());
            })
            .count();
    }
}
