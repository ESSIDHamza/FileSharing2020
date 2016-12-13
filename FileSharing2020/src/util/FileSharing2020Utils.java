package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class FileSharing2020Utils {
	private FileSharing2020Utils() {
	}

	public static synchronized void envoyerFichier(String cheminFichier, Socket destinataire) throws Exception {
		File file = new File(cheminFichier);
		byte[] bs = new byte[(int) file.length()];
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		bufferedInputStream.read(bs, 0, bs.length);
		OutputStream outputStream = destinataire.getOutputStream();
		String nomFichier = cheminFichier.split(Pattern.quote("\\"))[cheminFichier.split(Pattern.quote("\\")).length
				- 1];
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		dataOutputStream.writeUTF(nomFichier);
		dataOutputStream.writeInt(bs.length);
		outputStream.write(bs, 0, bs.length);
		bufferedInputStream.close();
	}

	public static synchronized String recevoirFichier(Socket emetteur, String cheminSauvegarde) throws Exception {
		InputStream inputStream = emetteur.getInputStream();
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		String nomFichier = dataInputStream.readUTF();
		int tailleFichier = dataInputStream.readInt();
		FileOutputStream fileOutputStream = new FileOutputStream(cheminSauvegarde + "\\" + nomFichier);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		byte[] bs = new byte[tailleFichier];
		int i = inputStream.read(bs, 0, bs.length);
		bufferedOutputStream.write(bs, 0, i);
		bufferedOutputStream.close();
		return nomFichier;
	}

	public static void initIndex() throws Exception {
		Directory directory = FSDirectory.open(new File("C:\\FileSharing2020\\index"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		@SuppressWarnings("deprecation")
		IndexWriter indexWriter = new IndexWriter(directory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
		File file = new File("C:\\FileSharing2020\\fichiers_partages");
		for (String nomFichier : file.list()) {
			String contenuFichier = "";
			InputStream inputStream = new FileInputStream("C:\\FileSharing2020\\fichiers_partages\\" + nomFichier);
			int c;
			while ((c = inputStream.read()) != -1)
				contenuFichier += (char) c;
			inputStream.close();
			Field nomFichierField = new Field("nomFichier", nomFichier, Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field contenuFichierField = new Field("contenuFichier", contenuFichier, Field.Store.NO,
					Field.Index.ANALYZED);
			Document document = new Document();
			document.add(nomFichierField);
			document.add(contenuFichierField);
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}

	public static synchronized void indexerFichier(String nomFichier) throws Exception {
		Directory directory = FSDirectory.open(new File("C:\\FileSharing2020\\index"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		@SuppressWarnings("deprecation")
		IndexWriter indexWriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
		String contenuFichier = "";
		InputStream inputStream = new FileInputStream("C:\\FileSharing2020\\fichiers_partages\\" + nomFichier);
		int c;
		while ((c = inputStream.read()) != -1)
			contenuFichier += (char) c;
		inputStream.close();
		Field nomFichierField = new Field("nomFichier", nomFichier, Field.Store.YES, Field.Index.NOT_ANALYZED);
		Field contenuFichierField = new Field("contenuFichier", contenuFichier, Field.Store.NO, Field.Index.ANALYZED);
		Document document = new Document();
		document.add(nomFichierField);
		document.add(contenuFichierField);
		indexWriter.addDocument(document);
		indexWriter.close();
	}

	public static synchronized List<String> rechercherFichiers(String motCle) throws Exception {
		List<String> resultat = new ArrayList<String>();
		Directory directory = FSDirectory.open(new File("C:\\FileSharing2020\\index"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryParser = new QueryParser(Version.LUCENE_36, "contenuFichier", analyzer);
		String[] motsCles = motCle.split(Pattern.quote(" "));
		String requete = "";
		for (int i = 0; i < motsCles.length - 1; i++)
			requete = requete + motsCles[i] + " OR ";
		requete += motsCles[motsCles.length - 1];
		Query query = queryParser.parse(requete);
		TopDocs topDocs = indexSearcher.search(query, 10);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			resultat.add(document.get("nomFichier"));
		}
		indexSearcher.close();
		return resultat;
	}
}