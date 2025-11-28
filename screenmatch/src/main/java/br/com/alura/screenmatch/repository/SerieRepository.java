package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    //Busca série através do título da série
    Optional<Serie> findByTituloContainingIgnoreCase(String serie);

    //Busca séries em que um determinado ator atuou
    Optional<List<Serie>> findByAtoresContainingIgnoreCase(String ator);

    //Busca séries de um ator com avaliações maiores ou iguais ao parâmetro
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);

    //Busca as 5 séries melhores avaliadas utilizando o DESC para pegar da maior a menor
    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    //Busca Séries através de uma categoria
    List<Serie> findByGenero(Categoria categoria);

    //Busca Séries com um número máximo de temporadas e uma avaliação mínima
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int numeroTemporadas, double avaliacao);

    //Mesma Busca anterior porém com linguagem JPQL
    @Query("select s from Serie s where s.totalTemporadas <= :numeroTemporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(int numeroTemporadas, double avaliacao);

    //Busca episódios pelo trecho do nome digitado
    @Query("SELECT e FROM Episodio e WHERE e.titulo ILIKE %:nomeEpisodio%")
    List<Episodio> episodiosPorNome(String nomeEpisodio);

    //Top 5 episódios de uma série
    @Query("SELECT e FROM Episodio e JOIN e.serie s WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    //Episódios lançados após ano digitado.
    @Query("SELECT e FROM Episodio e JOIN e.serie s WHERE s = :serie AND YEAR(e.dataLancamento) >= :ano")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int ano);
}
