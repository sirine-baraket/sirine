package tn.esprit.sirine;



import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.sirine.models.Commentaire;
import tn.esprit.sirine.models.Publication;
import tn.esprit.sirine.services.PublicationService;
import tn.esprit.sirine.utils.LanguageDetectionUtil;

import java.util.List;

import static java.io.IO.print;

public class HelloController {

    @FXML
    private TextArea publicationField;
    @FXML
    private VBox publicationList;
    @FXML
    private Label statusMessage;
    @FXML
    private TextField searchField;

    private final PublicationService service = new PublicationService();

    @FXML
    public void initialize() {
        chargerPublications();
    }

    @FXML
    public void publier() {
        String texte = publicationField.getText().trim();
        if (texte.isEmpty()) {
            statusMessage.setText("Le champ de publication est vide.");
            return;
        }

        Publication p = new Publication(texte);
        service.ajouterPublication(p);
        publicationField.clear();
        statusMessage.setText("");
        afficherPublication(p);
    }

    private void chargerPublications() {
        publicationList.getChildren().clear();
        List<Publication> publications = service.getAllPublications();
        for (Publication p : publications) {
            afficherPublication(p);
        }
    }
    private void afficherPublication(Publication p) {
        VBox pubBox = new VBox(5);
        pubBox.setPadding(new Insets(10));
        pubBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");

        Label pubText = new Label(p.getTexte());
        pubText.setStyle("-fx-font-weight: bold;");

        // 🔍 Détection de la langue via API
        String langue = LanguageDetectionUtil.detectLanguage(p.getTexte());
        Label langueLabel = new Label("Langue détectée : " + langue);
        langueLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        Button deletePubBtn = new Button("Supprimer");
        deletePubBtn.setOnAction(e -> {
            service.supprimerPublication(p.getId());
            publicationList.getChildren().remove(pubBox);
        });

        HBox header = new HBox(10, pubText, deletePubBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        // Zone de commentaire
        VBox commentairesBox = new VBox(5);
        List<Commentaire> commentaires = service.getCommentairesParPublication(p.getId());
        print(commentaires);
        for (Commentaire c : commentaires) {
            HBox ligne = new HBox(5);
            Label commentLabel = new Label("→ " + c.getTexte());
            Button deleteCommentBtn = new Button("X");
            deleteCommentBtn.setOnAction(e -> {
                service.supprimerCommentaire(c.getId());
                commentairesBox.getChildren().remove(ligne);
            });
            ligne.getChildren().addAll(commentLabel, deleteCommentBtn);
            commentairesBox.getChildren().add(ligne);
        }

        TextField newCommentField = new TextField();
        Button ajouterCommentBtn = new Button("Commenter");
        ajouterCommentBtn.setOnAction(e -> {
            String texte = newCommentField.getText().trim();
            if (!texte.isEmpty()) {
                Commentaire c = new Commentaire(p.getId(), texte);
                service.ajouterCommentaire(c);
                Label commentLabel = new Label("→ " + c.getTexte());
                Button deleteCommentBtn = new Button("X");
                deleteCommentBtn.setOnAction(e2 -> {
                    service.supprimerCommentaire(c.getId());
                    commentairesBox.getChildren().remove(commentLabel.getParent());
                });
                HBox ligne = new HBox(5, commentLabel, deleteCommentBtn);
                commentairesBox.getChildren().add(ligne);
                newCommentField.clear();
            }
        });

        HBox commentInput = new HBox(10, newCommentField, ajouterCommentBtn);
        commentInput.setAlignment(Pos.CENTER_LEFT);

        pubBox.getChildren().addAll(header, langueLabel, commentairesBox, commentInput);
        publicationList.getChildren().add(0, pubBox); // ajout en haut
    }

    private void afficherPublication2(Publication p) {
        VBox pubBox = new VBox(5);
        pubBox.setPadding(new Insets(10));
        pubBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");

        Label pubText = new Label(p.getTexte());
        pubText.setStyle("-fx-font-weight: bold;");
        Button deletePubBtn = new Button("Supprimer");
        deletePubBtn.setOnAction(e -> {
            service.supprimerPublication(p.getId());
            publicationList.getChildren().remove(pubBox);
        });

        HBox header = new HBox(10, pubText, deletePubBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        // Zone de commentaire
        VBox commentairesBox = new VBox(5);
        List<Commentaire> commentaires = service.getCommentairesParPublication(p.getId());
        print(commentaires);
        for (Commentaire c : commentaires) {
            HBox ligne = new HBox(5);
            Label commentLabel = new Label("→ " + c.getTexte());
            Button deleteCommentBtn = new Button("X");
            deleteCommentBtn.setOnAction(e -> {
                service.supprimerCommentaire(c.getId());
                commentairesBox.getChildren().remove(ligne);
            });
            ligne.getChildren().addAll(commentLabel, deleteCommentBtn);
            commentairesBox.getChildren().add(ligne);
        }

        TextField newCommentField = new TextField();
        Button ajouterCommentBtn = new Button("Commenter");
        ajouterCommentBtn.setOnAction(e -> {
            String texte = newCommentField.getText().trim();
            if (!texte.isEmpty()) {
                Commentaire c = new Commentaire(p.getId(), texte);
                service.ajouterCommentaire(c);
                Label commentLabel = new Label("→ " + c.getTexte());
                HBox ligne = new HBox(5, commentLabel, new Button("X"));
                commentairesBox.getChildren().add(ligne);
                newCommentField.clear();
            }
        });

        HBox commentInput = new HBox(10, newCommentField, ajouterCommentBtn);
        commentInput.setAlignment(Pos.CENTER_LEFT);

        pubBox.getChildren().addAll(header, commentairesBox, commentInput);
        publicationList.getChildren().add(0, pubBox); // ajout en haut
    }

    @FXML
    public void rechercherPublication() {
        String motCle = searchField.getText().trim().toLowerCase();
        publicationList.getChildren().clear();

        if (motCle.isEmpty()) {
            chargerPublications(); // recharge tout si champ vide
            return;
        }

        List<Publication> publications = service.getAllPublications();
        for (Publication p : publications) {
            if (p.getTexte().toLowerCase().contains(motCle)) {
                afficherPublication(p);
            }
        }
    }
}
