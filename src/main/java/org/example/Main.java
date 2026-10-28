package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        // Les chemins sont relatifs à la racine du projet, d'où l'importance
        // de lancer le programme depuis ce répertoire.
        File xml = new File("src/main/resources/films.xml");
        File xsd = new File("src/main/resources/films.xsd");

        // ===== 1. Vérifier que le document respecte sa structure XSD =====
        System.out.println("=== Validation XML/XSD ===");
        try {
            // Le schéma décrit les éléments, attributs et types autorisés
            // dans le fichier XML.
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            System.out.println("Le document XML est valide.");
        } catch (Exception e) {
            System.out.println("Document invalide : " + e.getMessage());
            return; // Inutile d'interroger un document qui ne respecte pas le schéma.
        }

        // ===== 2. Charger le XML et préparer l'évaluateur XPath =====
        System.out.println("\n=== Interrogation XPath ===");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Chaque expression sélectionne les titres correspondant au critère.
        afficher(xpath, doc, "Tous les titres de films", "//film/titre");
        afficher(xpath, doc, "Films sortis après 2000", "//film[annee > 2000]/titre");
        afficher(xpath, doc, "Films disponibles", "//film[@disponible='true']/titre");

        // count() renvoie un nombre, et non une liste de nœuds comme les requêtes précédentes.
        XPathExpression expr = xpath.compile("count(//film)");
        Double nb = (Double) expr.evaluate(doc, XPathConstants.NUMBER);
        System.out.println("\nNombre de films : " + nb.intValue());
    }

    // Évalue une requête qui sélectionne des nœuds, puis affiche le texte de chacun.
    static void afficher(XPath xpath, Document doc, String titre, String requete) throws Exception {
        XPathExpression expr = xpath.compile(requete);
        NodeList resultat = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        System.out.println("\n" + titre + "  (" + requete + ")");
        for (int i = 0; i < resultat.getLength(); i++) {
            System.out.println(" - " + resultat.item(i).getTextContent());
        }
    }
}
