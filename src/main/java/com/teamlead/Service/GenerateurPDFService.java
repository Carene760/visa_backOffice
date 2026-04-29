package com.teamlead.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.teamlead.Model.Demande;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.PieceAFournirRepository;

@Service
public class GenerateurPDFService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Génère un PDF récépissé pour une demande
     * Contient: État civil, passeport, visa, justificatifs
     */
    public byte[] genererRecepisse(Integer idDemande) {
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée: " + idDemande));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Titre
            Paragraph titre = new Paragraph("RÉCÉPISSÉ DE DEMANDE DE VISA")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(titre);

            // Date de génération
            Paragraph dateGen = new Paragraph("Généré le: " + java.time.LocalDateTime.now().format(DATETIME_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(15);
            document.add(dateGen);

            // Numéro de dossier
            Paragraph dossierNum = new Paragraph("Numéro de Dossier: #" + demande.getId())
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(15);
            document.add(dossierNum);

            // Statut
            Paragraph statut = new Paragraph("Statut: " + demande.getStatutDemande().getLibelle())
                    .setFontSize(11)
                    .setMarginBottom(20);
            document.add(statut);

            // Section 1: État Civil
            document.add(creerSectionEtatCivil(demande));
            document.add(new Paragraph("\n"));

            // Section 2: Passeport
            document.add(creerSectionPasseport(demande));
            document.add(new Paragraph("\n"));

            // Section 3: Visa
            document.add(creerSectionVisa(demande));
            document.add(new Paragraph("\n"));

            // Section 4: Justificatifs
            document.add(creerSectionJustificatifs(demande));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    /**
     * Section État Civil du récépissé
     */
    private Paragraph creerSectionEtatCivil(Demande demande) {
        Paragraph section = new Paragraph("SECTION 1: INFORMATIONS PERSONNELLES")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10);

        Table table = new Table(UnitValue.createPercentArray(2));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes
        table.addCell(new Cell().add(new Paragraph("Champ").setBold()));
        table.addCell(new Cell().add(new Paragraph("Valeur").setBold()));

        // Données
        addRow(table, "Nom", demande.getDemandeur().getNom());
        addRow(table, "Prénom", demande.getDemandeur().getPrenom());
        addRow(table, "Nom de Naissance", demande.getDemandeur().getNomNaissance() != null
                ? demande.getDemandeur().getNomNaissance()
                : "-");
        addRow(table, "Date de Naissance", demande.getDemandeur().getDateNaissance() != null
                ? demande.getDemandeur().getDateNaissance().format(DATE_FORMATTER)
                : "-");
        addRow(table, "Lieu de Naissance", demande.getDemandeur().getLieuNaissance() != null
                ? demande.getDemandeur().getLieuNaissance()
                : "-");
        addRow(table, "Nationalité", demande.getDemandeur().getNationalite().getLibelle());
        addRow(table, "Email", demande.getDemandeur().getEmail());
        addRow(table, "Téléphone", demande.getDemandeur().getTelephone());
        addRow(table, "Adresse", demande.getDemandeur().getAdresseMadagascar());

        return new Paragraph().add(section).add(table);
    }

    /**
     * Section Passeport du récépissé
     */
    private Paragraph creerSectionPasseport(Demande demande) {
        Paragraph section = new Paragraph("SECTION 2: INFORMATIONS PASSEPORT")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10);

        Table table = new Table(UnitValue.createPercentArray(2));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes
        table.addCell(new Cell().add(new Paragraph("Champ").setBold()));
        table.addCell(new Cell().add(new Paragraph("Valeur").setBold()));

        // Données - Le Demandeur peut avoir des passeports
        String numeroPasseport = "-";
        String dateDelivrance = "-";
        String dateExpiration = "-";

        // Pour Sprint 1/2, supposer un seul passeport par demandeur
        // En production, il faudrait parcourir la liste des passeports
        if (demande.getDemandeur() != null) {
            // Note: Accès direct au passeport via la relation - à adapter selon votre modèle
            // Pour maintenant, afficher que le passeport n'est pas accessible
            numeroPasseport = "(Voir détails demandeur)";
        }

        addRow(table, "Numéro Passeport", numeroPasseport);
        addRow(table, "Date Délivrance", dateDelivrance);
        addRow(table, "Date Expiration", dateExpiration);

        return new Paragraph().add(section).add(table);
    }

    /**
     * Section Visa du récépissé
     */
    private Paragraph creerSectionVisa(Demande demande) {
        Paragraph section = new Paragraph("SECTION 3: INFORMATIONS VISA")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10);

        Table table = new Table(UnitValue.createPercentArray(2));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes
        table.addCell(new Cell().add(new Paragraph("Champ").setBold()));
        table.addCell(new Cell().add(new Paragraph("Valeur").setBold()));

        // Données - La demande peut avoir un visa associé
        // Pour maintenant, afficher que le visa n'est pas accessible directement
        // Il faut accéder via VisaRepository.findByDemande()
        addRow(table, "Référence Visa", "(Voir détails dossier)");
        addRow(table, "Type Visa", "-");
        addRow(table, "Date Entrée", "-");
        addRow(table, "Date Expiration", "-");
        addRow(table, "Lieu Entrée", "-");

        return new Paragraph().add(section).add(table);
    }

    /**
     * Section Justificatifs du récépissé
     */
    private Paragraph creerSectionJustificatifs(Demande demande) {
        Paragraph section = new Paragraph("SECTION 4: JUSTIFICATIFS REÇUS")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10);

        List<PieceAFournir> pieces = pieceAFournirRepository.findByDemande(demande);

        Table table = new Table(UnitValue.createPercentArray(3));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes
        table.addCell(new Cell().add(new Paragraph("Document").setBold()));
        table.addCell(new Cell().add(new Paragraph("Obligatoire").setBold()));
        table.addCell(new Cell().add(new Paragraph("Reçu").setBold()));

        // Données
        if (pieces != null && !pieces.isEmpty()) {
            for (PieceAFournir piece : pieces) {
                String typeDoc = piece.getTypeDocument() != null
                        ? piece.getTypeDocument().getLibelle()
                        : "Unknown";
                String obligatoire = piece.getTypeDocument() != null && piece.getTypeDocument().getObligatoire()
                        ? "OUI"
                        : "NON";
                String recu = piece.getPresent() != null && piece.getPresent() ? "OUI" : "NON";

                table.addCell(new Cell().add(new Paragraph(typeDoc)));
                table.addCell(new Cell().add(new Paragraph(obligatoire)));
                table.addCell(new Cell().add(new Paragraph(recu)));
            }
        } else {
            table.addCell(new Cell().add(new Paragraph("Aucun justificatif")));
            table.addCell(new Cell().add(new Paragraph("-")));
            table.addCell(new Cell().add(new Paragraph("-")));
        }

        return new Paragraph().add(section).add(table);
    }

    /**
     * Utilitaire pour ajouter une ligne à une table
     */
    private void addRow(Table table, String cle, String valeur) {
        table.addCell(new Cell().add(new Paragraph(cle)));
        table.addCell(new Cell().add(new Paragraph(valeur != null ? valeur : "-")));
    }
}
