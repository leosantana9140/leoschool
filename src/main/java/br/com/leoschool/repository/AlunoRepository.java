package br.com.leoschool.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import br.com.leoschool.codecs.AlunoCodec;
import br.com.leoschool.model.Aluno;

@Repository
public class AlunoRepository {
	private MongoClient mongoClient;
	private MongoDatabase mongoDatabase;
	
	public void salvar(Aluno aluno) {
		getConnection();
		
		MongoCollection<Aluno> alunosCollection = this.mongoDatabase.getCollection("alunos", Aluno.class);
		
		if (aluno.getId() == null) {
			alunosCollection.insertOne(aluno);			
		} else {
			alunosCollection.updateOne(Filters.eq("_id", aluno.getId()), new Document("$set", aluno));
		}
		
		closeConnection();
	}
	
	public List<Aluno> listar() {
		getConnection();
		
		MongoCollection<Aluno> alunosCollection = this.mongoDatabase.getCollection("alunos", Aluno.class);
		
		MongoCursor<Aluno> mongoCursor = alunosCollection.find().iterator();
		
		List<Aluno> alunos = popularAlunos(mongoCursor);
		
		closeConnection();
		
		return alunos;
	}
	
	public Aluno findById(String id) {
		getConnection();
		
		MongoCollection<Aluno> alunosCollection = this.mongoDatabase.getCollection("alunos", Aluno.class);
		
		Aluno aluno = alunosCollection.find(Filters.eq("_id", new ObjectId(id))).first();
		
		return aluno;
	}
	
	public List<Aluno> findByName(String nome) {
		getConnection();
		
		MongoCollection<Aluno> alunosCollection = this.mongoDatabase.getCollection("alunos", Aluno.class);
		
		MongoCursor<Aluno> mongoCursor = alunosCollection.find(Filters.eq("nome", nome), Aluno.class).iterator();
		
		List<Aluno> alunos = popularAlunos(mongoCursor);
		
		closeConnection();
		
		return alunos;
	}
	
	private List<Aluno> popularAlunos(MongoCursor<Aluno> mongoCursor) {
		List<Aluno> alunos = new ArrayList<Aluno>();
		
		while (mongoCursor.hasNext()) {
			alunos.add(mongoCursor.next());
		}
		
		return alunos;	
	}
	
	public void getConnection() {
		Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		AlunoCodec alunoCodec = new AlunoCodec(codec);
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromCodecs(alunoCodec));
		MongoClientOptions mongoClientOptions = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
		
		this.mongoClient = new MongoClient("localhost:27017", mongoClientOptions);
		this.mongoDatabase = mongoClient.getDatabase("local");
	}
	
	public void closeConnection() {
		this.mongoClient.close();
	}

	public List<Aluno> findByGrade(String classificacao, double nota) {
		getConnection();
		
		MongoCollection<Aluno> alunosCollection = this.mongoDatabase.getCollection("alunos", Aluno.class);
		
		MongoCursor<Aluno> mongoCursor = null;
		
		if (classificacao.equals("reprovados")) {
			mongoCursor = alunosCollection.find(Filters.lt("notas", nota)).iterator();
		} else if (classificacao.equals("aprovados")) {
			mongoCursor = alunosCollection.find(Filters.gte("notas", nota)).iterator();
		}
		
		List<Aluno> alunos = popularAlunos(mongoCursor);
		
		closeConnection();
		
		return alunos;
	}

	public List<Aluno> findByLocation(Aluno aluno) {
		getConnection();
		
		MongoCollection<Aluno> alunosCollection = this.mongoDatabase.getCollection("alunos", Aluno.class);
		
		alunosCollection.createIndex(Indexes.geo2dsphere("contato"));
		
		List<Double> coordinates = aluno.getContato().getCoordinates();
		
		Point pontoDeReferencia = new Point(new Position(coordinates.get(0), coordinates.get(1)));
		
		MongoCursor<Aluno> mongoCursor = alunosCollection.find(Filters.nearSphere("contato", pontoDeReferencia, 2000.0, 0.0)).limit(2).skip(1).iterator();
		
		List<Aluno> alunos = popularAlunos(mongoCursor);
		
		closeConnection();
		
		return alunos;
	}
}
