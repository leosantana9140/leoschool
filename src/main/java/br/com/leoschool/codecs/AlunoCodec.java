package br.com.leoschool.codecs;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import br.com.leoschool.model.Aluno;
import br.com.leoschool.model.Curso;
import br.com.leoschool.model.Habilidade;
import br.com.leoschool.model.Nota;

public class AlunoCodec implements CollectibleCodec<Aluno> {
	
	private Codec<Document> codec;
	
	public AlunoCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter bsonWriter, Aluno aluno, EncoderContext encoderContext) {
		Document document = new Document();
		
		document.put("_id", aluno.getId());
		document.put("nome", aluno.getNome());
		document.put("dataNascimento", aluno.getDataNascimento());
		document.put("curso", new Document("nome", aluno.getCurso().getNome()));
		List<Habilidade> habilidades = aluno.getHabilidades();
		List<Nota> notas = aluno.getNotas();
		
		if (habilidades != null) {
			List<Document> habilidadesDocument = new ArrayList<>();
			
			for (Habilidade habilidade : habilidades) {
				habilidadesDocument.add(new Document("nome", habilidade.getNome())
						.append("nivel", habilidade.getNivel()));
			}
			
			document.put("habilidades", habilidadesDocument);
		}
		
		if (notas != null) {
			List<Double> notasParaSalvar = new ArrayList<Double>();
			
			for (Nota nota : notas) {
				notasParaSalvar.add(nota.getValor());
			}
			
			document.put("notas", notasParaSalvar);
		}
		
		codec.encode(bsonWriter, document, encoderContext);
	}

	@Override
	public Class<Aluno> getEncoderClass() {
		return Aluno.class;
	}

	@Override
	public Aluno decode(BsonReader bsonReaderreader, DecoderContext decoderContext) {
		Document document = codec.decode(bsonReaderreader, decoderContext);
		
		Aluno aluno = new Aluno();
		aluno.setId(document.getObjectId("_id"));
		aluno.setNome(document.getString("nome"));
		aluno.setDataNascimento(document.getDate("dataNascimento"));
		Document curso = (Document) document.get("curso");
		
		if (curso != null) {
			aluno.setCurso(new Curso(curso.getString("nome")));
		}
		
		List<Document> habilidades = (List<Document>) document.get("habilidades");
		
		if (habilidades != null) {
			List<Habilidade> habilidadesDoAluno = new ArrayList<>();
			
			for (Document habilidade : habilidades) {
				habilidadesDoAluno.add(new Habilidade(habilidade.getString("nome"), habilidade.getString("nivel")));
			}
			
			aluno.setHabilidades(habilidadesDoAluno);
		}
		
		List<Double> notas = (List<Double>) document.get("notas");
		
		if (notas != null) {
			List<Nota> notasDoAluno = new ArrayList<>();
			
			for (Double nota : notas) {
				notasDoAluno.add(new Nota(nota));
			}
			
			aluno.setNotas(notasDoAluno);
		}
		
		return aluno;
	}

	@Override
	public Aluno generateIdIfAbsentFromDocument(Aluno aluno) {
		return documentHasId(aluno) ? aluno.gerarId() : aluno;
	}

	@Override
	public boolean documentHasId(Aluno aluno) {
		return aluno.getId() == null;
	}

	@Override
	public BsonValue getDocumentId(Aluno aluno) {
		if (documentHasId(aluno))
			throw new IllegalStateException("Este document não possui ID");
		
		return new BsonString(aluno.getId().toHexString());
	}
}
