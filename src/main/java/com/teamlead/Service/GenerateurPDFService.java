package com.teamlead.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.teamlead.Model.Demande;
import com.teamlead.Model.PieceAFournir;
import com.teamlead.Repository.DemandeRepository;
import com.teamlead.Repository.DocumentSignatureRepository;
import com.teamlead.Repository.PieceAFournirRepository;
import com.teamlead.Model.CarteResident;
import com.teamlead.Repository.CarteResidentRepository;
import com.teamlead.Model.DocumentScan;
import com.teamlead.Repository.DocumentScanRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

@Service
public class GenerateurPDFService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private PieceAFournirRepository pieceAFournirRepository;

    @Autowired
    private DocumentScanRepository documentScanRepository;

    @Autowired
    private DocumentSignatureRepository documentSignatureRepository;

    @Autowired
    private CarteResidentRepository carteResidentRepository;

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
            document.setMargins(28, 28, 28, 28);

            document.add(creerBlocEnTete(demande));

            // Titre
            Paragraph titre = new Paragraph("RÉCÉPISSÉ DE DEMANDE DE VISA")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4);
            document.add(titre);

            // Date de génération
            Paragraph dateGen = new Paragraph("Généré le: " + java.time.LocalDateTime.now().format(DATETIME_FORMATTER))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(10);
            document.add(dateGen);

            // Numéro de dossier
            Paragraph dossierNum = new Paragraph("Numéro de Dossier: #" + demande.getId())
                    .setFontSize(11)
                    .setBold()
                    .setMarginBottom(8);
            document.add(dossierNum);

            // Statut
            Paragraph statut = new Paragraph("Statut: " + demande.getStatutDemande().getLibelle())
                    .setFontSize(10)
                    .setMarginBottom(14);
            document.add(statut);

            // Section 1: État Civil
            document.add(creerSectionTitre("SECTION 1: INFORMATIONS PERSONNELLES"));
            document.add(creerSectionEtatCivil(demande));
            document.add(new Paragraph(" ").setMarginBottom(10));

            // Section 2: Passeport
            document.add(creerSectionTitre("SECTION 2: INFORMATIONS PASSEPORT"));
            document.add(creerSectionPasseport(demande));
            document.add(new Paragraph(" ").setMarginBottom(10));

            // Section 3: Visa
            document.add(creerSectionTitre("SECTION 3: INFORMATIONS VISA"));
            document.add(creerSectionVisa(demande));
            document.add(new Paragraph(" ").setMarginBottom(10));

            // Section 4: Justificatifs
            document.add(creerSectionTitre("SECTION 4: JUSTIFICATIFS REÇUS"));
            document.add(creerSectionJustificatifs(demande));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    /**
     * Génère une fiche complète de la demande incluant photo et signature si disponibles
     */
    public byte[] genererFicheDemande(Integer idDemande) {
        Demande demande = demandeRepository.findByIdWithRelations(idDemande)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée: " + idDemande));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(24, 24, 24, 24);

            document.add(creerBlocEnTete(demande));

            Paragraph titre = new Paragraph("FICHE DE DEMANDE")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(6);
            document.add(titre);

            // Informations principales
            document.add(creerSectionTitre("INFORMATIONS DEMANDE"));
            document.add(creerSectionEtatCivil(demande));

            // Photo & signature
            var photoOpt = documentSignatureRepository.findPhotoWebcamByDemandeId(idDemande);
            var signatureOpt = documentSignatureRepository.findSignatureSourisByDemandeId(idDemande);

            if (photoOpt.isPresent() || signatureOpt.isPresent()) {
                document.add(creerSectionTitre("PHOTO / SIGNATURE"));
                Table t = new Table(UnitValue.createPercentArray(new float[] {1,1})).useAllAvailableWidth();

                if (photoOpt.isPresent()) {
                    byte[] photo = photoOpt.get().getContenu();
                    Image img = new Image(ImageDataFactory.create(photo)).setAutoScale(true).setMaxWidth(UnitValue.createPercentValue(45));
                    t.addCell(new Cell().add(new Paragraph("Photo webcam").setBold()).add(img));
                } else {
                    t.addCell(new Cell().add(new Paragraph("Photo webcam").setBold()).add(new Paragraph("(non fournie)")));
                }

                if (signatureOpt.isPresent()) {
                    byte[] signature = signatureOpt.get().getContenu();
                    Image sig = new Image(ImageDataFactory.create(signature)).setAutoScale(true).setMaxWidth(UnitValue.createPercentValue(45));
                    t.addCell(new Cell().add(new Paragraph("Signature souris").setBold()).add(sig));
                } else {
                    t.addCell(new Cell().add(new Paragraph("Signature souris").setBold()).add(new Paragraph("(non fournie)")));
                }

                document.add(t);
            }

            // Justificatifs (liste des scans reçus)
            document.add(creerSectionTitre("DOCUMENTS SCANNÉS"));
            List<DocumentScan> scans = documentScanRepository.findByIdDemande(idDemande);
            if (scans != null && !scans.isEmpty()) {
                Table table = new Table(UnitValue.createPercentArray(new float[] {4,2,2})).useAllAvailableWidth();
                table.addCell(cellHeader("Fichier"));
                table.addCell(cellHeader("Pièce"));
                table.addCell(cellHeader("Date upload"));
                for (DocumentScan ds : scans) {
                    table.addCell(cellValue(ds.getNomFichier()));
                    table.addCell(cellValue(ds.getPieceAFournir() != null && ds.getPieceAFournir().getTypeDocument() != null
                            ? ds.getPieceAFournir().getTypeDocument().getLibelle() : "-"));
                    table.addCell(cellValue(ds.getDateUpload() != null ? ds.getDateUpload().format(DATETIME_FORMATTER) : "-"));
                }
                document.add(table);
            } else {
                document.add(new Paragraph("Aucun document scanné enregistré.").setItalic());
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF de fiche: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    /**
     * Section État Civil du récépissé
     */
    private Table creerSectionEtatCivil(Demande demande) {
        Table table = new Table(UnitValue.createPercentArray(2));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepTogether(false);

        table.addCell(cellHeader("Champ"));
        table.addCell(cellHeader("Valeur"));

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

        return table;
    }

    /**
     * Section Passeport du récépissé
     */
    private Table creerSectionPasseport(Demande demande) {
        Table table = new Table(UnitValue.createPercentArray(2));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepTogether(false);

        table.addCell(cellHeader("Champ"));
        table.addCell(cellHeader("Valeur"));

        String numeroPasseport = "-";
        String dateDelivrance = "-";
        String dateExpiration = "-";

        if (demande.getDemandeur() != null) {
            numeroPasseport = "(Voir détails demandeur)";
        }

        addRow(table, "Numéro Passeport", numeroPasseport);
        addRow(table, "Date Délivrance", dateDelivrance);
        addRow(table, "Date Expiration", dateExpiration);

        return table;
    }

    /**
     * Section Visa du récépissé
     */
    private Table creerSectionVisa(Demande demande) {
        Table table = new Table(UnitValue.createPercentArray(2));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepTogether(false);

        table.addCell(cellHeader("Champ"));
        table.addCell(cellHeader("Valeur"));

        addRow(table, "Référence Visa", "(Voir détails dossier)");
        addRow(table, "Type Visa", "-");
        addRow(table, "Date Entrée", "-");
        addRow(table, "Date Expiration", "-");
        addRow(table, "Lieu Entrée", "-");

        return table;
    }

    /**
     * Section Justificatifs du récépissé
     */
    private Table creerSectionJustificatifs(Demande demande) {
        List<PieceAFournir> pieces = pieceAFournirRepository.findByDemande(demande);

        Table table = new Table(UnitValue.createPercentArray(3));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepTogether(false);

        table.addCell(cellHeader("Document"));
        table.addCell(cellHeader("Obligatoire"));
        table.addCell(cellHeader("Reçu"));

        if (pieces != null && !pieces.isEmpty()) {
            for (PieceAFournir piece : pieces) {
                String typeDoc = piece.getTypeDocument() != null
                        ? piece.getTypeDocument().getLibelle()
                        : "Unknown";
                String obligatoire = piece.getTypeDocument() != null && piece.getTypeDocument().getObligatoire()
                        ? "OUI"
                        : "NON";
                String recu = piece.getPresent() != null && piece.getPresent() ? "OUI" : "NON";

                table.addCell(cellValue(typeDoc));
                table.addCell(cellValue(obligatoire));
                table.addCell(cellValue(recu));
            }
        } else {
            table.addCell(new Cell(1, 3).add(new Paragraph("Aucun justificatif").setItalic()));
        }

        return table;
    }

    private Paragraph creerSectionTitre(String titre) {
        return new Paragraph(titre)
                .setFontSize(11)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(6)
                .setKeepTogether(true);
    }

    private Paragraph creerBlocEnTete(Demande demande) {
        Div bloc = new Div();
        bloc.setBorder(Border.NO_BORDER);
        bloc.setPaddingBottom(8);
        bloc.add(new Paragraph("Dossier #" + demande.getId())
                .setBold()
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT));

        String nomDemandeur = demande.getDemandeur() != null && demande.getDemandeur().getNom() != null
                ? demande.getDemandeur().getNom()
                : "-";
        String prenomDemandeur = demande.getDemandeur() != null && demande.getDemandeur().getPrenom() != null
                ? demande.getDemandeur().getPrenom()
                : "";
        bloc.add(new Paragraph("Demandeur: " + nomDemandeur + (prenomDemandeur.isBlank() ? "" : " " + prenomDemandeur))
                .setFontSize(10)
                .setMarginTop(0));

        return new Paragraph().add(bloc);
    }

    private Cell cellHeader(String texte) {
        return new Cell()
                .add(new Paragraph(texte).setBold().setFontSize(9))
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell cellValue(String texte) {
        return new Cell()
                .add(new Paragraph(texte != null ? texte : "-").setFontSize(8.5f))
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /**
     * Utilitaire pour ajouter une ligne à une table
     */
    private void addRow(Table table, String cle, String valeur) {
        table.addCell(new Cell().add(new Paragraph(cle)));
        table.addCell(new Cell().add(new Paragraph(valeur != null ? valeur : "-")));
    }

    /**
     * Génère la fiche PDF pour une CarteResident (inclut la demande associée)
     */
    public byte[] genererFicheCarteResident(Integer idCarte) {
        CarteResident carte = carteResidentRepository.findByIdWithDemande(idCarte);
        if (carte == null) throw new IllegalArgumentException("Carte resident non trouvée: " + idCarte);

        Demande demande = carte.getDemande();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(24,24,24,24);

            document.add(creerBlocEnTete(demande));
            document.add(creerSectionTitre("FICHE CARTE RÉSIDENT"));

            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addCell(cellHeader("Champ"));
            table.addCell(cellHeader("Valeur"));
            addRow(table, "Référence carte", carte.getReference());
            addRow(table, "Date émission", carte.getDateEmission() != null ? carte.getDateEmission().format(DATETIME_FORMATTER) : "-");
            addRow(table, "Date entrée", carte.getDateEntree() != null ? carte.getDateEntree().toString() : "-");
            addRow(table, "Date expiration", carte.getDateExpiration() != null ? carte.getDateExpiration().toString() : "-");
            addRow(table, "Demande liée", demande != null ? String.valueOf(demande.getId()) : "-");
            document.add(table);

            // Ajouter photo/signature du demandeur si présent
            if (demande != null) {
                Integer demandeId = demande.getId();
                var photoOpt = documentSignatureRepository.findPhotoWebcamByDemandeId(demandeId);
                var signatureOpt = documentSignatureRepository.findSignatureSourisByDemandeId(demandeId);
                
                if (photoOpt.isPresent() || signatureOpt.isPresent()) {
                    document.add(creerSectionTitre("PHOTO / SIGNATURE"));
                    Table t = new Table(UnitValue.createPercentArray(new float[]{1,1})).useAllAvailableWidth();
                    
                    if (photoOpt.isPresent()) {
                        byte[] photo = photoOpt.get().getContenu();
                        t.addCell(new Cell().add(new Paragraph("Photo").setBold()).add(new Image(ImageDataFactory.create(photo)).setAutoScale(true)));
                    } else {
                        t.addCell(new Cell().add(new Paragraph("Photo").setBold()).add(new Paragraph("(non fournie)")));
                    }
                    
                    if (signatureOpt.isPresent()) {
                        byte[] signature = signatureOpt.get().getContenu();
                        t.addCell(new Cell().add(new Paragraph("Signature").setBold()).add(new Image(ImageDataFactory.create(signature)).setAutoScale(true)));
                    } else {
                        t.addCell(new Cell().add(new Paragraph("Signature").setBold()).add(new Paragraph("(non fournie)")));
                    }
                    
                    document.add(t);
                }
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF carte: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}
