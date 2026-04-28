package com.teamlead.Service;
                    import java.time.LocalDateTime;
                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.stereotype.Service;
                    import com.teamlead.Exception.ValidationException;
                    import com.teamlead.Model.*;
                    import com.teamlead.Repository.*;

                    @Service
                    public class DemandeStatusService{
                    @Autowired private StatutDemandeRepository statutDemandeRepository;
                    @Autowired private HistoriqueStatutDemandeRepository historiqueStatutDemandeRepository;

                    public void initializeDemandeStatus(Demande demande){
                    StatutDemande statut=statutDemandeRepository.findByLibelle("DOSSIER_CREE");
                    if(statut==null)throw new ValidationException("Erreur de configuration du système",java.util.List.of("Le statut 'DOSSIER_CREE' n'existe pas en base de données."));
                    HistoriqueStatutDemande h=new HistoriqueStatutDemande();
                    h.setDemande(demande);
                    h.setStatut(statut);
                    h.setDateChangement(LocalDateTime.now());
                    historiqueStatutDemandeRepository.save(h);
                    }
                    }